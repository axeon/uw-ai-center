package uw.ai.center.controller.open;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uw.ai.center.service.AiChatService;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.common.dto.ResponseData;

/**
 * 测试接口。
 */
@RestController
@RequestMapping("/open/ai")
@Tag(name = "AI接口")
@ResponseAdviceIgnore
public class AiChatController {
    private static final Logger logger = LoggerFactory.getLogger( AiChatController.class );

    /**
     * ChatClient 简单调用
     */
    @GetMapping("/generate")
    public ResponseData<String> generate(@RequestParam(defaultValue = "1") long configId, @RequestParam(defaultValue = "你是谁？") String userPrompt,
                                         @RequestParam(defaultValue = "") String systemPrompt, @RequestParam(defaultValue = "") String toolList) {
        return AiChatService.generate( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), "guest", configId,
                userPrompt, systemPrompt, null, null );
    }

}


