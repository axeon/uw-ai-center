package uw.ai.center.vendor.anthropic;

import org.springframework.stereotype.Service;
import uw.ai.center.constant.ModelType;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vendor.client.AiModelClient;
import uw.ai.center.vo.AiModelConfigData;
import uw.common.app.vo.JsonConfigParam;

import java.util.Arrays;
import java.util.List;

/**
 * Anthropic 协议入口。
 * <p>数据库 {@code ai_model_config.vendor_class} 存的是本类的全限定名，作为"协议标识"稳定不变。
 * <p>同一 Vendor 既能接官方 Anthropic（Claude 系列），也能接 Anthropic 兼容代理
 * （阿里云百炼调千问、Kimi、智谱 GLM-4.5 等）：{@code baseUrl + apiKey} 在 ai_model_api 中按供应商填写。
 * <p>由于 langchain4j-anthropic 不提供 EmbeddingModel，本协议仅支持 {@link ModelType#CHAT}。
 */
@Service
public class AnthropicVendor implements AiVendor {

    private final AnthropicChatVendor chatVendor;

    public AnthropicVendor(AnthropicChatVendor chatVendor) {
        this.chatVendor = chatVendor;
    }

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

    @Override
    public AiModelClient buildClient(AiModelConfigData configData) {
        ModelType modelType = ModelType.of(configData.getModelType());
        return switch (modelType) {
            case CHAT -> chatVendor.buildChatClient(configData);
            default -> throw new IllegalStateException(
                    "AnthropicVendor 不支持模型类型[" + configData.getModelType() + "]");
        };
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
