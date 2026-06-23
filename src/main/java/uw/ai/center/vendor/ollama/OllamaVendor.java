package uw.ai.center.vendor.ollama;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import org.springframework.stereotype.Service;
import uw.ai.center.vendor.AiChatVendor;
import uw.ai.center.vendor.AiEmbeddingVendor;
import uw.ai.center.vendor.client.ChatClient;
import uw.ai.center.vendor.client.EmbeddingClient;
import uw.ai.center.vo.AiModelConfigData;
import uw.common.app.vo.JsonConfigBox;
import uw.common.app.vo.JsonConfigParam;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * Ollama 协议 vendor 实现。
 * <p>implements 列表即能力清单：{@link AiChatVendor} + {@link AiEmbeddingVendor}，
 * 类型系统直接表达"本 vendor 支持哪些能力"，无需点开源码看哪些方法被覆写。
 */
@Service
public class OllamaVendor implements AiChatVendor, AiEmbeddingVendor {

    /**
     * {@inheritDoc}
     * @return "Ollama"
     */
    @Override
    public String vendorName() {
        return "Ollama";
    }

    /**
     * {@inheritDoc}
     * @return "Ollama via LangChain4j"
     */
    @Override
    public String vendorDesc() {
        return "Ollama via LangChain4j";
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
     * @return Ollama 配置参数集合
     */
    @Override
    public List<JsonConfigParam> configParam() {
        return Arrays.asList(OllamaParam.Config.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChatClient buildChatClient(AiModelConfigData configData) {
        JsonConfigBox configParamBox = configData.getConfigParamBox();
        double temperature = configParamBox != null
                ? configParamBox.getDoubleParam("temperature", 0.7) : 0.7;

        var syncModel = OllamaChatModel.builder()
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .temperature(temperature)
                .timeout(Duration.ofSeconds(120))
                .build();

        var streamingModel = OllamaStreamingChatModel.builder()
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .temperature(temperature)
                .timeout(Duration.ofSeconds(120))
                .build();

        return new ChatClient(configData, syncModel, streamingModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EmbeddingClient buildEmbeddingClient(AiModelConfigData configData) {
        var embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .timeout(Duration.ofSeconds(60))
                .build();

        return new EmbeddingClient(configData, embeddingModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> listModel(String apiUrl, String apiKey) {
        return List.of();
    }
}
