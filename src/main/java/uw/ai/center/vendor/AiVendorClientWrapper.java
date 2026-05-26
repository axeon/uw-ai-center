package uw.ai.center.vendor;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.ai.center.vo.AiModelConfigData;

/**
 * AI供应商客户端封装类（LangChain4j）。
 */
@Schema(title = "AI客户端封装类", description = "AI客户端封装类")
public class AiVendorClientWrapper {

    @Schema(title = "配置数据", description = "配置数据")
    private final AiModelConfigData configData;

    @Schema(title = "同步聊天模型", description = "同步聊天模型")
    private final ChatLanguageModel chatLanguageModel;

    @Schema(title = "流式聊天模型", description = "流式聊天模型")
    private final StreamingChatLanguageModel streamingChatLanguageModel;

    @Schema(title = "嵌入模型", description = "嵌入模型")
    private final EmbeddingModel embeddingModel;

    public AiVendorClientWrapper(AiModelConfigData configData,
                                 ChatLanguageModel chatLanguageModel,
                                 StreamingChatLanguageModel streamingChatLanguageModel,
                                 EmbeddingModel embeddingModel) {
        this.configData = configData;
        this.chatLanguageModel = chatLanguageModel;
        this.streamingChatLanguageModel = streamingChatLanguageModel;
        this.embeddingModel = embeddingModel;
    }

    public AiModelConfigData getConfigData() {
        return configData;
    }

    public ChatLanguageModel getChatLanguageModel() {
        return chatLanguageModel;
    }

    public StreamingChatLanguageModel getStreamingChatLanguageModel() {
        return streamingChatLanguageModel;
    }

    public EmbeddingModel getEmbeddingModel() {
        return embeddingModel;
    }
}
