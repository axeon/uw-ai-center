package uw.ai.center.vendor.client;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.ai.center.constant.ModelType;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vendor.dashscope.realtimeTranscriptionModel.RealtimeTranscriptionModel;
import uw.ai.center.vo.AiModelConfigData;

/**
 * AUDIO_TRANSCRIPTION 类型客户端。
 * <p>仅作为"配置载体 + 实例工厂"，不持有 RealtimeTranscriptionModel 实例。
 * 实时语音识别在会话期间持有 WebSocket 等可变状态，缓存的共享实例不适合多请求并发复用，
 * 因此每次请求都通过 {@link #createRealtimeSession()} 现场新建独立实例。
 */
@Schema(title = "语音识别客户端", description = "语音识别客户端")
public class AudioTranscriptionClient extends AiModelClient {

    public AudioTranscriptionClient(AiModelConfigData configData, AiVendor vendor) {
        super(configData, vendor, ModelType.AUDIO_TRANSCRIPTION);
    }

    /**
     * 按需创建一个独立的实时语音识别模型实例。
     * <p>实例基于不可变配置，start() 时才建立 WebSocket，创建成本极低。
     * 调用方使用完毕后应自行 close() 释放底层 WebSocket 连接。
     *
     * @return 独立的模型实例；vendor 不支持时返回 null
     */
    public RealtimeTranscriptionModel createRealtimeSession() {
        return vendor.createAudioTranscriptionModel(configData);
    }

    /**
     * 空实现：本客户端只作为配置载体，不持有任何需要关闭的资源。
     */
    @Override
    protected void doClose() {
        // no-op
    }
}
