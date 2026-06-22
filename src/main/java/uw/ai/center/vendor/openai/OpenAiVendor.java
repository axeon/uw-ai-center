package uw.ai.center.vendor.openai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * OpenAI 供应商实现（LangChain4j）。
 * <p>基于 LangChain4j 的 OpenAiChatModel / OpenAiStreamingChatModel / OpenAiEmbeddingModel 构建客户端，
 * 支持 CHAT 与 EMBEDDING 两种模型类型。
 */
@Service
public class OpenAiVendor implements AiVendor {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiVendor.class);

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
     * @return OpenAI 配置参数集合（温度、最大 token、工具等）
     */
    @Override
    public List<JsonConfigParam> configParam() {
        return Arrays.asList(OpenAiParam.Config.values());
    }

    /**
     * 构建 CHAT 客户端：同时创建同步 ChatModel 与流式 StreamingChatModel。
     *
     * @param configData 模型配置数据
     * @return 封装了两个聊天模型的客户端
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
        var embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(configData.getApiKeyRaw())
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .timeout(Duration.ofSeconds(60))
                .build();

        return new EmbeddingClient(configData, this, embeddingModel);
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
