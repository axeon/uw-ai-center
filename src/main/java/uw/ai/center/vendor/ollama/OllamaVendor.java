package uw.ai.center.vendor.ollama;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import org.springframework.stereotype.Service;
import uw.ai.center.vo.AiModelConfigData;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vendor.client.ChatClient;
import uw.ai.center.vendor.client.EmbeddingClient;
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

    /**
     * 构建 CHAT 客户端：同时创建同步 OllamaChatModel 与流式 OllamaStreamingChatModel。
     *
     * @param configData 模型配置数据
     * @return 封装了两个聊天模型的客户端
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

        return new ChatClient(configData, this, syncModel, streamingModel);
    }

    /**
     * 构建 EMBEDDING 客户端，用于将文本转向量供 RAG 检索。
     *
     * @param configData 模型配置数据
     * @return 封装了嵌入模型的客户端
     */
    @Override
    public EmbeddingClient buildEmbeddingClient(AiModelConfigData configData) {
        var embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .timeout(Duration.ofSeconds(60))
                .build();

        return new EmbeddingClient(configData, this, embeddingModel);
    }

    @Override
    public List<String> listModel(String apiUrl, String apiKey) {
        return List.of();
    }
}
