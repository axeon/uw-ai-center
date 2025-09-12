package uw.ai.center.controller.open;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import uw.ai.AiClientHelper;
import uw.ai.center.service.AiChatService;
import uw.ai.vo.AiChatGenerateParam;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.common.dto.ResponseData;

/**
 * AiTest接口。
 */
@RestController
@RequestMapping("/open/test")
@Tag(name = "AiTest接口")
@ResponseAdviceIgnore
@Profile({"dev", "debug"})
public class AiTestOpenController {
    private static final Logger logger = LoggerFactory.getLogger(AiTestOpenController.class);

    /**
     * ChatClient 简单调用
     */
    @PostMapping("/generate")
    public ResponseData<String> generate(@ModelAttribute AiChatGenerateParam param) {
        return AiChatService.generate(AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), "anonymous",
                param.getConfigId(), param.getSystemPrompt(), param.getUserPrompt(), param.getToolList(), param.getToolContext(), param.getFileList(), param.getRagLibIds());
    }

    /**
     * ChatClient 简单调用
     */
    @GetMapping("/who")
    public ResponseData<String> who() {
        return AiChatService.generate(AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), "anonymous",
                2, null, "你是谁？", null, null, null, null);
    }

    /**
     * ChatClient 简单调用
     */
    @GetMapping("/who1")
    public ResponseData<String> who1() {
        return AiClientHelper.generate(AiChatGenerateParam.builder().configId(2).userPrompt("你是谁？").build());
    }

    /**
     * ChatClient 简单调用
     */
    @GetMapping("/who2")
    public Flux<String> who2(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        return AiChatService.chatGenerate(AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), "anonymous",
                2, null, "你是谁？", null, null, null, null);
    }

    /**
     * ChatClient 简单调用
     */
    @GetMapping("/who3")
    public Flux<String> who3(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        return AiClientHelper.chatGenerate(AiChatGenerateParam.builder().configId(2).userPrompt("你是谁？").build());
    }

}


