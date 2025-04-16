package uw.ai.center.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.swagger.v3.oas.annotations.media.Schema;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStoreOptions;
import org.springframework.ai.vectorstore.elasticsearch.SimilarityFunction;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uw.ai.center.entity.AiRagDoc;
import uw.ai.center.entity.AiRagLib;
import uw.ai.center.vendor.AiVendorClientWrapper;
import uw.ai.center.vendor.AiVendorHelper;
import uw.common.app.helper.JsonConfigHelper;
import uw.common.app.vo.JsonConfigBox;
import uw.common.app.vo.JsonConfigParam;
import uw.common.util.EnumUtils;
import uw.common.util.JsonUtils;
import uw.dao.DaoFactory;

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
    public static final List<JsonConfigParam> RAG_LIB_CONFIG_PARAMS = List.of( RagLibConfigParam.values() );
    /**
     * RAG库ES索引前缀.
     */
    public static final String RAG_ES_INDEX_PREFIX = "uw.ai.rag.";
    /**
     * 日志记录器.
     */
    private static final Logger logger = LoggerFactory.getLogger( AiRagService.class );
    /**
     * 数据库操作实例.
     */
    private static final DaoFactory dao = DaoFactory.getInstance();
    /**
     * RestClient实例.
     */
    private static RestClient restClient;
    /**
     * 实例缓存。
     */
    private static final LoadingCache<Long, AiRagClientWrapper> ragClientCache = Caffeine.newBuilder().maximumSize( 1000 ).build( new CacheLoader<Long, AiRagClientWrapper>() {
        @Override
        public AiRagService.AiRagClientWrapper load(Long libId) {
            return buildRagClientWrapper( libId );
        }
    } );

    private AiRagService(RestClient restClient) {
        AiRagService.restClient = restClient;
    }

    /**
     * 添加文档.
     *
     * @param ragLibId
     * @param docFile
     */
    public static Map<String, String> buildDocument(long ragLibId, MultipartFile docFile) {
        AiRagClientWrapper ragClientWrapper = getRagClientWrapper( ragLibId );
        try (InputStream inputStream = docFile.getInputStream()) {
            TikaDocumentReader reader = new TikaDocumentReader( new InputStreamResource( inputStream ) );
            List<Document> documentList = ragClientWrapper.textSplitter.apply( reader.get() );
            ragClientWrapper.vectorStore.add( documentList );
            return documentList.stream().collect( Collectors.toMap( Document::getId, Document::getText ) );
        } catch (IOException e) {
            logger.error( "处理文件[{}]时发生错误!{}", docFile.getOriginalFilename(), e.getMessage(), e );
        }
        return null;
    }

    /**
     * 添加文档.
     *
     * @param ragLibId
     * @param fileContent
     */
    public static Map<String, String> buildDocument(long ragLibId, String fileContent) {
        AiRagClientWrapper ragClientWrapper = getRagClientWrapper( ragLibId );
        List<Document> documentList = ragClientWrapper.textSplitter.apply( List.of( new Document( fileContent ) ) );
        ragClientWrapper.vectorStore.add( documentList );
        return documentList.stream().collect( Collectors.toMap( Document::getId, Document::getText ) );
    }

    /**
     * 删除文档.
     *
     * @param ragLibId
     */
    public static void deleteDocument(long ragLibId, AiRagDoc aiRagDoc) {
        if (aiRagDoc != null) {
            AiRagClientWrapper ragClientWrapper = getRagClientWrapper( ragLibId );
            Map<String, String> docMap = JsonUtils.parse( aiRagDoc.getDocContent(), new TypeReference<Map<String, String>>() {
            } );
            ragClientWrapper.vectorStore.delete( new ArrayList<>( docMap.keySet() ) );
        }
    }

    /**
     * 删除文档.
     *
     * @param ragLibId
     */
    public static void rebuildDocument(long ragLibId, AiRagDoc aiRagDoc) {
        if (aiRagDoc != null) {
            AiRagClientWrapper ragClientWrapper = getRagClientWrapper( ragLibId );
            Map<String, String> docMap = JsonUtils.parse( aiRagDoc.getDocContent(), new TypeReference<Map<String, String>>() {
            } );
//            ragClientWrapper.vectorStore.delete( new ArrayList<>( docMap.keySet() ) );
            List<Document> documentList = new ArrayList<>( docMap.size() );
            for (Map.Entry<String, String> entry : docMap.entrySet()) {
                documentList.add( new Document( entry.getKey(), entry.getValue(), Map.of() ) );
            }
            ragClientWrapper.vectorStore.add( documentList );
        }
    }

    /**
     * 删除RAG库.
     *
     * @param ragLibId
     */
    public static void deleteLib(long ragLibId) {
        AiRagClientWrapper ragClientWrapper = getRagClientWrapper( ragLibId );
        // 安全获取ElasticsearchClient并删除索引
        ragClientWrapper.vectorStore.getNativeClient().map( client -> (ElasticsearchClient) client ) // 转换类型
                .ifPresent( esClient -> {
                    try {
                        // 删除ES索引
                        esClient.indices().delete( r -> r.index( RAG_ES_INDEX_PREFIX + ragLibId ) );
                    } catch (Exception e) {
                        logger.error( "删除ES索引失败", e );
                    }
                } );
    }

    /**
     * 获取RAG客户端实例.
     *
     * @param ragLibId
     * @return
     */
    public static AiRagClientWrapper getRagClientWrapper(long ragLibId) {
        return ragClientCache.get( ragLibId );
    }

    /**
     * 查询.
     *
     * @param ragLibId
     * @param query
     * @return
     */
    public static String query(long ragLibId, String query) {
        AiRagClientWrapper ragClientWrapper = getRagClientWrapper( ragLibId );
        var searchRequestToUse = SearchRequest.from( ragClientWrapper.searchRequest ).query( query ).build();
        List<Document> documents = ragClientWrapper.vectorStore.similaritySearch( searchRequestToUse );
        StringBuilder sb = new StringBuilder( 1280 );
        sb.append( "来自知识库[" ).append( ragClientWrapper.aiRagLib.getLibName() ).append( "]检索的信息如下：\n" );
        for (Document document : documents) {
            sb.append( document.getText() ).append( "\n" );
        }
        sb.append( "\n" );
        return sb.toString();
    }

    /**
     * 构建RAG客户端.
     *
     * @param ragLibId
     * @return
     */
    private static AiRagClientWrapper buildRagClientWrapper(long ragLibId) {
        try {
            AiRagLib ragLib = dao.load( AiRagLib.class, ragLibId );
            if (ragLib != null) {
                String configData = ragLib.getLibConfig();
                JsonConfigBox configParamBox = JsonConfigHelper.buildParamBox( RAG_LIB_CONFIG_PARAMS, configData ).getData();
                int chunkSize = configParamBox.getIntParam( "chunk-size" );
                int chunkMinCharSize = configParamBox.getIntParam( "chunk-min-char-size" );
                int chunkMinEmbedSize = configParamBox.getIntParam( "chunk-min-embed-size" );
                int chunkMaxNum = configParamBox.getIntParam( "chunk-max-num" );
                double searchSimilarityThreshold = configParamBox.getDoubleParam( "search-similarity-threshold" );
                int searchTopK = configParamBox.getIntParam( "search-top-k" );
                TextSplitter textSplitter = new TokenTextSplitter( chunkSize, chunkMinCharSize, chunkMinEmbedSize, chunkMaxNum, true );
                SearchRequest searchRequest = SearchRequest.builder().topK( searchTopK ).similarityThreshold( searchSimilarityThreshold ).build();
                AiVendorClientWrapper aiVendorClientWrapper = AiVendorHelper.getChatClient( ragLib.getEmbedConfigId() );
                ElasticsearchVectorStoreOptions elasticsearchVectorStoreOptions = new ElasticsearchVectorStoreOptions();
                elasticsearchVectorStoreOptions.setIndexName( RAG_ES_INDEX_PREFIX + ragLibId );
                elasticsearchVectorStoreOptions.setDimensions( 1024 );
                elasticsearchVectorStoreOptions.setSimilarity( SimilarityFunction.cosine );
                ElasticsearchVectorStore vectorStore =
                        ElasticsearchVectorStore.builder( restClient, aiVendorClientWrapper.getEmbeddingModel() ).options( elasticsearchVectorStoreOptions ).initializeSchema( true ).build();
                vectorStore.afterPropertiesSet();
                return new AiRagClientWrapper( ragLib, vectorStore, textSplitter, searchRequest );
            }
        } catch (Exception e) {
            logger.error( e.getMessage(), e );
        }
        return null;
    }

    /**
     * RAG文档库配置.
     */
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Schema(title = "文档库参数", description = "文档库参数")
    public enum RagLibConfigParam implements JsonConfigParam {
        CHUNK_SIZE( JsonConfigParam.ParamType.INT, "800", "文本块大小", "文本块大小", null ),
        CHUNK_MIN_CHAR_SIZE( JsonConfigParam.ParamType.INT, "350", "文本块最小字符数", "文本块最小字符数", null ),
        CHUNK_MIN_EMBED_SIZE( JsonConfigParam.ParamType.INT, "5", "文本块embed最小长度", "文本块embed最小长度，低于这个长度将会不会embed。", null ),
        CHUNK_MAX_NUM( JsonConfigParam.ParamType.INT, "10000", "文本块最大数量", "文本块最大数量", null ),
        SEARCH_SIMILARITY_THRESHOLD( JsonConfigParam.ParamType.DOUBLE, "0.0", "搜索匹配下限", "搜索匹配下限，低于此下限值的将不会被使用", null ),
        SEARCH_TOP_K( JsonConfigParam.ParamType.INT, "4", "搜索topK", "搜索topK", null ),
        ;

        private final ParamData paramData;

        RagLibConfigParam(ParamType type, String value, String name, String desc, String regex) {
            this.paramData = new ParamData( EnumUtils.enumNameToDotCase( name() ), type, value, name, desc, regex );
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
     *
     * @param vectorStore
     * @param searchRequest
     */
    record AiRagClientWrapper(AiRagLib aiRagLib, VectorStore vectorStore, TextSplitter textSplitter, SearchRequest searchRequest) {
    }
}
