package uw.ai.center.vendor.dashscope.rerank;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.app.vo.JsonConfigParam;
import uw.common.util.EnumUtils;

/**
 * DashScope 重排参数（qwen3-rerank）。
 */
public class DashScopeRerankParam {

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Schema(title = "重排参数", description = "DashScope qwen3-rerank 重排参数")
    public enum Config implements JsonConfigParam {
        RERANK_TOP_N(ParamType.INT, "0", "返回前N条", "0表示返回全部，否则只返回得分最高的前N条"),
        RERANK_INSTRUCT(ParamType.STRING, "", "任务类型指令", "可选，空表示用模型默认（问答检索）"),
        RERANK_RETURN_DOCUMENTS(ParamType.BOOLEAN, "true", "回带文档原文", "true结果中包含原文，false仅返回下标与得分"),
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
