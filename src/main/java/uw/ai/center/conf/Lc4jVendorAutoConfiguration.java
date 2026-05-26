package uw.ai.center.conf;

import org.springframework.context.annotation.Configuration;
import uw.ai.center.vendor.AiVendorHelper;
import uw.ai.center.vendor.ollama.OllamaVendor;
import uw.ai.center.vendor.openai.OpenAiVendor;

/**
 * AI Vendor 自动配置。
 */
@Configuration
public class Lc4jVendorAutoConfiguration {

    public Lc4jVendorAutoConfiguration(OpenAiVendor openAiVendor, OllamaVendor ollamaVendor) {
        AiVendorHelper.registerOpenAiVendor(OpenAiVendor.class.getName(), openAiVendor);
        AiVendorHelper.registerOllamaVendor(OllamaVendor.class.getName(), ollamaVendor);
    }
}
