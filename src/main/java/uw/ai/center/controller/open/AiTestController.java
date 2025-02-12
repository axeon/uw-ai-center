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

/**
 * 测试接口。
 */
@RestController
@RequestMapping("/open/test")
@Tag(name = "测试接口")
public class AiTestController {

    private static final String DEFAULT_PROMPT = "你好，介绍下你自己！请用中文回答。";


    private final ChatClient ollamaiChatClient;


    public AiTestController(ChatModel chatModel) {

        // 构造时，可以设置 ChatClient 的参数
        // {@link org.springframework.ai.chat.client.ChatClient};
        this.ollamaiChatClient = ChatClient.builder(chatModel)
                // 实现 Chat Memory 的 Advisor
                // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory())
                )
                // 实现 Logger 的 Advisor
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(
                        OllamaOptions.builder().topP( 0.7 ).model( "deepseek-r1:7b" )
                                .build()
                )
                .build();
    }

    /**
     * ChatClient 简单调用
     */
    @GetMapping("/generate")
    public String simpleChat() {
        return ollamaiChatClient.prompt(DEFAULT_PROMPT).call().content();
    }

    /**
     * ChatClient 流式调用
     */
    @GetMapping("/chat")
    public Flux<String> streamChat(HttpServletResponse response) {

        response.setCharacterEncoding("UTF-8");
        return ollamaiChatClient.prompt(DEFAULT_PROMPT).stream().content();
    }
}
