package uw.ai.center.vendor.openai;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.app.vo.JsonConfigParam;
import uw.common.util.EnumUtils;

/**
 * OpenAI 协议 EMBEDDING 能力的配置参数定义。
 * <p>内部 {@link Config} 枚举列举了 OpenAI Embeddings 接口可调参数，
 * 作为 ai_model_config.config_param 的参数模板供管理端配置。
 */
public class OpenAiEmbeddingParam {

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Schema(title = "EMBEDDING 配置参数", description = "EMBEDDING 配置参数")
    public enum Config implements JsonConfigParam {
        EMBED_API_PATH(ParamType.STRING, "/v1/embeddings", "embedding api路径", "Embedding API路径"),
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
