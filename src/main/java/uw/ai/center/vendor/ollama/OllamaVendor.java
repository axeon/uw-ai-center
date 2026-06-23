package uw.ai.center.vendor.ollama;

import org.springframework.stereotype.Service;
import uw.ai.center.constant.ModelType;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vendor.client.AiModelClient;
import uw.ai.center.vo.AiModelConfigData;
import uw.common.app.vo.JsonConfigParam;

import java.util.Arrays;
import java.util.List;

/**
 * Ollama 协议入口。
 * <p>数据库 {@code ai_model_config.vendor_class} 存的是本类的全限定名，作为"协议标识"稳定不变。
 * <p>本类承担两类职责：
 * <ul>
 *   <li>提供协议元信息（vendorName/vendorDesc/configParam/listModel 等）</li>
 *   <li>按 {@code modelType} 委托构建到独立的能力子类（{@link OllamaChatVendor} / {@link OllamaEmbeddingVendor}），
 *       让 chat 与 embedding 的实际构建逻辑物理隔离到不同文件</li>
 * </ul>
 */
@Service
public class OllamaVendor implements AiVendor {

    private final OllamaChatVendor chatVendor;
    private final OllamaEmbeddingVendor embeddingVendor;

    public OllamaVendor(OllamaChatVendor chatVendor, OllamaEmbeddingVendor embeddingVendor) {
        this.chatVendor = chatVendor;
        this.embeddingVendor = embeddingVendor;
    }

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

    @Override
    public AiModelClient buildClient(AiModelConfigData configData) {
        ModelType modelType = ModelType.of(configData.getModelType());
        return switch (modelType) {
            case CHAT -> chatVendor.buildChatClient(configData);
            case EMBEDDING -> embeddingVendor.buildEmbeddingClient(configData);
            default -> throw new IllegalStateException(
                    "OllamaVendor 不支持模型类型[" + configData.getModelType() + "]");
        };
    }

    @Override
    public List<String> listModel(String apiUrl, String apiKey) {
        return List.of();
    }
}
