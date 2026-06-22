package uw.ai.center.vendor;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.ai.center.constant.ModelType;
import uw.ai.center.vendor.client.AiModelClient;
import uw.ai.center.vendor.client.AudioTranscriptionClient;
import uw.ai.center.vendor.client.ChatClient;
import uw.ai.center.vendor.client.EmbeddingClient;
import uw.ai.center.vendor.client.ImageGenerationClient;
import uw.ai.center.vendor.dashscope.realtimeTranscriptionModel.RealtimeTranscriptionModel;
import uw.ai.center.vo.AiModelConfigData;
import uw.common.app.vo.JsonConfigParam;

import java.util.List;

/**
 * Ai供应商接口。
 */
@Schema(title = "Ai供应商接口", description = "Ai供应商接口")
public interface AiVendor {

    /**
     * 供应商名称
     */
    @JsonProperty("vendorName")
    @Schema(title = "供应商名称", description = "供应商名称")
    String vendorName();

    /**
     * 供应商描述
     */
    @JsonProperty("vendorDesc")
    @Schema(title = "供应商描述", description = "供应商描述")
    String vendorDesc();

    /**
     * 供应商版本
     */
    @JsonProperty("vendorVersion")
    @Schema(title = "供应商版本", description = "供应商版本")
    String vendorVersion();

    /**
     * 供应商图标
     */
    @JsonProperty("vendorIcon")
    @Schema(title = "供应商图标", description = "供应商图标")
    String vendorIcon();

    /**
     * 供应商类名
     */
    @JsonProperty("vendorClass")
    @Schema(title = "供应商类名", description = "供应商类名")
    default String vendorClass() {
        return this.getClass().getName();
    }

    /**
     * 配置参数信息集合，管理员可见。
     */
    @JsonProperty("configParam")
    @Schema(title = "配置参数信息集合", description = "配置参数信息集合，管理员可见。")
    List<JsonConfigParam> configParam();

    /**
     * 按模型类型构建对应的客户端实例。
     * <p>默认实现按 AiModelConfigData 中的 modelType（对应 {@link ModelType} 枚举）分发到对应的 buildXxxClient 方法；
     * vendor 不支持的类型默认返回 null。
     *
     * @param configData 聚合了 API 配置与模型配置的数据对象
     * @return 具体子类实例（ChatClient/EmbeddingClient/ImageGenerationClient/AudioTranscriptionClient）；不支持时返回 null
     */
    default AiModelClient buildClient(AiModelConfigData configData) {
        ModelType modelType = ModelType.of(configData.getModelType());
        if (modelType == null) {
            return null;
        }
        return switch (modelType) {
            case CHAT -> buildChatClient(configData);
            case EMBEDDING -> buildEmbeddingClient(configData);
            case IMAGE_GENERATION -> buildImageClient(configData);
            case AUDIO_TRANSCRIPTION -> buildAudioTranscriptionClient(configData);
            // TTS / RERANK / OCR 等类型待后续接入时新增 buildXxxClient + 对应子类
            default -> null;
        };
    }

    /**
     * 构建 CHAT 类型客户端。默认不支持，子类按需覆写。
     */
    default ChatClient buildChatClient(AiModelConfigData configData) {
        return null;
    }

    /**
     * 构建 EMBEDDING 类型客户端。默认不支持，子类按需覆写。
     */
    default EmbeddingClient buildEmbeddingClient(AiModelConfigData configData) {
        return null;
    }

    /**
     * 构建 IMAGE_GENERATION 类型客户端。默认不支持，子类按需覆写。
     */
    default ImageGenerationClient buildImageClient(AiModelConfigData configData) {
        return null;
    }

    /**
     * 构建 AUDIO_TRANSCRIPTION 类型客户端。默认不支持，子类按需覆写。
     */
    default AudioTranscriptionClient buildAudioTranscriptionClient(AiModelConfigData configData) {
        return null;
    }

    /**
     * 获取模型列表。
     */
    List<String> listModel(String apiUrl, String apiKey);

    /**
     * 按需创建一个独立的实时语音识别模型实例。
     * <p>实时语音识别在会话期间持有 WebSocket 等可变状态，不适合多请求共享。
     * 调用方（如文件转录）应每次请求创建独立实例，避免并发会话互相冲突。
     * <p>默认不支持，由实现了 AUDIO_TRANSCRIPTION 类型的 vendor 覆写。
     *
     * @param configData 模型配置数据
     * @return 独立的模型实例；不支持时返回 null
     */
    default RealtimeTranscriptionModel createAudioTranscriptionModel(AiModelConfigData configData) {
        return null;
    }

}
