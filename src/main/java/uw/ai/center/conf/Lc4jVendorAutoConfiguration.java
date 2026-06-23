package uw.ai.center.conf;

import org.springframework.context.annotation.Configuration;
import uw.ai.center.vendor.AiVendorHelper;
import uw.ai.center.vendor.anthropic.AnthropicVendor;
import uw.ai.center.vendor.dashscope.DashScopeVendor;
import uw.ai.center.vendor.ollama.OllamaVendor;
import uw.ai.center.vendor.openai.OpenAiVendor;

/**
 * AI Vendor 自动配置。
 * <p>启动时将 Spring 容器托管的 Vendor 实例注册到 {@link AiVendorHelper}。
 * <p>每个 Vendor 类通过 implements 能力接口（{@code ChatVendor} / {@code EmbeddingVendor} 等）
 * 表达自身支持的能力，类的 implements 列表即能力清单；数据库 {@code vendor_class} 字段
 * 只表达"协议"，不关心具体能力。
 */
@Configuration
public class Lc4jVendorAutoConfiguration {

    public Lc4jVendorAutoConfiguration(OpenAiVendor openAiVendor,
                                       OllamaVendor ollamaVendor,
                                       DashScopeVendor dashScopeVendor,
                                       AnthropicVendor anthropicVendor) {
        AiVendorHelper.registerVendor(OpenAiVendor.class.getName(), openAiVendor);
        AiVendorHelper.registerVendor(OllamaVendor.class.getName(), ollamaVendor);
        AiVendorHelper.registerVendor(DashScopeVendor.class.getName(), dashScopeVendor);
        AiVendorHelper.registerVendor(AnthropicVendor.class.getName(), anthropicVendor);
    }
}
