package uw.ai.center.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uw.ai.center.entity.AiRagDoc;
import uw.ai.center.entity.AiRagLib;
import uw.ai.center.util.AiDocumentSplitter;
import uw.ai.center.vendor.AiVendorClientWrapper;
import uw.ai.center.vendor.AiVendorHelper;
import uw.ai.center.constant.ModelType;
import uw.common.app.helper.JsonConfigHelper;
import uw.common.app.vo.JsonConfigBox;
import uw.common.app.vo.JsonConfigParam;
import uw.common.util.EnumUtils;
import uw.common.util.JsonUtils;
import uw.dao.DaoManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RAG库服务.
 */
@Service
public class AiRagService {

    /**
     * RAG库配置参数.
     */
    public static final List<JsonConfigParam> RAG_LIB_CONFIG_PARAMS = List.of(RagLibConfigParam.values());
    /**
     * RAG库ES索引前缀.
     */
    public static final String RAG_ES_INDEX_PREFIX = "uw.ai.rag.";
    /**
     * 日志记录器.
     */
    private static final Logger logger = LoggerFactory.getLogger(AiRagService.class);
    /**
     * 数据库操作实例.
     */
    private static final DaoManager dao = DaoManager.getInstance();
    /**
     * ElasticsearchClient实例（替代原RestClient，使用ES官方高级客户端API）.
     */
    private static ElasticsearchClient esClient;
    /**
     * 实例缓存。
     */
    private static final LoadingCache<Long, AiRagClientWrapper> ragClientCache = Caffeine.newBuilder().maximumSize(1000).build(new CacheLoader<Long, AiRagClientWrapper>() {
        @Override
        public AiRagService.AiRagClientWrapper load(Long libId) {
            return buildRagClientWrapper(libId);
        }
    });

    private AiRagService(ElasticsearchClient esClient) {
        AiRagService.esClient = esClient;
    }

    /**
     * 添加文档（文件上传方式）.
     * 使用Tika解析文件，再用AiDocumentSplitter分割为带UUID的TextSegment列表。
     *
     * @param ragLibId RAG库ID
     * @param docFile 上传的文档文件
     * @return Map<UUID, chunkText> 用于存入AiRagDoc.docContent
     */
    public static Map<String, String> buildDocument(long ragLibId, MultipartFile docFile) {
        AiRagClientWrapper ragClientWrapper = getRagClientWrapper(ragLibId);
        if (ragClientWrapper == null) {
            logger.error("RAG客户端不存在, libId={}", ragLibId);
            return null;
        }
        try (InputStream inputStream = docFile.getInputStream()) {
            // 使用Tika解析文档为纯文本
            ApacheTikaDocumentParser parser = new ApacheTikaDocumentParser();
            dev.langchain4j.data.document.Document lc4jDoc = parser.parse(inputStream);
            // 使用AiDocumentSplitter分割（内含原生递归分割+UUID生成+最大数量限制）
            List<TextSegment> segments = ragClientWrapper.documentSplitter.split(lc4jDoc.text());
            List<Embedding> embeddings = ragClientWrapper.embeddingModel.embedAll(segments).content();
            // 使用metadata.id作为ES文档_id，使_id与metadata.id一致，这样removeAll才能按_id正确删除
            List<String> ids = segments.stream().map(s -> s.metadata().getString("id")).toList();
            ragClientWrapper.vectorStore.addAll(ids, embeddings, segments);
            return segments.stream().collect(Collectors.toMap(
                    s -> s.metadata().getString("id"),
                    TextSegment::text));
        } catch (IOException e) {
            logger.error("处理文件[{}]时发生错误!{}", docFile.getOriginalFilename(), e.getMessage(), e);
        }
        return null;
    }

    /**
     * 添加文档（纯文本方式）.
     * 使用AiDocumentSplitter分割为带UUID的TextSegment列表。
     *
     * @param ragLibId RAG库ID
     * @param fileContent 纯文本内容
     * @return Map<UUID, chunkText> 用于存入AiRagDoc.docContent
     */
    public static Map<String, String> buildDocument(long ragLibId, String fileContent) {
        AiRagClientWrapper ragClientWrapper = getRagClientWrapper(ragLibId);
        if (ragClientWrapper == null) {
            logger.error("RAG客户端不存在, libId={}", ragLibId);
            return null;
        }
        // 使用AiDocumentSplitter分割（内含原生递归分割+UUID生成+最大数量限制）
        List<TextSegment> segments = ragClientWrapper.documentSplitter.split(fileContent);
        List<Embedding> embeddings = ragClientWrapper.embeddingModel.embedAll(segments).content();
        // 使用metadata.id作为ES文档_id，使_id与metadata.id一致，这样removeAll才能按_id正确删除
        List<String> ids = segments.stream().map(s -> s.metadata().getString("id")).toList();
        ragClientWrapper.vectorStore.addAll(ids, embeddings, segments);
        return segments.stream().collect(Collectors.toMap(
                s -> s.metadata().getString("id"),
                TextSegment::text));
    }

    /**
     * 删除文档向量.
     *
     * @param ragDocId
     */
    public static void deleteDocument(long ragDocId) {
        dao.load(AiRagDoc.class, ragDocId).onSuccess(aiRagDoc -> {
            AiRagClientWrapper ragClientWrapper = getRagClientWrapper(aiRagDoc.getLibId());
            if (ragClientWrapper == null) {
                logger.error("RAG客户端不存在, libId={}", aiRagDoc.getLibId());
                return;
            }
            Map<String, String> docMap = JsonUtils.parse(aiRagDoc.getDocContent(), new TypeReference<Map<String, String>>() {
            });
            if (docMap == null || docMap.isEmpty()) {
                logger.warn("文档内容为空, ragDocId={}", ragDocId);
                return;
            }
            ragClientWrapper.vectorStore.removeAll(new ArrayList<>(docMap.keySet()));
        });
    }

    /**
     * 重建文档（先删旧数据再写入，避免ES中出现重复chunk）.
     *
     * @param ragDocId RAG文档ID
     */
    public static void rebuildDocument(long ragDocId) {
        dao.load(AiRagDoc.class, ragDocId).onSuccess(aiRagDoc -> {
            AiRagClientWrapper ragClientWrapper = getRagClientWrapper(aiRagDoc.getLibId());
            if (ragClientWrapper == null) {
                logger.error("RAG客户端不存在, libId={}", aiRagDoc.getLibId());
                return;
            }
            Map<String, String> docMap = JsonUtils.parse(aiRagDoc.getDocContent(), new TypeReference<Map<String, String>>() {
            });
            if (docMap == null || docMap.isEmpty()) {
                logger.warn("文档内容为空, ragDocId={}", ragDocId);
                return;
            }
            // 先删除旧的chunk数据，再写入新的，避免重复
            ragClientWrapper.vectorStore.removeAll(new ArrayList<>(docMap.keySet()));
            List<TextSegment> segments = new ArrayList<>(docMap.size());
            for (Map.Entry<String, String> entry : docMap.entrySet()) {
                Metadata metadata = new Metadata();
                metadata.put("id", entry.getKey());
                segments.add(TextSegment.from(entry.getValue(), metadata));
            }
            List<Embedding> embeddings = ragClientWrapper.embeddingModel.embedAll(segments).content();
            // 使用metadata.id作为ES文档_id，使_id与metadata.id一致，这样removeAll才能按_id正确删除
            List<String> ids = segments.stream().map(s -> s.metadata().getString("id")).toList();
            ragClientWrapper.vectorStore.addAll(ids, embeddings, segments);
        });
    }

    /**
     * 删除RAG库.
     *
     * @param ragLibId RAG库ID
     */
    public static void deleteLib(long ragLibId) {
        try {
            esClient.indices().delete(d -> d.index(RAG_ES_INDEX_PREFIX + ragLibId));
        } catch (Exception e) {
            logger.error("删除ES索引失败, ragLibId={}", ragLibId, e);
            throw new RuntimeException("删除ES索引失败: " + ragLibId, e);
        }
    }

    /**
     * 获取RAG客户端实例.
     *
     * @param ragLibId
     * @return
     */
    public static AiRagClientWrapper getRagClientWrapper(long ragLibId) {
        return ragClientCache.get(ragLibId);
    }

    /**
     * 失效RAG客户端缓存.
     * 在RAG库配置（libConfig/embedConfigId等）变更时调用，
     * 使下次请求重新构建AiRagClientWrapper，读取最新配置。
     *
     * @param libId RAG库ID
     */
    public static void invalidateRagClientCache(long libId) {
        ragClientCache.invalidate(libId);
    }

    /**
     * 查询（双路召回 + 加权融合）.
     * 第一路：向量检索（ES KNN搜索），召回数量由libConfig的search.vector.k控制（默认10）
     * 第二路：BM25全文检索（ES match查询），召回数量由libConfig的search.bm25.k控制（默认10）
     * 合并去重后，对两路分数做Min-Max归一化，按权重加权融合排序（权重由libConfig的search.vector.weight和search.bm25.weight控制，默认0.7和0.3），取最终TopK。
     * 若BM25搜索失败则降级为纯向量检索。
     *
     * @param ragLibId RAG库ID
     * @param query 用户查询文本
     * @return 拼接的检索结果文本
     */
    public static String query(long ragLibId, String query) {
        AiRagClientWrapper ragClientWrapper = getRagClientWrapper(ragLibId);
        if (ragClientWrapper == null) {
            logger.error("RAG客户端不存在, libId={}", ragLibId);
            return "";
        }
        // 向量检索
        Embedding queryEmbedding = ragClientWrapper.embeddingModel.embed(query).content();
        EmbeddingSearchRequest vectorRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(ragClientWrapper.searchVectorK)
                .minScore(ragClientWrapper.searchSimilarityThreshold)
                .build();
        EmbeddingSearchResult<TextSegment> vectorResult = ragClientWrapper.vectorStore.search(vectorRequest);

        // BM25全文检索
        List<AiRagSearcher.Bm25SearchHit> bm25Results =
                AiRagSearcher.searchBm25(esClient, RAG_ES_INDEX_PREFIX + ragLibId, query, ragClientWrapper.searchBm25K);

        // 合并去重 + 加权融合（权重从libConfig读取）
        List<AiRagSearcher.ScoredChunk> merged = AiRagSearcher.mergeAndFuse(
                vectorResult.matches(), bm25Results,
                ragClientWrapper.searchVectorWeight, ragClientWrapper.searchBm25Weight);

        // 取最终 TopK，拼接文本
        StringBuilder sb = new StringBuilder(1280);
        sb.append("来自知识库[").append(ragClientWrapper.aiRagLib.getLibName()).append("]检索的信息如下：\n");
        // 按 searchTopK（从libConfig读取，默认4）截取最终结果
        merged.stream().limit(ragClientWrapper.searchTopK).forEach(chunk ->
                sb.append(chunk.text()).append("\n"));
        sb.append("\n");
        return sb.toString();
    }

    /**
     * 构建RAG客户端.
     *
     * @param ragLibId
     * @return
     */
    private static AiRagClientWrapper buildRagClientWrapper(long ragLibId) {
        AiRagLib ragLib = dao.load(AiRagLib.class, ragLibId).getData();
        if (ragLib == null) {
            return null;
        }
        String configData = ragLib.getLibConfig();
        JsonConfigBox configParamBox = JsonConfigHelper.buildParamBox(RAG_LIB_CONFIG_PARAMS, configData).getData();
        int chunkSize = configParamBox.getIntParam(RagLibConfigParam.CHUNK_SIZE);
        int chunkMaxNum = configParamBox.getIntParam(RagLibConfigParam.CHUNK_MAX_NUM);
        double searchSimilarityThreshold = configParamBox.getDoubleParam(RagLibConfigParam.SEARCH_SIMILARITY_THRESHOLD);
        int searchTopK = configParamBox.getIntParam(RagLibConfigParam.SEARCH_TOP_K);
        // 双路召回参数：从libConfig读取，未配置时使用枚举默认值
        int searchVectorK = configParamBox.getIntParam(RagLibConfigParam.SEARCH_VECTOR_K);
        int searchBm25K = configParamBox.getIntParam(RagLibConfigParam.SEARCH_BM25_K);
        double searchVectorWeight = configParamBox.getDoubleParam(RagLibConfigParam.SEARCH_VECTOR_WEIGHT);
        double searchBm25Weight = configParamBox.getDoubleParam(RagLibConfigParam.SEARCH_BM25_WEIGHT);
        // 使用AiDocumentSplitter封装原生递归分割器，内含UUID生成和最大数量限制
        // 递归层级：段落(\n\n) → 行(\n) → 句子 → 词 → 字符，超长段自动降级到子分割器
        // overlap为chunkSize的10%（如chunkSize=800则overlap=80），提供跨chunk边界上下文
        AiDocumentSplitter documentSplitter = new AiDocumentSplitter(chunkSize, chunkSize / 10, chunkMaxNum);

        AiVendorClientWrapper vendorWrapper;
        try {
            vendorWrapper = AiVendorHelper.getClientWrapper(ragLib.getEmbedConfigId());
        } catch (IllegalStateException e) {
            logger.error("RAG库[{}]获取EmbeddingModel失败: {}", ragLibId, e.getMessage());
            return null;
        }
        if (!vendorWrapper.isType(ModelType.EMBEDDING)) {
            logger.error("RAG库[{}]获取EmbeddingModel失败: 配置不是EMBEDDING类型", ragLibId);
            return null;
        }
        dev.langchain4j.model.embedding.EmbeddingModel embeddingModel = vendorWrapper.getEmbeddingModel();

        ElasticsearchEmbeddingStore vectorStore = ElasticsearchEmbeddingStore.builder()
                .client(esClient)
                .indexName(RAG_ES_INDEX_PREFIX + ragLibId)
                .build();

        return new AiRagClientWrapper(ragLib, vectorStore, documentSplitter, searchTopK,
                searchSimilarityThreshold, searchVectorK, searchBm25K, searchVectorWeight, searchBm25Weight,
                embeddingModel);
    }

    /**
     * RAG文档库配置.
     */
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Schema(title = "文档库参数", description = "文档库参数")
    public enum RagLibConfigParam implements JsonConfigParam {
        CHUNK_SIZE(JsonConfigParam.ParamType.INT, "800", "文本块大小", null),
        CHUNK_MAX_NUM(JsonConfigParam.ParamType.INT, "10000", "文本块最大数量", null),
        SEARCH_SIMILARITY_THRESHOLD(JsonConfigParam.ParamType.DOUBLE, "0.0", "搜索匹配下限，低于此下限值的将不会被使用", null),
        SEARCH_TOP_K(JsonConfigParam.ParamType.INT, "4", "最终返回结果数量（从融合排序结果中取TopK）", null),
        SEARCH_VECTOR_K(JsonConfigParam.ParamType.INT, "10", "向量召回数量", null),
        SEARCH_BM25_K(JsonConfigParam.ParamType.INT, "10", "BM25召回数量", null),
        SEARCH_VECTOR_WEIGHT(JsonConfigParam.ParamType.DOUBLE, "0.7", "向量分数权重", null),
        SEARCH_BM25_WEIGHT(JsonConfigParam.ParamType.DOUBLE, "0.3", "BM25分数权重", null),
        ;

        private final ParamData paramData;

        RagLibConfigParam(ParamType type, String value, String desc, String regex) {
            this.paramData = new ParamData(EnumUtils.enumNameToDotCase(name()), type, value, desc, regex);
        }

        /**
         * 配置参数数据。
         *
         * @return
         */
        @Override
        public ParamData getParamData() {
            return paramData;
        }

    }

    /**
     * RAG实例封装类.
     * 封装每个RAG库的向量存储、分割器、搜索参数和嵌入模型。
     *
     * @param aiRagLib RAG库配置实体
     * @param vectorStore ES向量存储
     * @param documentSplitter AiDocumentSplitter分割器（封装原生递归分割+UUID生成+最大数量限制）
     * @param searchTopK 搜索返回数量（从libConfig读取，默认4）
     * @param searchSimilarityThreshold 搜索相似度阈值（从libConfig读取，默认0.0）
     * @param searchVectorK 向量召回数量（从libConfig读取，默认10）
     * @param searchBm25K BM25召回数量（从libConfig读取，默认10）
     * @param searchVectorWeight 向量分数权重（从libConfig读取，默认0.7）
     * @param searchBm25Weight BM25分数权重（从libConfig读取，默认0.3）
     * @param embeddingModel 嵌入模型
     */
    record AiRagClientWrapper(AiRagLib aiRagLib,
                              ElasticsearchEmbeddingStore vectorStore,
                              AiDocumentSplitter documentSplitter,
                              int searchTopK,
                              double searchSimilarityThreshold,
                              int searchVectorK,
                              int searchBm25K,
                              double searchVectorWeight,
                              double searchBm25Weight,
                              EmbeddingModel embeddingModel) {
    }
}
