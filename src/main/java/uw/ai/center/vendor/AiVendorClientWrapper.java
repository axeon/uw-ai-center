package uw.ai.center.vendor;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.ai.center.constant.ModelType;
import uw.ai.center.vo.AiModelConfigData;

/**
 * AI供应商客户端封装类（LangChain4j）。
 * 按modelType构建：CHAT类型提供ChatModel+StreamingChatModel，EMBEDDING类型提供EmbeddingModel。
 */
@Schema(title = "AI客户端封装类", description = "AI客户端封装类")
public class AiVendorClientWrapper {

    @Schema(title = "配置数据", description = "配置数据")
    private final AiModelConfigData configData;

    @Schema(title = "同步聊天模型", description = "同步聊天模型，CHAT类型时可用")
    private final ChatModel chatModel;

    @Schema(title = "流式聊天模型", description = "流式聊天模型，CHAT类型时可用")
    private final StreamingChatModel streamingChatModel;

    @Schema(title = "嵌入模型", description = "嵌入模型，EMBEDDING类型时可用")
    private final EmbeddingModel embeddingModel;

    public AiVendorClientWrapper(AiModelConfigData configData,
                                 ChatModel chatModel,
                                 StreamingChatModel streamingChatModel,
                                 EmbeddingModel embeddingModel) {
        this.configData = configData;
        this.chatModel = chatModel;
        this.streamingChatModel = streamingChatModel;
        this.embeddingModel = embeddingModel;
    }

    public AiModelConfigData getConfigData() {
        return configData;
    }

    public ChatModel getChatModel() {
        return chatModel;
    }

    public StreamingChatModel getStreamingChatModel() {
        return streamingChatModel;
    }

    public EmbeddingModel getEmbeddingModel() {
        return embeddingModel;
    }

    /**
     * 获取模型类型。
     */
    public ModelType getModelType() {
        return ModelType.of(configData.getModelType());
    }

    /**
     * 是否为指定模型类型。
     */
    public boolean isType(ModelType modelType) {
        return modelType == getModelType();
    }
}
