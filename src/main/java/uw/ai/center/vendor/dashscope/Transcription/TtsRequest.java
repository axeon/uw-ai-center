package uw.ai.center.vendor.dashscope.Transcription;

/**
 * 语音合成请求对象。
 */
public class TtsRequest {

    /**
     * 待合成文本。
     */
    private final String text;

    /**
     * 音色（如 longxiaochun、longshu）。
     */
    private final String voice;

    /**
     * 音频格式（如 wav、mp3、pcm）。
     */
    private final String format;

    /**
     * 采样率。
     */
    private final Integer sampleRate;

    /**
     * 音量（0-100）。
     */
    private final Integer volume;

    /**
     * 语速。
     */
    private final Float speed;

    private TtsRequest(Builder builder) {
        this.text = builder.text;
        this.voice = builder.voice;
        this.format = builder.format;
        this.sampleRate = builder.sampleRate;
        this.volume = builder.volume;
        this.speed = builder.speed;
    }

    public String getText() {
        return text;
    }

    public String getVoice() {
        return voice;
    }

    public String getFormat() {
        return format;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public Integer getVolume() {
        return volume;
    }

    public Float getSpeed() {
        return speed;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String text;
        private String voice;
        private String format = "wav";
        private Integer sampleRate;
        private Integer volume;
        private Float speed;

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder voice(String voice) {
            this.voice = voice;
            return this;
        }

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder sampleRate(Integer sampleRate) {
            this.sampleRate = sampleRate;
            return this;
        }

        public Builder volume(Integer volume) {
            this.volume = volume;
            return this;
        }

        public Builder speed(Float speed) {
            this.speed = speed;
            return this;
        }

        public TtsRequest build() {
            return new TtsRequest(this);
        }
    }
}
