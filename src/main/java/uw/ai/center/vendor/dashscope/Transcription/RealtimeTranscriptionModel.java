package uw.ai.center.vendor.dashscope.Transcription;

/**
 * 实时转录模型接口。
 * LangChain4j 的 AudioTranscriptionModel 是同步单次调用，不支持实时流式，因此自定义此接口。
 * <p>
 * 使用方式：
 * <pre>
 *   model.start(listener);      // 建立 WebSocket 连接，发送 StartTranscription 指令
 *   model.sendAudio(audioData); // 循环发送音频二进制帧
 *   model.stop();               // 发送 StopTranscription 指令，等待完成后关闭连接
 * </pre>
 */
public interface RealtimeTranscriptionModel extends AutoCloseable {

    /**
     * 开始实时转录会话。
     * 建立 WebSocket 连接，发送 StartTranscription 指令，等待服务端确认。
     *
     * @param listener 转录结果监听器
     */
    void start(TranscriptionResultListener listener);

    /**
     * 发送音频数据。
     * 通过 WebSocket 发送音频二进制帧，模拟实时语音流。
     * 建议每次发送 3200 字节（16kHz 采样率下约 100ms 的音频），间隔 100ms。
     *
     * @param audioData 音频二进制数据
     */
    void sendAudio(byte[] audioData);

    /**
     * 停止转录会话。
     * 发送 StopTranscription 指令，等待服务端返回 TranscriptionComplete 后关闭连接。
     */
    void stop();
}
