package uw.ai.center.vendor.openai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.slf4j.Logger;
import org.apache.commons.lang3.StringUtils;
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
    public List<JsonConfigParam> vendorParam() {
        return Arrays.asList( OpenAiParam.Vendor.values() ); // 自动获取所有枚举项
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
        JsonConfigBox vendorParamBox = aiModelConfigData.getVendorParamBox();
        String apiKey = aiModelConfigData.getApiKey();
        String apiUrl = aiModelConfigData.getApiUrl();
        String modelMain = aiModelConfigData.getModelMain();
        String modelEmbed = aiModelConfigData.getModelEmbed();
        double temperature = vendorParamBox != null
                ? vendorParamBox.getDoubleParam("temperature", 0.7) : 0.7;

        var syncModel = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(apiUrl)
                .modelName(modelMain)
                .temperature(temperature)
                .timeout(Duration.ofSeconds(120))
                .build();

        var streamingModel = OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(apiUrl)
                .modelName(modelMain)
                .temperature(temperature)
                .timeout(Duration.ofSeconds(120))
                .build();

        var embeddingModel = StringUtils.isNotBlank(modelEmbed) ? OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .baseUrl(apiUrl)
                .modelName(modelEmbed)
                .timeout(Duration.ofSeconds(60))
                .build() : null;

        return new AiVendorClientWrapper(aiModelConfigData, syncModel, streamingModel, embeddingModel);
    }

    @Override
    public List<String> listModel(String apiUrl, String apiKey) {
        return List.of();
    }
}
