package uw.ai.center.controller.open;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import uw.ai.center.vendor.AiVendorHelper;

/**
 * 测试接口。
 */
@RestController
@RequestMapping("/open/test")
@Tag(name = "测试接口")
public class AiTestController {

    private static final String DEFAULT_PROMPT = "你好，介绍下你自己！请用中文回答。";

    /**
     * ChatClient 简单调用
     */
    @GetMapping("/generate")
    public String simpleChat() {
        return AiVendorHelper.buildChatClient( 1L ).prompt(DEFAULT_PROMPT).call().content();
    }

    /**
     * ChatClient 流式调用
     */
    @GetMapping("/chat")
    public Flux<String> streamChat(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        return AiVendorHelper.buildChatClient( 1L ).prompt(DEFAULT_PROMPT).stream().content();
    }
}
