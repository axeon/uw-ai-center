package uw.ai.center.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import dev.langchain4j.data.document.Document;
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
import uw.ai.center.vendor.AiVendorHelper;
import uw.ai.center.vendor.client.EmbeddingClient;
import uw.common.app.constant.CommonState;
import uw.common.app.helper.JsonConfigHelper;
import uw.common.app.vo.JsonConfigBox;
import uw.common.app.vo.JsonConfigParam;
import uw.common.util.EnumUtils;
import uw.common.util.JsonUtils;
import uw.common.util.SystemClock;
import uw.dao.DaoManager;

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
     * 单个 RAG 文档大小上限：50MB。
     */
    private static final long MAX_RAG_DOC_SIZE = 50L * 1024 * 1024;
    /**
     * 允许的 RAG 文档扩展名白名单。
     */
    private static final java.util.Set<String> ALLOWED_RAG_DOC_EXTENSIONS = java.util.Set.of(
            "txt", "md", "csv", "log",
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "html", "htm", "xml", "json", "rtf");
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
     * <p>向量化或 ES 写入失败时，会尝试回滚已写入的 chunk，避免留下孤儿向量。
     *
     * @param ragLibId RAG库ID
     * @param docFile 上传的文档文件
     * @return Map<UUID, chunkText> 用于存入AiRagDoc.docContent；失败返回 null
     */
    public static Map<String, String> buildDocument(long ragLibId, MultipartFile docFile) {
        AiRagClientWrapper ragClientWrapper = getRagClientWrapper(ragLibId);
        if (ragClientWrapper == null) {
            logger.error("RAG客户端不存在, libId={}", ragLibId);
            return null;
        }
        if (docFile == null || docFile.isEmpty()) {
            logger.warn("RAG文档上传为空, libId={}", ragLibId);
            return null;
        }
        if (docFile.getSize() > MAX_RAG_DOC_SIZE) {
            logger.warn("RAG文档超过大小限制({} > {}), filename={}", docFile.getSize(), MAX_RAG_DOC_SIZE, docFile.getOriginalFilename());
            return null;
        }
        String filename = docFile.getOriginalFilename();
        if (filename == null || filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            logger.warn("RAG文档文件名非法, filename={}", filename);
            return null;
        }
        int dotIdx = filename == null ? -1 : filename.lastIndexOf('.');
        if (dotIdx < 0) {
            logger.warn("RAG文档缺少扩展名, filename={}", filename);
            return null;
        }
        String ext = filename.substring(dotIdx + 1).toLowerCase();
        if (!ALLOWED_RAG_DOC_EXTENSIONS.contains(ext)) {
            logger.warn("RAG文档类型不支持, filename={}, ext={}", filename, ext);
            return null;
        }
        List<TextSegment> segments = null;
        List<String> writtenIds = null;
        try (InputStream inputStream = docFile.getInputStream()) {
            // 使用Tika解析文档为纯文本
            ApacheTikaDocumentParser parser = new ApacheTikaDocumentParser();
            Document lc4jDoc = parser.parse(inputStream);
            // 使用AiDocumentSplitter分割（内含原生递归分割+UUID生成+最大数量限制）
            segments = ragClientWrapper.documentSplitter.split(lc4jDoc.text());
            List<Embedding> embeddings = ragClientWrapper.embeddingModel.embedAll(segments).content();
            // 使用metadata.id作为ES文档_id，使_id与metadata.id一致，这样removeAll才能按_id正确删除
            writtenIds = segments.stream().map(s -> s.metadata().getString("id")).toList();
            ragClientWrapper.vectorStore.addAll(writtenIds, embeddings, segments);
            return segments.stream().collect(Collectors.toMap(
                    s -> s.metadata().getString("id"),
                    TextSegment::text));
        } catch (Exception e) {
            // 向量化或 ES 写入失败：回滚已写入的 chunk，避免留下孤儿向量
            String errorMsg = e.getClass().getSimpleName() + ": " + e.getMessage();
            logger.error("处理文件[{}]失败，尝试回滚已写入chunk, written={}, err={}",
                    docFile.getOriginalFilename(),
                    writtenIds != null ? writtenIds.size() : 0, errorMsg, e);
            rollbackWrittenChunks(ragClientWrapper, writtenIds);
        }
        return null;
    }

    /**
     * 回滚已写入 ES 的 chunk：按 id 列表批量删除，避免留下孤儿向量。
     * 回滚本身失败仅记录日志，不影响主流程错误返回。
     *
     * @param ragClientWrapper RAG 客户端
     * @param ids              已写入 ES 的 chunk id 列表（可能为 null）
     */
    private static void rollbackWrittenChunks(AiRagClientWrapper ragClientWrapper, List<String> ids) {
        if (ragClientWrapper == null || ids == null || ids.isEmpty()) {
            return;
        }
        try {
            ragClientWrapper.vectorStore.removeAll(new ArrayList<>(ids));
        } catch (Exception rollbackErr) {
            logger.warn("回滚已写入chunk失败，可能需要人工清理ES, count={}", ids.size(), rollbackErr);
        }
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
        List<String> ids = null;
        try {
            List<Embedding> embeddings = ragClientWrapper.embeddingModel.embedAll(segments).content();
            // 使用metadata.id作为ES文档_id，使_id与metadata.id一致，这样removeAll才能按_id正确删除
            ids = segments.stream().map(s -> s.metadata().getString("id")).toList();
            ragClientWrapper.vectorStore.addAll(ids, embeddings, segments);
            return segments.stream().collect(Collectors.toMap(
                    s -> s.metadata().getString("id"),
                    TextSegment::text));
        } catch (Exception e) {
            logger.error("纯文本向量化或ES写入失败, libId={}, err={}", ragLibId, e.getMessage(), e);
            rollbackWrittenChunks(ragClientWrapper, ids);
            return null;
        }
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
     * <p>ES 操作异常或前置数据缺失时回滚 DB 状态为 DISABLED，避免出现"DB=ENABLED 但 ES 无向量"的不一致状态。
     * 回滚本身的异常仅记录日志，不向上抛出。
     *
     * @param ragDocId RAG文档ID
     */
    public static void rebuildDocument(long ragDocId) {
        dao.load(AiRagDoc.class, ragDocId).onSuccess(aiRagDoc -> {
            AiRagClientWrapper ragClientWrapper = getRagClientWrapper(aiRagDoc.getLibId());
            if (ragClientWrapper == null) {
                logger.error("RAG客户端不存在, libId={}, 回滚DB状态", aiRagDoc.getLibId());
                rollbackDocStateToDisabled(ragDocId);
                return;
            }
            Map<String, String> docMap = JsonUtils.parse(aiRagDoc.getDocContent(), new TypeReference<Map<String, String>>() {
            });
            if (docMap == null || docMap.isEmpty()) {
                logger.warn("文档内容为空, ragDocId={}, 回滚DB状态", ragDocId);
                rollbackDocStateToDisabled(ragDocId);
                return;
            }
            try {
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
            } catch (Exception e) {
                logger.error("重建文档向量失败, ragDocId={}, 回滚DB状态", ragDocId, e);
                rollbackDocStateToDisabled(ragDocId);
            }
        });
    }

    /**
     * 把文档 DB 状态从 ENABLED 回滚为 DISABLED，仅用于 rebuildDocument 失败时的状态补偿。
     * 回滚本身的异常仅记录日志，不向上抛出。
     *
     * @param ragDocId RAG文档ID
     */
    private static void rollbackDocStateToDisabled(long ragDocId) {
        try {
            dao.execute("update ai_rag_doc set state=?, modify_date=? where id=? and state=?",
                    new Object[]{CommonState.DISABLED.getValue(), SystemClock.nowDate(),
                            ragDocId, CommonState.ENABLED.getValue()});
        } catch (Exception ex) {
            logger.error("回滚文档状态失败, ragDocId={}", ragDocId, ex);
        }
    }

    /**
     * 删除RAG库：级联软删除 DB 中文档记录，再删除 ES 索引。
     * <p>顺序：先软删 DB（ai_rag_doc.state → DELETED），失效客户端缓存，再删 ES 索引。
     * ES 删除失败抛出异常，但 DB 软删已生效，避免留下孤儿 doc 记录指向已不存在 chunk。
     *
     * @param ragLibId RAG库ID
     */
    public static void deleteLib(long ragLibId) {
        // 1. 软删除该库下所有非删除态的文档记录，避免 RAG 库恢复后引用幽灵 chunk
        try {
            dao.execute("update ai_rag_doc set state=?, modify_date=? where lib_id=? and state<>?",
                    new Object[]{CommonState.DELETED.getValue(), SystemClock.nowDate(), ragLibId, CommonState.DELETED.getValue()});
        } catch (Exception e) {
            logger.error("软删除RAG文档记录失败, ragLibId={}", ragLibId, e);
            throw new RuntimeException("软删除RAG文档记录失败: " + ragLibId, e);
        }
        // 2. 失效该 RAG 库的客户端缓存（释放 EmbeddingModel 引用等）
        invalidateRagClientCache(ragLibId);
        // 3. 删除ES索引
        try {
            esClient.indices().delete(d -> d.index(RAG_ES_INDEX_PREFIX + ragLibId));
        } catch (Exception e) {
            logger.error("删除ES索引失败, ragLibId={}", ragLibId, e);
            throw new RuntimeException("删除ES索引失败: " + ragLibId, e);
        }
    }

    /**
     * 按 chunk id 集合批量删除 ES 向量数据.
     * <p>用于文档入库后 DB 写入失败的补偿回滚，避免 ES 中留下孤儿 chunk。
     * 回滚本身的异常仅记录日志，不向上抛出。
     *
     * @param ragLibId RAG库ID
     * @param chunkIds 要删除的 chunk id 集合
     */
    public static void rollbackChunks(long ragLibId, java.util.Collection<String> chunkIds) {
        if (chunkIds == null || chunkIds.isEmpty()) {
            return;
        }
        AiRagClientWrapper ragClientWrapper = getRagClientWrapper(ragLibId);
        if (ragClientWrapper == null) {
            logger.warn("回滚chunk失败：RAG客户端不存在, libId={}", ragLibId);
            return;
        }
        rollbackWrittenChunks(ragClientWrapper, new ArrayList<>(chunkIds));
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
        long startMs = SystemClock.now();
        String shortQuery = truncate(query, 100);
        AiRagClientWrapper ragClientWrapper = getRagClientWrapper(ragLibId);
        if (ragClientWrapper == null) {
            logger.error("RAG客户端不存在, libId={}", ragLibId);
            return "";
        }
        logger.info("RAG查询开始, libId={}, libName={}, query=[{}]", ragLibId, ragClientWrapper.aiRagLib.getLibName(), shortQuery);
        // 向量检索
        long t1 = SystemClock.now();
        Embedding queryEmbedding = ragClientWrapper.embeddingModel.embed(query).content();
        EmbeddingSearchRequest vectorRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(ragClientWrapper.searchVectorK)
                .minScore(ragClientWrapper.searchSimilarityThreshold)
                .build();
        EmbeddingSearchResult<TextSegment> vectorResult = ragClientWrapper.vectorStore.search(vectorRequest);
        long vectorMs = SystemClock.now() - t1;

        // BM25全文检索
        long t2 = SystemClock.now();
        List<AiRagSearcher.Bm25SearchHit> bm25Results =
                AiRagSearcher.searchBm25(esClient, RAG_ES_INDEX_PREFIX + ragLibId, query, ragClientWrapper.searchBm25K);
        long bm25Ms = SystemClock.now() - t2;

        // 合并去重 + 加权融合（权重从libConfig读取）
        List<AiRagSearcher.ScoredChunk> merged = AiRagSearcher.mergeAndFuse(
                vectorResult.matches(), bm25Results,
                ragClientWrapper.searchVectorWeight, ragClientWrapper.searchBm25Weight);
        int topK = Math.min(ragClientWrapper.searchTopK, merged.size());

        // 取最终 TopK，拼接文本
        StringBuilder sb = new StringBuilder(1280);
        sb.append("来自知识库[").append(ragClientWrapper.aiRagLib.getLibName()).append("]检索的信息如下：\n");
        // 按 searchTopK（从libConfig读取，默认4）截取最终结果
        merged.stream().limit(ragClientWrapper.searchTopK).forEach(chunk ->
                sb.append(chunk.text()).append("\n"));
        sb.append("\n");
        logger.info("RAG查询完成, libId={}, vectorHits={}, bm25Hits={}, fused={}, topK={}, resultLen={}, vectorMs={}, bm25Ms={}, totalMs={}",
                ragLibId, vectorResult.matches().size(), bm25Results.size(), merged.size(), topK, sb.length(), vectorMs, bm25Ms, SystemClock.now() - startMs);
        return sb.toString();
    }

    /**
     * 截断字符串到指定长度（超出补省略号），用于日志输出避免 query 过长刷屏。
     */
    private static String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
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

        EmbeddingClient embeddingClient;
        try {
            embeddingClient = AiVendorHelper.getEmbeddingClient(ragLib.getEmbedConfigId());
        } catch (IllegalStateException e) {
            logger.error("RAG库[{}]获取EmbeddingModel失败: {}", ragLibId, e.getMessage());
            return null;
        }
        EmbeddingModel embeddingModel = embeddingClient.getEmbeddingModel();

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
