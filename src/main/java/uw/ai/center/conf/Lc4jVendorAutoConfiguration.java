package uw.ai.center.conf;

import org.springframework.context.annotation.Configuration;
import uw.ai.center.vendor.AiVendorHelper;
import uw.ai.center.vendor.anthropic.AnthropicVendor;
import uw.ai.center.vendor.dashscope.DashScopeVendor;
import uw.ai.center.vendor.ollama.OllamaChatVendor;
import uw.ai.center.vendor.ollama.OllamaEmbeddingVendor;
import uw.ai.center.vendor.openai.OpenAiChatVendor;
import uw.ai.center.vendor.openai.OpenAiEmbeddingVendor;

/**
 * AI Vendor 自动配置。
 * <p>启动时将 Spring 容器托管的 Vendor 实例注册到 {@link AiVendorHelper}。
 * <p>每个 Vendor 类通过 implements 能力接口（{@code AiChatVendor} / {@code AiEmbeddingVendor} 等）
 * 表达自身支持的能力，类的 implements 列表即能力清单。
 * <p>同一协议的不同能力拆分到独立 Vendor 类（如 OpenAiChatVendor=CHAT / OpenAiEmbeddingVendor=EMBEDDING），
 * 各自注册到 {@code VENDOR_MAP}，数据库 {@code vendor_class} 字段按模型 modelType 填对应 Vendor 的 className。
 * <p>构造器首次调用 {@link AiVendorHelper#registerVendor} 时会触发 JVM 类初始化机制，
 * 执行 {@link AiVendorHelper} 的 static 块（注册 FusionCache），无需 {@code @Component} 显式加载。
 */
@Configuration
public class Lc4jVendorAutoConfiguration {

    public Lc4jVendorAutoConfiguration(OpenAiChatVendor openAiChatVendor,
                                       OpenAiEmbeddingVendor openAiEmbeddingVendor,
                                       OllamaChatVendor ollamaChatVendor,
                                       OllamaEmbeddingVendor ollamaEmbeddingVendor,
                                       DashScopeVendor dashScopeVendor,
                                       AnthropicVendor anthropicVendor) {
        AiVendorHelper.registerVendor(OpenAiChatVendor.class.getName(), openAiChatVendor);
        AiVendorHelper.registerVendor(OpenAiEmbeddingVendor.class.getName(), openAiEmbeddingVendor);
        AiVendorHelper.registerVendor(OllamaChatVendor.class.getName(), ollamaChatVendor);
        AiVendorHelper.registerVendor(OllamaEmbeddingVendor.class.getName(), ollamaEmbeddingVendor);
        AiVendorHelper.registerVendor(DashScopeVendor.class.getName(), dashScopeVendor);
        AiVendorHelper.registerVendor(AnthropicVendor.class.getName(), anthropicVendor);
    }
}
