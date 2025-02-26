package uw.ai.center.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * session类型。
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(title = "session类型", description = "session类型")
public enum SessionType {

    COMMON( 0, "通用" ),

    CHAT( 1, "会话" );

    private final int value;

    private final String label;

    SessionType(int value, String label) {
        this.value = value;
        this.label = label;
    }

    /**
     * 判断输入的类型数值是否对应正常状态
     *
     * @param value
     * @return
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

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
