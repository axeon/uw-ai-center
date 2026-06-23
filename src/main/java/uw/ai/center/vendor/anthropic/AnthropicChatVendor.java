package uw.ai.center.vendor.anthropic;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import org.springframework.stereotype.Service;
import uw.ai.center.vendor.client.ChatClient;
import uw.ai.center.vo.AiModelConfigData;
import uw.common.app.vo.JsonConfigBox;

import java.time.Duration;

/**
 * Anthropic 协议 CHAT 能力实现。
 * <p>只承担 CHAT 客户端的构建逻辑；元信息由协议入口 {@link AnthropicVendor} 统一提供。
 * 作为 Spring bean 存在，由协议入口注入并按 model_type 委托调用。
 */
@Service
public class AnthropicChatVendor {

    /**
     * 构建 CHAT 客户端：同时创建同步 ChatModel 与流式 StreamingChatModel。
     */
    public ChatClient buildChatClient(AiModelConfigData configData) {
        JsonConfigBox box = configData.getConfigParamBox();
        double temperature = box != null ? box.getDoubleParam("temperature", 1.0) : 1.0;
        int maxTokens = box != null ? box.getIntParam("max.tokens", 4096) : 4096;

        var syncModel = AnthropicChatModel.builder()
                .apiKey(configData.getApiKeyRaw())
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .temperature(temperature)
                .maxTokens(maxTokens)
                .timeout(Duration.ofSeconds(120))
                .build();

        var streamingModel = AnthropicStreamingChatModel.builder()
                .apiKey(configData.getApiKeyRaw())
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .temperature(temperature)
                .maxTokens(maxTokens)
                .timeout(Duration.ofSeconds(120))
                .build();

        return new ChatClient(configData, syncModel, streamingModel);
    }
}
