package uw.ai.center.vendor.dashscope.Transcription;

/**
 * 转录结果监听器接口。
 * 回调阿里云 NLS 实时语音识别服务端推送的事件。
 * <p>
 * 事件流程：TranscriberStart → (SentenceBegin → TranscriptionResultChanged* → SentenceEnd)* → TranscriptionComplete
 */
public interface TranscriptionResultListener {

    /**
     * 连接已建立，StartTranscription 已确认。
     *
     * @param taskId 任务全局唯一ID
     */
    void onStarted(String taskId);

    /**
     * 检测到一句话开始。
     *
     * @param index 句子编号，从1开始递增
     * @param time  当前已处理的音频时长，单位毫秒
     */
    void onSentenceBegin(int index, long time);

    /**
     * 中间识别结果（需开启 enable_intermediate_result）。
     * 一句话识别过程中，结果可能多次变化。
     *
     * @param index  句子编号
     * @param result 当前识别结果
     * @param time   当前已处理的音频时长，单位毫秒
     */
    void onTranscriptionResultChanged(int index, String result, long time);

    /**
     * 一句话识别完成。
     * 服务端智能断句后返回，包含最终识别结果和置信度。
     *
     * @param index      句子编号
     * @param result     最终识别结果
     * @param confidence 置信度，取值 [0.0, 1.0]
     * @param beginTime  当前句子对应的 SentenceBegin 事件时间，单位毫秒
     * @param time       当前已处理的音频时长，单位毫秒
     */
    void onSentenceEnd(int index, String result, double confidence, long beginTime, long time);

    /**
     * 整个转录会话完成。
     */
    void onCompleted();

    /**
     * 发生错误。
     *
     * @param message 错误信息
     */
    void onError(String message);
}
