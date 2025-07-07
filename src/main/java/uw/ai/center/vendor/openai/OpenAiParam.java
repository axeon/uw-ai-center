package uw.ai.center.vendor.openai;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.app.vo.JsonConfigParam;
import uw.common.util.EnumUtils;

public class OpenAiParam {

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Schema(title = "vendor参数", description = "vendor参数")
    enum Vendor implements JsonConfigParam {
        API_PATH(ParamType.STRING, "/v1/chat/completions", "api路径", "api路径"),
        TEMPERATURE(ParamType.FLOAT, "0.8", "采样温度控制", "控制生成的明显创造性"),
        FREQUENCY_PENALTY(ParamType.FLOAT, "0.0", "频率惩罚", "降低重复内容概率"),
        MAX_COMPLETION_TOKENS(ParamType.INT, "", "最大生成标记数", "生成标记数上限"),
        N(ParamType.INT, "1", "生成选项数量", "每个输入生成的完成选项数"),
        STORE(ParamType.BOOLEAN, "FALSE", "存储开关", "是否存储请求输出"),
        OUTPUT_MODALITIES(ParamType.STRING, "", "输出模态", "生成类型（text/audio）"),
        OUTPUT_AUDIO(ParamType.STRING, "", "音频参数", "音频生成配置"),
        PRESENCE_PENALTY(ParamType.FLOAT, "", "存在惩罚", "增加新主题可能性"),
        SEED(ParamType.STRING, "", "随机种子", "确定性采样控制"),
        STOP(ParamType.STRING, "", "停止序列", "停止生成的序列列表"),
        TOP_P(ParamType.FLOAT, "", "核采样阈值", "核采样概率质量"),
        TOOLS(ParamType.SET_STRING, "", "可用工具", "模型可调用的工具列表"),
        TOOL_CHOICE(ParamType.STRING, "", "工具选择", "强制调用特定工具"),
        USER(ParamType.STRING, "", "用户标识", "唯一用户标识符"),
        PARALLEL_TOOL_CALLS(ParamType.BOOLEAN, "true", "并行调用", "启用并行函数调用"),
        ;

        private final JsonConfigParam.ParamData paramData;

        Vendor(ParamType type, String value, String desc, String regex) {
            this.paramData = new ParamData( EnumUtils.enumNameToDotCase( name() ), type, value, desc, regex );
        }

        /**
         * 配置参数数据。
         *
         * @return
         */
        @Override
        public JsonConfigParam.ParamData getParamData() {
            return paramData;
        }

    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Schema(title = "embed参数", description = "embed参数")
    enum Embed implements JsonConfigParam {
        API_PATH(ParamType.STRING, "/v1/embeddings", "api.path", "API路径", "Embedding API路径");
        ;

        private final JsonConfigParam.ParamData paramData;

        Embed(ParamType type, String value, String name, String desc, String regex) {
            this.paramData = new ParamData( EnumUtils.enumNameToDotCase( name() ), type, value, desc, regex );
        }

        /**
         * 配置参数数据。
         *
         * @return
         */
        @Override
        public JsonConfigParam.ParamData getParamData() {
            return paramData;
        }

    }
}
