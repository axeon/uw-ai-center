package uw.ai.center.vendor.dashscope;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.app.vo.JsonConfigParam;
import uw.common.util.EnumUtils;

/**
 * DashScope 供应商配置参数。
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
        // 语音识别参数
        AUDIO_FORMAT(ParamType.STRING, "wav", "音频格式", "音频文件格式，如wav、mp3、pcm等"),
        AUDIO_SAMPLE_RATE(ParamType.INT, "16000", "采样率", "音频采样率"),
        AUDIO_ENABLE_WORDS(ParamType.BOOLEAN, "false", "词级时间戳", "是否返回词级时间戳"),
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
