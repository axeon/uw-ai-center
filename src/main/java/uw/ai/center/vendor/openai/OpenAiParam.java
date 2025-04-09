package uw.ai.center.vendor.openai;

import uw.common.app.vo.JsonConfigParam;
import uw.common.util.EnumUtils;

public class OpenAiParam {


    enum Vendor implements JsonConfigParam {
        API_PATH(ParamType.STRING, "/v1/chat/completions", "api.path", "api路径", "api路径"),
        TEMPERATURE(ParamType.FLOAT, "0.8", "temperature", "采样温度控制", "控制生成的明显创造性"),
        FREQUENCY_PENALTY(ParamType.FLOAT, "0.0", "frequency.penalty", "频率惩罚", "降低重复内容概率"),
        MAX_COMPLETION_TOKENS(ParamType.INT, "", "max.completion.tokens", "最大生成标记数", "生成标记数上限"),
        N(ParamType.INT, "1", "n", "生成选项数量", "每个输入生成的完成选项数"),
        STORE(ParamType.BOOLEAN, "FALSE", "store", "存储开关", "是否存储请求输出"),
        OUTPUT_MODALITIES(ParamType.STRING, "", "output.modalities", "输出模态", "生成类型（text/audio）"),
        OUTPUT_AUDIO(ParamType.STRING, "", "output.audio", "音频参数", "音频生成配置"),
        PRESENCE_PENALTY(ParamType.FLOAT, "", "presence.penalty", "存在惩罚", "增加新主题可能性"),
        SEED(ParamType.STRING, "", "seed", "随机种子", "确定性采样控制"),
        STOP(ParamType.STRING, "", "stop", "停止序列", "停止生成的序列列表"),
        TOP_P(ParamType.FLOAT, "", "top.p", "核采样阈值", "核采样概率质量"),
        TOOLS(ParamType.SET_STRING, "", "tools", "可用工具", "模型可调用的工具列表"),
        TOOL_CHOICE(ParamType.STRING, "", "tool.choice", "工具选择", "强制调用特定工具"),
        USER(ParamType.STRING, "", "user", "用户标识", "唯一用户标识符"),
        PARALLEL_TOOL_CALLS(ParamType.BOOLEAN, "true", "parallel.tool.calls", "并行调用", "启用并行函数调用"),
        ;

        private final JsonConfigParam.ParamData paramData;

        Vendor(ParamType type, String value, String name, String desc, String regex) {
            this.paramData = new ParamData( EnumUtils.enumNameToDotCase( name() ), type, value, name, desc, regex );
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


    enum Embed implements JsonConfigParam {
        API_PATH(ParamType.STRING, "/v1/embeddings", "api.path", "API路径", "Embedding API路径");
        ;

        private final JsonConfigParam.ParamData paramData;

        Embed(ParamType type, String value, String name, String desc, String regex) {
            this.paramData = new ParamData( EnumUtils.enumNameToDotCase( name() ), type, value, name, desc, regex );
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
