package uw.ai.center.vendor.lc4j;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uw.ai.center.vo.AiModelConfigData;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vendor.AiVendorClientWrapper;
import uw.common.app.vo.JsonConfigBox;
import uw.common.app.vo.JsonConfigParam;

import java.time.Duration;
import java.util.List;

/**
 * LangChain4j 版 OpenAI 供应商实现。
 */
@Service
public class OpenAiLc4jVendor implements AiVendor {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiLc4jVendor.class);

    @Override
    public String vendorName() {
        return "OpenAi-Lc4j";
    }

    @Override
    public String vendorDesc() {
        return "OpenAI via LangChain4j";
    }

    @Override
    public String vendorVersion() {
        return "1.0.0";
    }

    @Override
    public String vendorIcon() {
        return "";
    }

    @Override
    public List<JsonConfigParam> vendorParam() {
        return List.of();
    }

    @Override
    public List<JsonConfigParam> modelParam() {
        return List.of();
    }

    @Override
    public List<JsonConfigParam> embedParam() {
        return List.of();
    }

    @Override
    public AiVendorClientWrapper buildClientWrapper(AiModelConfigData aiModelConfigData) {
        throw new UnsupportedOperationException(
            "OpenAiLc4jVendor 不支持 buildClientWrapper()，请使用 buildLc4jClientWrapper()");
    }

    /**
     * 构造 LangChain4j 客户端。
     */
    public Lc4jClientWrapper buildLc4jClientWrapper(AiModelConfigData aiModelConfigData) {
        JsonConfigBox vendorParamBox = aiModelConfigData.getVendorParamBox();
        String apiKey = aiModelConfigData.getApiKey();
        String apiUrl = aiModelConfigData.getApiUrl();
        String modelMain = aiModelConfigData.getModelMain();
        String modelEmbed = aiModelConfigData.getModelEmbed();
        Double temperature = vendorParamBox.getDoubleParam("temperature");

        var syncModel = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(apiUrl)
                .modelName(modelMain)
                .temperature(temperature != null ? temperature : 0.7)
                .timeout(Duration.ofSeconds(120))
                .build();

        var streamingModel = OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(apiUrl)
                .modelName(modelMain)
                .temperature(temperature != null ? temperature : 0.7)
                .timeout(Duration.ofSeconds(120))
                .build();

        var embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .baseUrl(apiUrl)
                .modelName(modelEmbed)
                .timeout(Duration.ofSeconds(60))
                .build();

        return new Lc4jClientWrapper(aiModelConfigData, syncModel, streamingModel, embeddingModel);
    }

    @Override
    public List<String> listModel(String apiUrl, String apiKey) {
        return List.of();
    }
}
