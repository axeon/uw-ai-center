package uw.ai.center.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import uw.ai.center.vendor.dashscope.realtimeTranscriptionModel.AiAudioTranscriptionHandler;

/**
 * Spring WebSocket 配置。
 * <p>注册实时语音转录 WebSocket Handler，端点路径为 {@code /ws/audio/transcribe?configId=xxx}，
 * 由 {@link AiAudioTranscriptionHandler} 代理客户端与阿里云 NLS 之间的实时语音识别交互。
 */
@Configuration
@EnableWebSocket
public class AiWebSocketConfig implements WebSocketConfigurer {

    /**
     * 注册实时语音转录 WebSocket Handler。
     *
     * @param registry WebSocket Handler 注册器
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new AiAudioTranscriptionHandler(), "/ws/audio/transcribe")
                .setAllowedOrigins("*");
    }
}
