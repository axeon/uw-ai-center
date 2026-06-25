package uw.ai.center.vendor.client;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.ai.center.constant.ModelType;
import uw.ai.center.vendor.dashscope.rerank.RerankModel;
import uw.ai.center.vo.AiModelConfigData;
import uw.ai.center.vo.RerankResult;

import java.util.List;

/**
 * RERANK 类型客户端：重排模型，对召回结果做二次精准排序。
 * <p>持有 {@link RerankModel} 实例（DashScope 特有接口，地位对齐 LangChain4j 的
 * {@code ChatModel} / {@code ImageModel}），由 vendor 注入具体实现
 * （如 {@code DashScopeRerankModel}）。
 */
@Schema(title = "重排客户端", description = "重排模型客户端")
public class RerankClient extends AiModelClient {

    /**
     * 重排模型实例。
     */
    private final RerankModel rerankModel;

    public RerankClient(AiModelConfigData configData, RerankModel rerankModel) {
        super(configData, ModelType.RERANK);
        this.rerankModel = rerankModel;
    }

    /**
     * 调用重排模型，对 documents 按与 query 的语义相关性重新排序。
     * <p>所有重排参数（topN/instruct/returnDocuments）走 model_data 配置的默认值。
     *
     * @param query     查询文本
     * @param documents 候选文档列表
     * @return 重排结果（已按 relevanceScore 降序）
     */
    public RerankResult rerank(String query, List<String> documents) {
        return rerankModel.rerank(query, documents);
    }

    @Override
    protected void doClose() {
        closeQuietly(rerankModel);
    }
}
