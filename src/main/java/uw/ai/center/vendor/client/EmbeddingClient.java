package uw.ai.center.vendor.client;

import dev.langchain4j.model.embedding.EmbeddingModel;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.ai.center.constant.ModelType;
import uw.ai.center.vo.AiModelConfigData;

/**
 * EMBEDDING 类型客户端：嵌入模型，用于 RAG 文档向量化与查询向量化。
 */
@Schema(title = "Embedding客户端", description = "嵌入模型客户端")
public class EmbeddingClient extends AiModelClient {

    @Schema(title = "嵌入模型", description = "嵌入模型")
    private final EmbeddingModel embeddingModel;

    public EmbeddingClient(AiModelConfigData configData, EmbeddingModel embeddingModel) {
        super(configData, ModelType.EMBEDDING);
        this.embeddingModel = embeddingModel;
    }

    public EmbeddingModel getEmbeddingModel() {
        return embeddingModel;
    }

    @Override
    protected void doClose() {
        closeQuietly(embeddingModel);
    }
}
