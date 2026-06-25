package uw.ai.center.vendor.dashscope.rerank;

import uw.ai.center.vendor.dashscope.DashScopeApiClient;
import uw.ai.center.vo.RerankResult;

import java.util.List;
import java.util.Map;

/**
 * DashScope 重排模型实现（qwen3-rerank）
 *
 */
public class DashScopeRerankModel implements RerankModel {

    private final String baseUrl;
    private final String apiKey;
    private final String modelName;
    private final Map<String, Object> defaultParams;

    public DashScopeRerankModel(String baseUrl, String apiKey, String modelName, Map<String, Object> defaultParams) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.defaultParams = defaultParams != null ? defaultParams : Map.of();
    }

    @Override
    public RerankResult rerank(String query, List<String> documents) {
        return DashScopeApiClient.rerank(baseUrl, apiKey, modelName, query, documents, defaultParams);
    }

    /**
     * 空实现：HTTP 调用通过 uw-base 共享的 OkHttp 客户端池化管理，本实例无独立资源需要释放。
     * <p>实现 AutoCloseable 是为了与项目其他 Model 接口（RealtimeTranscriptionModel 等）保持一致，
     * 让 RerankClient 可统一用 closeQuietly 级联关闭。
     */
    @Override
    public void close() {
        // no-op
    }
}
