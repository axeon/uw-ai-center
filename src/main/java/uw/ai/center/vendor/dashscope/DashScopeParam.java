package uw.ai.center.vendor.dashscope;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.app.vo.JsonConfigParam;
import uw.common.util.EnumUtils;

/**
 * DashScope 供应商配置参数。
 * 语音识别基于 DashScope Fun-ASR 协议，鉴权使用 ai_model_api.api_key（Bearer Token）。
 */
public class DashScopeParam {

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Schema(title = "配置参数", description = "配置参数")
    enum Config implements JsonConfigParam {
        // 图片生成参数
        IMAGE_SIZE(ParamType.STRING, "1024*1024", "图片尺寸", "生成图片的尺寸，如1024*1024、720*1280等"),
        IMAGE_STYLE(ParamType.STRING, "<auto>", "图片风格", "生成图片的风格，如<auto>、<photography>等"),
        IMAGE_N(ParamType.INT, "1", "生成数量", "一次生成的图片数量"),
        IMAGE_REF_MODE(ParamType.STRING, "", "参考模式", "图片生成参考模式"),
        // 语音识别参数（DashScope Fun-ASR 实时识别）
        DASHSCOPE_WORKSPACE_ID(ParamType.STRING, "", "业务空间ID", "可选，阿里云百炼业务空间ID（X-DashScope-WorkSpace）"),
        AUDIO_FORMAT(ParamType.STRING, "pcm", "音频格式", "音频格式：pcm、wav、mp3、opus、speex、aac、amr"),
        AUDIO_SAMPLE_RATE(ParamType.INT, "16000", "采样率", "音频采样率，fun-asr-realtime 固定 16000Hz"),
        AUDIO_LANGUAGE_HINTS(ParamType.STRING, "zh", "语种提示", "语种代码：zh中文/en英文/ja日语，逗号分隔多个"),
        AUDIO_SEMANTIC_PUNCTUATION(ParamType.BOOLEAN, "false", "语义断句", "true开启语义断句(更准)，false使用VAD断句(更低延迟)"),
        AUDIO_MAX_SENTENCE_SILENCE(ParamType.INT, "800", "断句静音阈值", "VAD断句的静音时长阈值，200~6000ms，默认800"),
        AUDIO_SPEECH_NOISE_THRESHOLD(ParamType.FLOAT, "0.0", "VAD灵敏度", "[-1.0, 1.0]，越接近-1越敏感，越接近1越严格"),
        // 语音合成参数
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
