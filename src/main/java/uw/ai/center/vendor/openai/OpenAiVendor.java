package uw.ai.center.vendor.openai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.springframework.stereotype.Service;
import uw.ai.center.vendor.capability.ChatVendor;
import uw.ai.center.vendor.capability.EmbeddingVendor;
import uw.ai.center.vendor.client.ChatClient;
import uw.ai.center.vendor.client.EmbeddingClient;
import uw.ai.center.vo.AiModelConfigData;
import uw.common.app.vo.JsonConfigBox;
import uw.common.app.vo.JsonConfigParam;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * OpenAI 协议 vendor 实现。
 * <p>implements 列表即能力清单：{@link ChatVendor} + {@link EmbeddingVendor}，
 * 类型系统直接表达"本 vendor 支持哪些能力"，无需点开源码看哪些方法被覆写。
 */
@Service
public class OpenAiVendor implements ChatVendor, EmbeddingVendor {

    /**
     * {@inheritDoc}
     * @return "OpenAi"
     */
    @Override
    public String vendorName() {
        return "OpenAi";
    }

    /**
     * {@inheritDoc}
     * @return "OpenAI via LangChain4j"
     */
    @Override
    public String vendorDesc() {
        return "OpenAI via LangChain4j";
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
     * @return OpenAI 配置参数集合（合并 CHAT 与 EMBEDDING 两类参数）
     */
    @Override
    public List<JsonConfigParam> configParam() {
        List<JsonConfigParam> params = new ArrayList<>();
        params.addAll(Arrays.asList(OpenAiChatParam.Config.values()));
        params.addAll(Arrays.asList(OpenAiEmbeddingParam.Config.values()));
        return params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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

    /**
     * {@inheritDoc}
     */
    @Override
    public EmbeddingClient buildEmbeddingClient(AiModelConfigData configData) {
        var embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(configData.getApiKeyRaw())
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .timeout(Duration.ofSeconds(60))
                .build();

        return new EmbeddingClient(configData, embeddingModel);
    }

    /**
     * {@inheritDoc}
     * <p>当前未接入 OpenAI 的模型列表接口，固定返回空列表。
     */
    @Override
    public List<String> listModel(String apiUrl, String apiKey) {
        return List.of();
    }
}
