package uw.ai.center.conf;

import org.springframework.context.annotation.Configuration;
import uw.ai.center.vendor.AiVendorHelper;
import uw.ai.center.vendor.anthropic.AnthropicVendor;
import uw.ai.center.vendor.dashscope.DashScopeVendor;
import uw.ai.center.vendor.ollama.OllamaVendor;
import uw.ai.center.vendor.openai.OpenAiVendor;

/**
 * AI Vendor 自动配置。
 * <p>启动时将 Spring 容器托管的协议入口 Vendor 注册到 {@link AiVendorHelper}。
 * <p>每个协议入口内部按能力委托给独立的子类（如 {@code OpenAiChatVendor} / {@code OpenAiEmbeddingVendor}），
 * 既保证代码职责单一，又让数据库 {@code vendor_class} 字段保持稳定（只表达"协议"，不关心具体能力）。
 * <p>能力子类是 Spring bean 但不注册到 VENDOR_MAP —— 它们只对协议入口可见，不对外暴露。
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
