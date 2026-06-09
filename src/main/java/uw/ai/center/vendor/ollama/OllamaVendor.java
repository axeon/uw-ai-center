package uw.ai.center.vendor.ollama;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
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
 * Ollama 供应商实现（LangChain4j）。
 */
@Service
public class OllamaVendor implements AiVendor {

    private static final Logger logger = LoggerFactory.getLogger(OllamaVendor.class);

    @Override
    public String vendorName() {
        return "Ollama";
    }

    @Override
    public String vendorDesc() {
        return "Ollama via LangChain4j";
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
        return Arrays.asList(OllamaParam.Config.values());
    }

    @Override
    public AiVendorClientWrapper buildClientWrapper(AiModelConfigData configData) {
        ModelType modelType = ModelType.of(configData.getModelType());
        if (modelType == null) {
            logger.warn("未知的模型类型: {}, configId={}", configData.getModelType(), configData.getId());
            return null;
        }
        return switch (modelType) {
            case CHAT -> buildChat(configData);
            case EMBEDDING -> buildEmbedding(configData);
            default -> {
                logger.warn("OllamaVendor暂不支持模型类型: {}", modelType);
                yield null;
            }
        };
    }

    private AiVendorClientWrapper buildChat(AiModelConfigData configData) {
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

        return new AiVendorClientWrapper(configData, syncModel, streamingModel, null, null, null, null);
    }

    private AiVendorClientWrapper buildEmbedding(AiModelConfigData configData) {
        var embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .timeout(Duration.ofSeconds(60))
                .build();

        return new AiVendorClientWrapper(configData, null, null, embeddingModel, null, null, null);
    }

    @Override
    public List<String> listModel(String apiUrl, String apiKey) {
        return List.of();
    }
}
