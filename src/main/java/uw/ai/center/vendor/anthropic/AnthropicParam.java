package uw.ai.center.vendor.anthropic;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.app.vo.JsonConfigParam;
import uw.common.util.EnumUtils;

/**
 * Anthropic 供应商的配置参数定义。
 * <p>内部 {@link Config} 枚举列举了 Anthropic Messages API 的可调参数（采样温度、最大 token、
 * 核采样、停止序列、系统提示词等），作为 ai_model_config.model_data 的参数模板供管理端配置。
 * <p>Anthropic 协议与 OpenAI 协议参数集不同，且 {@code max_tokens} 在 Anthropic 中为必填项。
 */
public class AnthropicParam {

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Schema(title = "配置参数", description = "配置参数")
    enum Config implements JsonConfigParam {
        TEMPERATURE(ParamType.FLOAT, "1.0", "采样温度控制", "取值 0.0~1.0，值越大回答越具创造性"),
        MAX_TOKENS(ParamType.INT, "4096", "最大生成标记数", "Anthropic 协议必填，生成 token 上限"),
        TOP_P(ParamType.FLOAT, "", "核采样阈值", "核采样概率质量，与 temperature 二选一"),
        STOP_SEQUENCES(ParamType.STRING, "", "停止序列", "遇到这些字符串时停止生成"),
        SYSTEM(ParamType.STRING, "", "系统提示词", "Anthropic 协议中 system 为顶层独立字段"),
        CACHE_SYSTEM_MESSAGES(ParamType.BOOLEAN, "FALSE", "缓存系统提示词", "开启后 SystemMessage 标记 cache_control，命中按 0.1x 计费"),
        CACHE_TOOLS(ParamType.BOOLEAN, "FALSE", "缓存工具定义", "开启后工具列表标记 cache_control，工具调用循环场景下复用率最高"),
        ;

        private final JsonConfigParam.ParamData paramData;

        Config(ParamType type, String value, String desc, String regex) {
            this.paramData = new ParamData(EnumUtils.enumNameToDotCase(name()), type, value, desc, regex);
        }

        @Override
        public JsonConfigParam.ParamData getParamData() {
            return paramData;
        }

    }
}
