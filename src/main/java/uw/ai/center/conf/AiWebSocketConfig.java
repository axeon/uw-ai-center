package uw.ai.center.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import uw.ai.center.websocket.AiAudioTranscriptionHandler;

/**
 * Spring WebSocket 配置。
 * 注册实时语音转录 WebSocket Handler。
 */
@Configuration
@EnableWebSocket
public class AiWebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new AiAudioTranscriptionHandler(), "/ws/audio/transcribe")
                .setAllowedOrigins("*");
    }
}
