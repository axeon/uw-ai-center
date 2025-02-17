package uw.ai.center.controller.open;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
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

    /**
     * ChatClient 简单调用
     */
    @GetMapping("/generate")
    public String simpleChat(String question) {
        ChatClient chatClient = AiVendorHelper.getChatClient( 1L );
        return AiVendorHelper.getChatClient( 1L ).prompt(question).call().content();
    }

    /**
     * ChatClient 流式调用
     */
    @GetMapping("/chat")
    public Flux<String> streamChat(String question,HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        return AiVendorHelper.getChatClient( 1L ).prompt(question).stream().content();
    }
}
