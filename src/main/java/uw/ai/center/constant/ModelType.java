package uw.ai.center.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 模型类型枚举。
 */
@Schema(title = "模型类型", description = "模型类型枚举")
public enum ModelType {

    CHAT("聊天模型"),
    EMBEDDING("嵌入模型"),
    RERANK("重排模型"),
    TTS("语音合成"),
    OCR("文字识别");

    private final String desc;

    ModelType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 从字符串安全转换为枚举，不区分大小写。
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
