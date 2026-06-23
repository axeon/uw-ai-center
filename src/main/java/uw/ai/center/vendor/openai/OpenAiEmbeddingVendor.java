package uw.ai.center.vendor.openai;

import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.springframework.stereotype.Service;
import uw.ai.center.vendor.AiEmbeddingVendor;
import uw.ai.center.vendor.client.EmbeddingClient;
import uw.ai.center.vo.AiModelConfigData;
import uw.common.app.vo.JsonConfigParam;

import java.time.Duration;
import java.util.List;

/**
 * OpenAI 协议 EMBEDDING 能力 Vendor。
 * <p>从原聚合 Vendor 拆分而来，专门负责构建 OpenAI Embedding 客户端。
 * <p>CHAT 能力由 {@link OpenAiChatVendor} 提供；本类独立注册到 {@code VENDOR_MAP}，
 * 数据库 {@code vendor_class} 字段填本类全限定名。
 */
@Service
public class OpenAiEmbeddingVendor implements AiEmbeddingVendor {

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
     * @return "OpenAI Embedding via LangChain4j"
     */
    @Override
    public String vendorDesc() {
        return "OpenAI Embedding via LangChain4j";
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
     * @return 空列表（OpenAI Embedding 当前无可调参数）
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
        var embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(configData.getApiKeyRaw())
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
