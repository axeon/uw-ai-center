package uw.ai.center.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 模型能力标签枚举。
 */
@Schema(title = "模型能力标签", description = "模型能力标签枚举")
public enum ModelTag {

    MULTIMODAL("多模态"),
    TEXT_TO_IMAGE("文生图"),
    IMAGE_TO_IMAGE("图生图"),
    SPEECH_TO_TEXT("语音转文字"),
    TEXT_TO_SPEECH("文字转语音"),
    TEXT_GENERATION("文本生成"),
    EMBEDDING("向量嵌入"),
    RERANK("重排序"),
    OCR("文字识别");

    private final String desc;

    ModelTag(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 从字符串安全转换为枚举，不区分大小写。
     */
    public static ModelTag of(String value) {
        if (value == null) {
            return null;
        }
        for (ModelTag tag : values()) {
            if (tag.name().equalsIgnoreCase(value)) {
                return tag;
            }
        }
        return null;
    }
}
