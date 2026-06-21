package uw.ai.center.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 模型能力标签枚举。
 */
@Schema(title = "模型能力标签", description = "模型能力标签枚举")
public enum ModelTag {

    /** 多模态：同时处理文本、图片、音频等多种输入。 */
    MULTIMODAL("多模态"),
    /** 文生图。 */
    TEXT_TO_IMAGE("文生图"),
    /** 图生图。 */
    IMAGE_TO_IMAGE("图生图"),
    /** 语音转文字。 */
    SPEECH_TO_TEXT("语音转文字"),
    /** 文字转语音。 */
    TEXT_TO_SPEECH("文字转语音"),
    /** 文本生成。 */
    TEXT_GENERATION("文本生成"),
    /** 向量嵌入。 */
    EMBEDDING("向量嵌入"),
    /** 重排序。 */
    RERANK("重排序"),
    /** 文字识别。 */
    OCR("文字识别");

    /** 枚举的中文描述，用于前端展示。 */
    private final String desc;

    ModelTag(String desc) {
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
     * @param value 标签字符串，为 null 时返回 null
     * @return 对应的枚举值；无法匹配时返回 null
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
