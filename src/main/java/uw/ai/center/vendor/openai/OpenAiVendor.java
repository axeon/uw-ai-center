package uw.ai.center.vendor.openai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uw.ai.center.constant.ModelType;
import uw.ai.center.vo.AiModelConfigData;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vendor.AiVendorClientWrapper;
import uw.common.app.vo.JsonConfigBox;
import uw.common.app.vo.JsonConfigParam;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * OpenAI 供应商实现（LangChain4j）。
 */
@Service
public class OpenAiVendor implements AiVendor {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiVendor.class);

    @Override
    public String vendorName() {
        return "OpenAi";
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
    public List<JsonConfigParam> configParam() {
        return Arrays.asList(OpenAiParam.Config.values());
    }

    @Override
    public AiVendorClientWrapper buildClientWrapper(AiModelConfigData configData) {
        ModelType modelType = ModelType.of(configData.getModelType());
        if (modelType == null) {
            logger.warn("未知的模型类型: {}, configId={}", configData.getModelType(), configData.getId());
            return null;
        }
        JsonConfigBox configParamBox = configData.getConfigParamBox();
        double temperature = configParamBox != null
                ? configParamBox.getDoubleParam("temperature", 0.7) : 0.7;

        return switch (modelType) {
            case CHAT -> buildChat(configData, temperature);
            case EMBEDDING -> buildEmbedding(configData);
            default -> {
                logger.warn("OpenAiVendor暂不支持模型类型: {}", modelType);
                yield null;
            }
        };
    }

    private AiVendorClientWrapper buildChat(AiModelConfigData configData, double temperature) {
        var syncModel = OpenAiChatModel.builder()
                .apiKey(configData.getApiKey())
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .temperature(temperature)
                .timeout(Duration.ofSeconds(120))
                .build();

        var streamingModel = OpenAiStreamingChatModel.builder()
                .apiKey(configData.getApiKey())
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .temperature(temperature)
                .timeout(Duration.ofSeconds(120))
                .build();

        return new AiVendorClientWrapper(configData, syncModel, streamingModel, null);
    }

    private AiVendorClientWrapper buildEmbedding(AiModelConfigData configData) {
        var embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(configData.getApiKey())
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .timeout(Duration.ofSeconds(60))
                .build();

        return new AiVendorClientWrapper(configData, null, null, embeddingModel);
    }

    @Override
    public List<String> listModel(String apiUrl, String apiKey) {
        return List.of();
    }
}
