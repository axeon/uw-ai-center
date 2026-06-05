package uw.ai.center.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RAG检索器.
 * 负责向量+BM25双路召回、Min-Max归一化、加权融合排序。
 * 从AiRagService中抽取，保持AiRagService专注于文档入库/删除等业务操作。
 */
public class AiRagSearcher {

    private static final Logger logger = LoggerFactory.getLogger(AiRagSearcher.class);

    /**
     * BM25全文搜索.
     * 利用ES索引中已有的text字段（类型为text，支持分词），通过ES match查询实现BM25检索。
     * BM25搜索失败时返回空列表，触发降级为纯向量检索，不影响核心功能。
     *
     * @param esClient ES高级客户端
     * @param indexName ES索引名（如 uw.ai.rag.123）
     * @param query 用户查询文本
     * @param k 返回数量（从libConfig的search.bm25.k读取，默认10）
     * @return BM25搜索命中列表
     */
    public static List<Bm25SearchHit> searchBm25(ElasticsearchClient esClient, String indexName, String query, int k) {
        try {
            // 使用ES高级客户端发起match查询，ES会对text字段做分词后计算BM25得分
            SearchResponse<Map> response = esClient.search(s -> s
                            .index(indexName)
                            .query(q -> q.match(m -> m.field("text").query(query)))
                            .size(k),
                    Map.class);
            List<Bm25SearchHit> results = new ArrayList<>();
            for (var hit : response.hits().hits()) {
                Map source = hit.source();
                if (source == null) {
                    continue;
                }
                // 提取chunk文本内容
                String text = (String) source.get("text");
                // 从metadata中提取UUID（ES文档的metadata.id存的是chunk的UUID）
                Map metadata = (Map) source.get("metadata");
                String id = metadata != null ? (String) metadata.get("id") : hit.id();
                results.add(new Bm25SearchHit(id, hit.score() != null ? hit.score() : 0.0, text));
            }
            // 调试日志：输出BM25搜索结果数量和每条的得分
            logger.info("BM25搜索完成, index={}, query={}, 返回{}条结果", indexName, query, results.size());
            for (int i = 0; i < results.size(); i++) {
                Bm25SearchHit r = results.get(i);
                // 截取前50字符避免日志过长
                String preview = r.text != null && r.text.length() > 50 ? r.text.substring(0, 50) + "..." : r.text;
                logger.info("  BM25[{}]: id={}, score={}, text={}", i, r.id, String.format("%.4f", r.score), preview);
            }
            return results;
        } catch (Exception e) {
            // BM25失败时降级：返回空列表，query()方法会退化为纯向量检索
            logger.error("BM25搜索失败, index={}, query={}", indexName, query, e);
            return List.of();
        }
    }

    /**
     * 合并向量检索和BM25检索结果，Min-Max归一化后加权融合.
     * 同一个chunk在两路都命中时，两路分数均计入；仅一路命中时，另一路分数为0。
     * 归一化公式：normalizedScore = (score - min) / (max - min)，max==min时设为1.0。
     *
     * @param vectorMatches 向量检索命中列表
     * @param bm25Hits BM25检索命中列表
     * @param vectorWeight 向量分数权重（从libConfig读取，默认0.7）
     * @param bm25Weight BM25分数权重（从libConfig读取，默认0.3）
     * @return 按融合得分降序排列的得分块列表
     */
    public static List<ScoredChunk> mergeAndFuse(
            List<EmbeddingMatch<TextSegment>> vectorMatches,
            List<Bm25SearchHit> bm25Hits,
            double vectorWeight,
            double bm25Weight) {

        // id -> [向量归一化分, BM25归一化分]
        Map<String, double[]> chunkMap = new LinkedHashMap<>();
        // id -> chunk文本
        Map<String, String> textMap = new HashMap<>();

        // 收集向量结果，Min-Max归一化
        if (!vectorMatches.isEmpty()) {
            // 计算向量分数的最小值和最大值，用于归一化
            double minVecScore = vectorMatches.stream().mapToDouble(EmbeddingMatch::score).min().orElse(0);
            double maxVecScore = vectorMatches.stream().mapToDouble(EmbeddingMatch::score).max().orElse(1);
            double vecRange = maxVecScore - minVecScore;

            for (EmbeddingMatch<TextSegment> match : vectorMatches) {
                String id = match.embedded().metadata().getString("id");
                // Min-Max归一化到[0,1]，max==min时全部设为1.0（所有分数相同）
                double normalized = vecRange > 0 ? (match.score() - minVecScore) / vecRange : 1.0;
                chunkMap.put(id, new double[]{normalized, 0.0});  // [向量分, BM25分暂为0]
                textMap.put(id, match.embedded().text());
            }
        }

        // 收集BM25结果，Min-Max归一化，合并到同一个Map
        if (!bm25Hits.isEmpty()) {
            // 计算BM25分数的最小值和最大值，用于归一化
            double minBm25Score = bm25Hits.stream().mapToDouble(Bm25SearchHit::score).min().orElse(0);
            double maxBm25Score = bm25Hits.stream().mapToDouble(Bm25SearchHit::score).max().orElse(1);
            double bm25Range = maxBm25Score - minBm25Score;

            for (Bm25SearchHit hit : bm25Hits) {
                double normalized = bm25Range > 0 ? (hit.score() - minBm25Score) / bm25Range : 1.0;
                if (chunkMap.containsKey(hit.id())) {
                    // 该chunk在向量结果中已存在，补充BM25分数
                    chunkMap.get(hit.id())[1] = normalized;
                } else {
                    // 该chunk仅在BM25中命中，向量分为0
                    chunkMap.put(hit.id(), new double[]{0.0, normalized});
                    textMap.put(hit.id(), hit.text());
                }
            }
        }

        // 加权融合：finalScore = vectorWeight * 向量分 + bm25Weight * BM25分
        List<ScoredChunk> result = chunkMap.entrySet().stream()
                .map(e -> new ScoredChunk(e.getKey(),
                        vectorWeight * e.getValue()[0] + bm25Weight * e.getValue()[1],
                        textMap.get(e.getKey())))
                .filter(c -> c.score > 0)  // 过滤掉两路归一化分都为0的无效结果
                .sorted()  // ScoredChunk实现了Comparable，按score降序排列
                .collect(Collectors.toList());

        // 调试日志：输出融合后的排序结果
        logger.info("双路融合完成, 向量{}条 + BM25{}条, 合并去重后{}条, 权重向量={}, BM25={}",
                vectorMatches.size(), bm25Hits.size(), result.size(),
                String.format("%.1f", vectorWeight), String.format("%.1f", bm25Weight));
        for (int i = 0; i < Math.min(result.size(), 10); i++) {
            ScoredChunk c = result.get(i);
            double[] scores = chunkMap.get(c.id);
            String preview = c.text != null && c.text.length() > 50 ? c.text.substring(0, 50) + "..." : c.text;
            logger.info("  融合[{}]: id={}, 融合分={}, 向量归一化={}, BM25归一化={}, text={}",
                    i, c.id, String.format("%.4f", c.score),
                    String.format("%.4f", scores[0]), String.format("%.4f", scores[1]), preview);
        }
        return result;
    }

    /**
     * BM25搜索命中结果.
     *
     * @param id chunk的UUID（来自metadata.id或ES文档_id）
     * @param score BM25得分（由ES match查询返回）
     * @param text chunk文本内容
     */
    public record Bm25SearchHit(String id, double score, String text) {
    }

    /**
     * 合并后的得分块，按融合得分降序排列.
     * 实现Comparable接口，用于sorted()排序。
     *
     * @param id chunk的UUID
     * @param score 融合得分 = vectorWeight * 向量归一化分 + bm25Weight * BM25归一化分（权重从libConfig读取）
     * @param text chunk文本内容
     */
    public record ScoredChunk(String id, double score, String text) implements Comparable<ScoredChunk> {
        @Override
        public int compareTo(ScoredChunk o) {
            // 按score降序排列（大的排在前面）
            return Double.compare(o.score, this.score);
        }
    }
}
