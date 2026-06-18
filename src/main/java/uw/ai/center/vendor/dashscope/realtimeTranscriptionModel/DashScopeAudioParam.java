package uw.ai.center.vendor.dashscope.realtimeTranscriptionModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.app.vo.JsonConfigParam;
import uw.common.util.EnumUtils;

/**
 * DashScope 语音识别参数（Fun-ASR 实时识别）。
 */
public class DashScopeAudioParam {

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Schema(title = "语音识别参数", description = "DashScope Fun-ASR 实时语音识别参数")
    public enum Config implements JsonConfigParam {
        DASHSCOPE_WORKSPACE_ID(ParamType.STRING, "", "业务空间ID", "可选，阿里云百炼业务空间ID（X-DashScope-WorkSpace）"),
        AUDIO_FORMAT(ParamType.STRING, "pcm", "音频格式", "音频格式：pcm、wav、mp3、opus、speex、aac、amr"),
        AUDIO_SAMPLE_RATE(ParamType.INT, "16000", "采样率", "音频采样率，fun-asr-realtime 固定 16000Hz"),
        AUDIO_LANGUAGE_HINTS(ParamType.STRING, "zh", "语种提示", "语种代码：zh中文/en英文/ja日语，逗号分隔多个"),
        AUDIO_SEMANTIC_PUNCTUATION(ParamType.BOOLEAN, "false", "语义断句", "true开启语义断句(更准)，false使用VAD断句(更低延迟)"),
        AUDIO_MAX_SENTENCE_SILENCE(ParamType.INT, "800", "断句静音阈值", "VAD断句的静音时长阈值，200~6000ms，默认800"),
        AUDIO_SPEECH_NOISE_THRESHOLD(ParamType.FLOAT, "0.0", "VAD灵敏度", "[-1.0, 1.0]，越接近-1越敏感，越接近1越严格"),
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
