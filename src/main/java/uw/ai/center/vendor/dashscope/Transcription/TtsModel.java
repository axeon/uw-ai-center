package uw.ai.center.vendor.dashscope.Transcription;

/**
 * 语音合成模型接口。
 * LangChain4j 1.15.0 无 TTS 标准接口，自定义此接口。
 */
public interface TtsModel {

    /**
     * 语音合成。
     *
     * @param request 合成请求
     * @return 合成响应
     */
    TtsResponse synthesize(TtsRequest request);
}
