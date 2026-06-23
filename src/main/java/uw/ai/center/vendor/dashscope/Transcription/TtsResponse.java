package uw.ai.center.vendor.dashscope.Transcription;

/**
 * 语音合成响应对象。
 */
public class TtsResponse {

    /**
     * 音频二进制数据。
     */
    private final byte[] audioData;

    /**
     * 音频内容类型（如 audio/wav）。
     */
    private final String contentType;

    /**
     * 音频时长（毫秒）。
     */
    private final long audioLength;

    public TtsResponse(byte[] audioData, String contentType, long audioLength) {
        this.audioData = audioData;
        this.contentType = contentType;
        this.audioLength = audioLength;
    }

    public byte[] getAudioData() {
        return audioData;
    }

    public String getContentType() {
        return contentType;
    }

    public long getAudioLength() {
        return audioLength;
    }
}
