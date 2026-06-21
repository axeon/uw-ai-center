package uw.ai.center.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 模型类型枚举。
 */
@Schema(title = "模型类型", description = "模型类型枚举")
public enum ModelType {

    /** 聊天模型：支持同步/流式对话与工具调用。 */
    CHAT("聊天模型"),
    /** 嵌入模型：将文本转向量，用于RAG检索。 */
    EMBEDDING("嵌入模型"),
    /** 重排模型：对召回结果二次排序（预留）。 */
    RERANK("重排模型"),
    /** 语音合成：文本转语音（预留）。 */
    TTS("语音合成"),
    /** 文字识别：图片转文字（预留）。 */
    OCR("文字识别"),
    /** 图片生成：文本生成图片。 */
    IMAGE_GENERATION("图片生成"),
    /** 语音识别：实时语音转文字（ASR）。 */
    AUDIO_TRANSCRIPTION("语音识别");

    /** 枚举的中文描述，用于前端展示。 */
    private final String desc;

    ModelType(String desc) {
        this.desc = desc;
    }

    /**
     * 获取枚举的中文描述。
     *
     * @return 中文描述
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 从字符串安全转换为枚举，不区分大小写。
     *
     * @param value 模型类型字符串（如 "CHAT"），为 null 时返回 null
     * @return 对应的枚举值；无法匹配时返回 null
     */
    public static ModelType of(String value) {
        if (value == null) {
            return null;
        }
        for (ModelType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
