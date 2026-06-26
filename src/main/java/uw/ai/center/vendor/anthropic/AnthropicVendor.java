package uw.ai.center.vendor.anthropic;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import org.springframework.stereotype.Service;
import uw.ai.center.vendor.AiChatVendor;
import uw.ai.center.vendor.client.ChatClient;
import uw.ai.center.vo.AiModelConfigData;
import uw.common.app.vo.JsonConfigBox;
import uw.common.app.vo.JsonConfigParam;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * Anthropic 协议 vendor 实现。
 * <p>implements 列表即能力清单：{@link AiChatVendor}，类型系统直接表达"本 vendor 仅支持 CHAT"，
 * 无需点开源码看哪些方法被覆写。
 * <p>同一 Vendor 既能接官方 Anthropic（Claude 系列），也能接 Anthropic 兼容代理
 * （阿里云百炼调千问、Kimi、智谱 GLM-4.5 等）：{@code baseUrl + apiKey} 在 ai_model_api 中按供应商填写。
 * <p>由于 langchain4j-anthropic 不提供 EmbeddingModel，本协议仅支持 CHAT。
 */
@Service
public class AnthropicVendor implements AiChatVendor {

    /**
     * {@inheritDoc}
     * @return "Anthropic"
     */
    @Override
    public String vendorName() {
        return "Anthropic";
    }

    /**
     * {@inheritDoc}
     * @return "Anthropic via LangChain4j"
     */
    @Override
    public String vendorDesc() {
        return "Anthropic via LangChain4j";
    }

    /**
     * {@inheritDoc}
     * @return "1.0.0"
     */
    @Override
    public String vendorVersion() {
        return "1.0.0";
    }

    /**
     * {@inheritDoc}
     * @return 空字符串（未配置图标）
     */
    @Override
    public String vendorIcon() {
        return "";
    }

    /**
     * {@inheritDoc}
     * @return Anthropic 配置参数集合（temperature / maxTokens / topP / stopSequences / system）
     */
    @Override
    public List<JsonConfigParam> configParam() {
        return Arrays.asList(AnthropicParam.Config.values());
    }

    /**
     * {@inheritDoc}
     * <p>同时创建同步 {@link AnthropicChatModel} 与流式 {@link AnthropicStreamingChatModel}。
     */
    @Override
    public ChatClient buildChatClient(AiModelConfigData configData) {
        JsonConfigBox box = configData.getConfigParamBox();
        double temperature = box != null ? box.getDoubleParam("temperature", 1.0) : 1.0;
        int maxTokens = box != null ? box.getIntParam("max.tokens", 4096) : 4096;
        // Prompt Caching 开关：默认关闭，开启后 SystemMessage / 工具定义会被标记 cache_control。
        boolean cacheSystemMessages = box != null && box.getBooleanParam("cache.system.messages", false);
        boolean cacheTools = box != null && box.getBooleanParam("cache.tools", false);

        var syncModel = AnthropicChatModel.builder()
                .apiKey(configData.getApiKeyRaw())
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .temperature(temperature)
                .maxTokens(maxTokens)
                .cacheSystemMessages(cacheSystemMessages)
                .cacheTools(cacheTools)
                .timeout(Duration.ofSeconds(120))
                .build();

        var streamingModel = AnthropicStreamingChatModel.builder()
                .apiKey(configData.getApiKeyRaw())
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .temperature(temperature)
                .maxTokens(maxTokens)
                .cacheSystemMessages(cacheSystemMessages)
                .cacheTools(cacheTools)
                .timeout(Duration.ofSeconds(120))
                .build();

        return new ChatClient(configData, syncModel, streamingModel);
    }

    /**
     * {@inheritDoc}
     * <p>当前未接入 Anthropic 的模型列表接口，固定返回空列表。
     */
    @Override
    public List<String> listModel(String apiUrl, String apiKey) {
        return List.of();
    }
}
