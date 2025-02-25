package uw.ai.center.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import uw.ai.center.service.AiChatService;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.common.dto.ResponseData;

/**
 * 测试接口。
 */
@RestController
@RequestMapping("/user/ai")
@Tag(name = "AI接口")
@ResponseAdviceIgnore
public class AiChatController {
    private static final Logger logger = LoggerFactory.getLogger( AiChatController.class );

    /**
     * ChatClient 简单调用
     */
    @GetMapping("/generate")
    public ResponseData<String> generate(@RequestParam(defaultValue = "1") long config, @RequestParam(defaultValue = "你是谁？") String userPrompt) {
        return AiChatService.generate( config, AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), AuthServiceHelper.getUserName(),
                userPrompt );
    }

    /**
     * ChatClient 流式调用
     */
    @GetMapping("/chat")
    public Flux<String> chat(@RequestParam(defaultValue = "1") long config, @RequestParam(defaultValue = "0") long sessionId, @RequestParam(defaultValue = "你是谁？") String userPrompt) {
        return AiChatService.chat( config, AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), AuthServiceHelper.getUserName(),
                sessionId, userPrompt );
    }
}


