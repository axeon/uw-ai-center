package uw.ai.center.vendor.dashscope.imageModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.app.vo.JsonConfigParam;
import uw.common.util.EnumUtils;

/**
 * DashScope 图片生成参数。
 */
public class DashScopeImageParam {

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Schema(title = "图片生成参数", description = "图片生成参数")
    public enum Config implements JsonConfigParam {
        IMAGE_SIZE(ParamType.STRING, "1024*1024", "图片尺寸", "生成图片的尺寸，如1024*1024、720*1280等"),
        IMAGE_STYLE(ParamType.STRING, "<auto>", "图片风格", "生成图片的风格，如<auto>、<photography>等"),
        IMAGE_N(ParamType.INT, "1", "生成数量", "一次生成的图片数量"),
        IMAGE_REF_MODE(ParamType.STRING, "", "参考模式", "图片生成参考模式"),
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
