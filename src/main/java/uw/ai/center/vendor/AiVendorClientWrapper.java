package uw.ai.center.vendor;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import uw.ai.center.vo.AiModelConfigData;

/**
 * 聊天客户端包装类。
 *
 */
@Schema(title = "AiVendor客户端封装类", description = "AiVendor客户端封装类")
public class AiVendorClientWrapper {

    /**
     * 配置数据.
     */
    @Schema(title = "配置数据", description = "配置数据" )
    private final AiModelConfigData configData;

    /**
     * 聊天客户端.
     */
    @Schema(title = "聊天客户端", description = "聊天客户端" )
    private final ChatClient chatClient;

    /**
     * 嵌入模型.
     */
    @Schema(title = "嵌入模型", description = "嵌入模型" )
    private final EmbeddingModel embeddingModel;

    public AiVendorClientWrapper(AiModelConfigData configData, ChatClient chatClient, EmbeddingModel embeddingModel) {
        this.configData = configData;
        this.chatClient = chatClient;
        this.embeddingModel = embeddingModel;
    }

    public AiModelConfigData getConfigData() {
        return configData;
    }

    public ChatClient getChatClient() {
        return chatClient;
    }

    public EmbeddingModel getEmbeddingModel() {
        return embeddingModel;
    }
}
