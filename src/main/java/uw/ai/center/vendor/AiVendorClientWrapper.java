package uw.ai.center.vendor;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
 import dev.langchain4j.model.image.ImageModel;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.ai.center.constant.ModelType;
import uw.ai.center.model.RealtimeTranscriptionModel;
import uw.ai.center.model.TtsModel;
import uw.ai.center.vo.AiModelConfigData;

/**
 * AI供应商客户端封装类（LangChain4j）。
 * 按modelType构建：CHAT类型提供ChatModel+StreamingChatModel，EMBEDDING类型提供EmbeddingModel，
 * IMAGE_GENERATION类型提供ImageModel，AUDIO_TRANSCRIPTION类型提供RealtimeTranscriptionModel，
 * TTS类型提供TtsModel。
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

    @Schema(title = "图片生成模型", description = "图片生成模型，IMAGE_GENERATION类型时可用")
    private final ImageModel imageModel;

    @Schema(title = "实时语音识别模型", description = "实时语音识别模型，AUDIO_TRANSCRIPTION类型时可用")
    private final RealtimeTranscriptionModel audioTranscriptionModel;

    @Schema(title = "语音合成模型", description = "语音合成模型，TTS类型时可用")
    private final TtsModel ttsModel;

    public AiVendorClientWrapper(AiModelConfigData configData,
                                 ChatModel chatModel,
                                 StreamingChatModel streamingChatModel,
                                 EmbeddingModel embeddingModel,
                                 ImageModel imageModel,
                                 RealtimeTranscriptionModel audioTranscriptionModel,
                                 TtsModel ttsModel) {
        this.configData = configData;
        this.chatModel = chatModel;
        this.streamingChatModel = streamingChatModel;
        this.embeddingModel = embeddingModel;
        this.imageModel = imageModel;
        this.audioTranscriptionModel = audioTranscriptionModel;
        this.ttsModel = ttsModel;
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

    public ImageModel getImageModel() {
        return imageModel;
    }

    public RealtimeTranscriptionModel getAudioTranscriptionModel() {
        return audioTranscriptionModel;
    }

    public TtsModel getTtsModel() {
        return ttsModel;
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
        closeResource(chatModel, "ChatModel");
        closeResource(streamingChatModel, "StreamingChatModel");
        closeResource(embeddingModel, "EmbeddingModel");
        closeResource(imageModel, "ImageModel");
        closeResource(audioTranscriptionModel, "AudioTranscriptionModel");
        closeResource(ttsModel, "TtsModel");
    }

    private void closeResource(Object resource, String name) {
        if (resource instanceof AutoCloseable closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                logger.warn("关闭{}资源失败", name, e);
            }
        }
    }
}
