package uw.ai.center.vendor.client;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.ai.center.constant.ModelType;
import uw.ai.center.vendor.dashscope.Transcription.RealtimeTranscriptionModel;
import uw.ai.center.vo.AiModelConfigData;

import java.util.function.Supplier;

/**
 * AUDIO_TRANSCRIPTION 类型客户端。
 * <p>仅作为"配置载体 + 实例工厂"，不持有 RealtimeTranscriptionModel 实例。
 * 实时语音识别在会话期间持有 WebSocket 等可变状态，缓存的共享实例不适合多请求并发复用，
 * 因此每次请求都通过 {@link #createRealtimeSession()} 现场新建独立实例。
 * <p>实例创建逻辑由 vendor 在构建客户端时通过 {@link Supplier} 工厂注入，避免反向持有 vendor 引用。
 */
@Schema(title = "语音识别客户端", description = "语音识别客户端")
public class AudioTranscriptionClient extends AiModelClient {

    /**
     * 实时语音识别模型实例工厂，闭包捕获 vendor 端的构建逻辑。
     */
    private final Supplier<RealtimeTranscriptionModel> factory;

    public AudioTranscriptionClient(AiModelConfigData configData,
                                    Supplier<RealtimeTranscriptionModel> factory) {
        super(configData, ModelType.AUDIO_TRANSCRIPTION);
        this.factory = factory;
    }

    /**
     * 按需创建一个独立的实时语音识别模型实例。
     * <p>实例基于不可变配置，start() 时才建立 WebSocket，创建成本极低。
     * 调用方使用完毕后应自行 close() 释放底层 WebSocket 连接。
     *
     * @return 独立的模型实例
     */
    public RealtimeTranscriptionModel createRealtimeSession() {
        return factory.get();
    }

    /**
     * 空实现：本客户端只作为配置载体，不持有任何需要关闭的资源。
     */
    @Override
    protected void doClose() {
        // no-op
    }
}
