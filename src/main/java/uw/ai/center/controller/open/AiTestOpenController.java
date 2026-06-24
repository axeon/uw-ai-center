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
import uw.common.response.ResponseData;

/**
 * AiTest接口。
 * <p>开发联调用的测试接口（仅 dev/debug profile 启用），路径前缀 {@code /open/test}。
 */
@RestController
@RequestMapping("/open/test")
@Tag(name = "AI测试接口")
@ResponseAdviceIgnore
@Profile({"dev", "debug"})
public class AiTestOpenController {
    private static final Logger logger = LoggerFactory.getLogger(AiTestOpenController.class);

    /**
     * 同步生成测试入口（直接调 AiChatService）。
     *
     * @param param 聊天生成参数
     * @return 生成的文本
     */
    @PostMapping("/generate")
    public ResponseData<String> generate(@ModelAttribute AiChatGenerateParam param) {
        return AiChatService.generate(AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), AuthServiceHelper.getUserName(),
                param.getConfigId(), param.getSystemPrompt(), param.getUserPrompt(), param.getToolList(), param.getToolContext(), param.getFileList(), param.getRagLibIds());
    }

    /**
     * 直接调 AiChatService 的同步生成。
     *
     * @param configId 模型配置ID
     * @param query    用户提示词
     * @return 生成的文本
     */
    @GetMapping("/who")
    public ResponseData<String> who(int configId, String query) {
        return AiChatService.generate(AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), AuthServiceHelper.getUserName(),
                configId, null, query, null, null, null, null);
    }

    /**
     * 经 AiClientHelper 客户端 SDK 的同步生成。
     *
     * @param configId 模型配置ID
     * @param query    用户提示词
     * @return 生成的文本
     */
    @GetMapping("/who1")
    public ResponseData<String> who1(int configId, String query) {
        return AiClientHelper.generate(AiChatGenerateParam.builder().bindAuthInfo().configId(configId).userPrompt(query).build());
    }

    /**
     * 直接调 AiChatService 的流式生成。
     *
     * @param response HTTP 响应（设置 UTF-8）
     * @param configId 模型配置ID
     * @param query    用户提示词
     * @return SSE 事件流
     */
    @GetMapping("/who2")
    public Flux<String> who2(HttpServletResponse response, int configId, String query) {
        response.setCharacterEncoding("UTF-8");
        return AiChatService.chatGenerate(AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), AuthServiceHelper.getUserName(),
                configId, null, query, null, null, null, null);
    }

    /**
     * 经 AiClientHelper 客户端 SDK 的流式生成。
     *
     * @param response HTTP 响应（设置 UTF-8）
     * @param configId 模型配置ID
     * @param query    用户提示词
     * @return SSE 事件流
     */
    @GetMapping("/who3")
    public Flux<String> who3(HttpServletResponse response, int configId, String query) {
        response.setCharacterEncoding("UTF-8");
        return AiClientHelper.chatGenerate(AiChatGenerateParam.builder().bindAuthInfo().configId(configId).userPrompt(query).build());
    }

}


