package uw.ai.center.vendor.openai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
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
 * OpenAI 协议 CHAT 能力 Vendor。
 * <p>implements 列表即能力清单：{@link AiChatVendor}，类型系统直接表达"本 vendor 仅支持 CHAT"，
 * 无需点开源码看哪些方法被覆写。
 * <p>EMBEDDING 能力由独立的 {@link OpenAiEmbeddingVendor} 提供，数据库 vendor_class 字段
 * 按模型 modelType 区分填本类或子 Vendor 的 className。
 */
@Service
public class OpenAiChatVendor implements AiChatVendor {

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
     * <p>当前未接入 OpenAI 的模型列表接口，固定返回空列表。
     */
    @Override
    public List<String> listModel(String apiUrl, String apiKey) {
        return List.of();
    }
}
