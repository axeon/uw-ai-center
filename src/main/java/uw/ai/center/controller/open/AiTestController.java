package uw.ai.center.controller.open;

import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import uw.ai.center.vendor.AiVendorHelper;
import uw.auth.service.annotation.ResponseAdviceIgnore;

/**
 * 测试接口。
 */
@RestController
@RequestMapping("/open/test")
@Tag(name = "测试接口")
@ResponseAdviceIgnore
public class AiTestController {

    /**
     * ChatClient 简单调用
     */
    @GetMapping("/generate")
    public String simpleChat(@RequestParam(defaultValue = "你是谁？") String question) {
        ChatClient chatClient = AiVendorHelper.getChatClient( 1L );
        return AiVendorHelper.getChatClient( 1L ).prompt(question).call().content();
    }

    /**
     * ChatClient 流式调用
     */
    @GetMapping("/chat")
    @ResponseAdviceIgnore
    public Flux<ChatResponse> streamChat(@RequestParam(required=false,defaultValue = "你是谁？") String question, HttpServletResponse response) {
        if(StringUtils.isEmpty(question)){
            question = "你是谁？";
        }
        response.setCharacterEncoding("UTF-8");
        return AiVendorHelper.getChatClient( 1L ).prompt(question).stream().chatResponse();
    }
}
