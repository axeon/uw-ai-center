package uw.ai.center.vendor.dashscope.tts;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.app.vo.JsonConfigParam;
import uw.common.util.EnumUtils;

/**
 * DashScope 语音合成参数。
 */
public class DashScopeTtsParam {

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Schema(title = "语音合成参数", description = "DashScope 语音合成参数")
    public enum Config implements JsonConfigParam {
        TTS_VOICE(ParamType.STRING, "longxiaochun", "音色", "语音合成的音色，如longxiaochun、longshu等"),
        TTS_FORMAT(ParamType.STRING, "wav", "输出格式", "音频输出格式，如wav、mp3、pcm等"),
        TTS_SAMPLE_RATE(ParamType.INT, "22050", "采样率", "音频采样率"),
        TTS_VOLUME(ParamType.INT, "50", "音量", "语音音量（0-100）"),
        TTS_SPEED(ParamType.FLOAT, "1.0", "语速", "语音合成语速"),
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
