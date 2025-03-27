package uw.ai.center.service;

import org.elasticsearch.client.RestClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.stereotype.Service;
import uw.common.constant.TypeConfigParam;
import uw.common.vo.ConfigParam;

import java.util.List;

/**
 * RAG库服务.
 */
@Service
public class AiRagService {

    /**
     * RAG库配置参数.
     */
    public static final List<ConfigParam> RAG_LIB_CONFIG_PARAMS = List.of(
            new ConfigParam( "chunk-size", "800", TypeConfigParam.INT.getValue(), "文本块大小", "文本块大小" ),
            new ConfigParam( "chunk-min-char-size", "350", TypeConfigParam.INT.getValue(), "文本块最小字符数", "文本块大小" ),
            new ConfigParam( "chunk-min-embed-size", "5", TypeConfigParam.INT.getValue(), "文本块embed最小长度", "文本块embed最小长度，低于这个长度将会不会embed。" ),
            new ConfigParam( "chunk-max-num", "10000", TypeConfigParam.INT.getValue(), "文本块最大数量", "文本块最大数量" ),
            new ConfigParam( "search-similarity-threshold", "0.0", TypeConfigParam.DOUBLE.getValue(), "搜索匹配下限", "搜索匹配下限，低于此下限值的将不会被使用" ),
            new ConfigParam( "search-top-k", "5", TypeConfigParam.INT.getValue(), "搜索topK", "搜索topK" )
    );
    /**
     * RAG库ES索引前缀.
     */
    public static final String RAG_ES_INDEX_PREFIX = "uw.ai.rag.";

    private static RestClient restClient;

    public AiRagService(RestClient restClient) {
        AiRagService.restClient = restClient;
    }

    private static VectorStore buildVectorStore(long ragLibId) {
        VectorStore vectorStore = ElasticsearchVectorStore.builder( restClient,null ).build();
        return vectorStore;
    }

}
