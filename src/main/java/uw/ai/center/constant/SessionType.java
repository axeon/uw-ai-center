package uw.ai.center.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * session类型。
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(title = "session类型", description = "session类型")
public enum SessionType {

    /** 通用会话：无固定 sessionId，图片生成/语音识别等单次交互复用同一通用会话。 */
    COMMON( 0, "通用" ),

    /** 聊天会话：携带历史消息的多轮对话会话，由 sessionId 唯一标识。 */
    CHAT( 1, "会话" );

    /** 会话类型数值，持久化到 ai_session_info.session_type。 */
    private final int value;

    /** 会话类型中文标签，用于前端展示。 */
    private final String label;

    SessionType(int value, String label) {
        this.value = value;
        this.label = label;
    }

    /**
     * 判断输入的类型数值是否对应有效的会话类型。
     *
     * @param value 会话类型数值
     * @return true=有效，false=无效
     */
    public static boolean isEffective(int value) {
        SessionType[] values = SessionType.values();
        for (SessionType type : values) {
            if (value == type.value) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取会话类型数值。
     *
     * @return 会话类型数值
     */
    public int getValue() {
        return value;
    }

    /**
     * 获取会话类型中文标签。
     *
     * @return 中文标签
     */
    public String getLabel() {
        return label;
    }
}
