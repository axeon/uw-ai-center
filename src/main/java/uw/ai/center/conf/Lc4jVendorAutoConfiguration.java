package uw.ai.center.conf;

import org.springframework.context.annotation.Configuration;
import uw.ai.center.vendor.AiVendorHelper;
import uw.ai.center.vendor.dashscope.DashScopeVendor;
import uw.ai.center.vendor.ollama.OllamaVendor;
import uw.ai.center.vendor.openai.OpenAiVendor;

/**
 * AI Vendor 自动配置。
 * <p>构造时将 Spring 容器托管的 OpenAiVendor / OllamaVendor / DashScopeVendor 注册到 {@link AiVendorHelper}，
 * 使其可按 vendorClass 被检索和构建客户端。
 */
@Configuration
public class Lc4jVendorAutoConfiguration {

    public Lc4jVendorAutoConfiguration(OpenAiVendor openAiVendor, OllamaVendor ollamaVendor, DashScopeVendor dashScopeVendor) {
        AiVendorHelper.registerOpenAiVendor(OpenAiVendor.class.getName(), openAiVendor);
        AiVendorHelper.registerOllamaVendor(OllamaVendor.class.getName(), ollamaVendor);
        AiVendorHelper.registerVendor(DashScopeVendor.class.getName(), dashScopeVendor);
    }
}
