package uw.ai.center.vendor.openai;

import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.springframework.stereotype.Service;
import uw.ai.center.vendor.client.EmbeddingClient;
import uw.ai.center.vo.AiModelConfigData;

import java.time.Duration;

/**
 * OpenAI 协议 EMBEDDING 能力实现。
 * <p>只承担嵌入客户端的构建逻辑；元信息由协议入口 {@link OpenAiVendor} 统一提供。
 * 作为 Spring bean 存在，由协议入口注入并按 model_type 委托调用。
 */
@Service
public class OpenAiEmbeddingVendor {

    /**
     * 构建 EMBEDDING 客户端。
     */
    public EmbeddingClient buildEmbeddingClient(AiModelConfigData configData) {
        var embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(configData.getApiKeyRaw())
                .baseUrl(configData.getApiUrl())
                .modelName(configData.getModelName())
                .timeout(Duration.ofSeconds(60))
                .build();

        return new EmbeddingClient(configData, embeddingModel);
    }
}
