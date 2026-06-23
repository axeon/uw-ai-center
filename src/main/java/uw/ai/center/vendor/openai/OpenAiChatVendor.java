package uw.ai.center.vendor.openai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.springframework.stereotype.Service;
import uw.ai.center.vendor.client.ChatClient;
import uw.ai.center.vo.AiModelConfigData;
import uw.common.app.vo.JsonConfigBox;

import java.time.Duration;

/**
 * OpenAI 协议 CHAT 能力实现。
 * <p>只承担 CHAT 客户端的构建逻辑；元信息由协议入口 {@link OpenAiVendor} 统一提供。
 * 作为 Spring bean 存在，由协议入口注入并按 model_type 委托调用。
 */
@Service
public class OpenAiChatVendor {

    /**
     * 构建 CHAT 客户端：同时创建同步 ChatModel 与流式 StreamingChatModel。
     */
    public ChatClient buildChatClient(AiModelConfigData configData) {
        JsonConfigBox configParamBox = configData.getConfigParamBox();
        double temperature = configParamBox != null
                ? configParamBox.getDoubleParam("temperature", 0.7) : 0.7;

        var syncModel = OpenAiChatModel.builder()
                .apiKey(configData.getApiKeyRaw())
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .temperature(temperature)
                .timeout(Duration.ofSeconds(120))
                .build();

        var streamingModel = OpenAiStreamingChatModel.builder()
                .apiKey(configData.getApiKeyRaw())
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .temperature(temperature)
                .timeout(Duration.ofSeconds(120))
                .build();

        return new ChatClient(configData, syncModel, streamingModel);
    }
}
