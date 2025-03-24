package uw.ai.center.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 描述: 数据类型
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(title = "数据类型", description = "数据类型")
public enum TypeConfigData {

    /**
     * 数值类型
     */
    INT("int", "数值类型"),

    /**
     * 列表数值类型
     */
   SET_INT("set<int>", "列表数值类型"),

    /**
     * 字符串类型
     */
    STRING("string", "字符串类型"),

    /**
     * 列表字符串类型
     */
    SET_STRING("set<string>", "列表字符串类型"),

    /**
     * 布尔类型
     */
    BOOLEAN("boolean", "布尔类型"),

    /**
     * map类型
     */
    MAP("map", "map类型"),

   /**
     * 列表布尔类型
     */
    SET_BOOLEAN("set<boolean>", "列表布尔类型"),

    /**
     * 日期类型
     */
    DATE("date", "日期类型"),

    /**
     * 列表日期类型
     */
    SET_DATE("set<date>", "列表日期类型"),

    /**
     * 日期时间类型
     */
    DATETIME("datetime", "日期时间类型"),

    /**
     * 列表日期时间类型
     */
    SET_DATETIME("set<datetime>", "列表日期时间类型"),

    /**
     * 浮点类型
     */
    FLOAT("float", "浮点类型"),

    /**
     * 列表浮点类型
     */
    SET_FLOAT("set<float>", "列表浮点类型"),
    /**
     * 双精度浮点类型
     */
    DOUBLE("double", "双精度浮点类型"),

    /**
     * 列表双精度浮点类型
     */
    SET_DOUBLE("set<double>", "列表双精度浮点类型"),

    /**
     * 枚举类型
     */
    ENUM("enum", "枚举类型"),

    /**
     * 列表枚举类型
     */
    SET_ENUM("set<enum>", "列表枚举类型"),
    ;

    TypeConfigData(String value, String label) {
        this.value = value;
        this.label = label;
    }

    private final String value;
    private final String label;

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }
}