package uw.ai.center.vendor;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.ai.center.vo.AiModelConfigData;
import uw.ai.center.vendor.dashscope.realtimeTranscriptionModel.RealtimeTranscriptionModel;
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
     * 构建模型实例。
     * 实现类根据 AiModelConfigData 中的 modelType（对应 ModelType 枚举）构建对应类型的客户端。
     */
    AiVendorClientWrapper buildClientWrapper(AiModelConfigData configData);

    /**
     * 获取模型列表。
     */
    List<String> listModel(String apiUrl, String apiKey);

    /**
     * 按需创建一个独立的实时语音识别模型实例。
     * <p>
     * 实时语音识别模型在会话期间持有 WebSocket 等可变状态，不适合多请求共享。
     * 调用方（如文件转录）应每次请求创建独立实例，避免并发会话互相冲突。
     * <p>
     * 默认不支持，由实现了 AUDIO_TRANSCRIPTION 类型的 vendor 覆写。
     *
     * @param configData 模型配置数据
     * @return 独立的模型实例；不支持时返回 null
     */
    default RealtimeTranscriptionModel createAudioTranscriptionModel(AiModelConfigData configData) {
        return null;
    }

}
