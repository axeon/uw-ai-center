package uw.ai.center.vendor;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.ai.center.constant.ModelType;
import uw.ai.center.vo.AiModelConfigData;

/**
 * AI供应商客户端封装类（LangChain4j）。
 * 按modelType构建：CHAT类型提供ChatModel+StreamingChatModel，EMBEDDING类型提供EmbeddingModel。
 * 实现AutoCloseable，在缓存失效时主动释放底层HTTP连接池等资源。
 */
@Schema(title = "AI客户端封装类", description = "AI客户端封装类")
public class AiVendorClientWrapper implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(AiVendorClientWrapper.class);

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

    /**
     * 释放底层资源（HTTP连接池等）。
     * 在缓存失效时由AiVendorHelper.invalidateClientWrapper调用。
     */
    @Override
    public void close() {
        try {
            if (chatModel instanceof AutoCloseable closeable) {
                closeable.close();
            }
        } catch (Exception e) {
            // 日志记录但不抛出，避免影响其他资源的释放
            logger.warn("关闭ChatModel资源失败", e);
        }
        try {
            if (streamingChatModel instanceof AutoCloseable closeable) {
                closeable.close();
            }
        } catch (Exception e) {
            logger.warn("关闭StreamingChatModel资源失败", e);
        }
        try {
            if (embeddingModel instanceof AutoCloseable closeable) {
                closeable.close();
            }
        } catch (Exception e) {
            logger.warn("关闭EmbeddingModel资源失败", e);
        }
    }
}
