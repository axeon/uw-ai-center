package uw.ai.center.vendor.ollama;

import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.springframework.stereotype.Service;
import uw.ai.center.vendor.AiEmbeddingVendor;
import uw.ai.center.vendor.client.EmbeddingClient;
import uw.ai.center.vo.AiModelConfigData;
import uw.common.app.vo.JsonConfigParam;

import java.time.Duration;
import java.util.List;

/**
 * Ollama 协议 EMBEDDING 能力 Vendor。
 * <p>从原聚合 Vendor 拆分而来，专门负责构建 Ollama Embedding 客户端。
 * <p>CHAT 能力由 {@link OllamaChatVendor} 提供；本类独立注册到 {@code VENDOR_MAP}，
 * 数据库 {@code vendor_class} 字段填本类全限定名。
 */
@Service
public class OllamaEmbeddingVendor implements AiEmbeddingVendor {

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
     * @return "Ollama Embedding via LangChain4j"
     */
    @Override
    public String vendorDesc() {
        return "Ollama Embedding via LangChain4j";
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
     * @return 空列表（Ollama Embedding 当前无可调参数）
     */
    @Override
    public List<JsonConfigParam> configParam() {
        return List.of();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EmbeddingClient buildEmbeddingClient(AiModelConfigData configData) {
        var embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .timeout(Duration.ofSeconds(60))
                .build();

        return new EmbeddingClient(configData, embeddingModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> listModel(String apiUrl, String apiKey) {
        return List.of();
    }
}
