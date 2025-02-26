package uw.ai.center.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import uw.ai.center.constant.SessionType;
import uw.ai.center.dto.AiSessionInfoQueryParam;
import uw.ai.center.dto.AiSessionMsgQueryParam;
import uw.ai.center.entity.AiSessionInfo;
import uw.ai.center.entity.AiSessionMsg;
import uw.ai.center.service.AiChatService;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.common.dto.ResponseData;
import uw.dao.DataList;

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
        return AiChatService.generate( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), AuthServiceHelper.getUserName(), config,
                userPrompt );
    }

    /**
     * ChatClient 初始化会话.
     *
     * @param config
     * @param userPrompt
     * @return
     */
    @PostMapping(value = "/init")
    public ResponseData<AiSessionInfo> initSession(@RequestParam(defaultValue = "1") long config, @RequestParam(defaultValue = "你是谁？") String userPrompt) {
        return AiChatService.initSession( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), AuthServiceHelper.getUserName(),
                SessionType.CHAT.getValue(),
                "", userPrompt, 0 );
    }

    /**
     * ChatClient 列出会话列表.
     *
     * @param queryParam
     * @return
     */
    @GetMapping("/listSessionInfo")
    public ResponseData<DataList<AiSessionInfo>> listSessionInfo(AiSessionInfoQueryParam queryParam) {
        return AiChatService.listSessionInfo( queryParam );
    }

    /**
     * ChatClient 列出会话消息.
     *
     * @param queryParam
     * @return
     */
    @GetMapping("/listSessionMsg")
    public ResponseData<DataList<AiSessionMsg>> listSessionMsg(AiSessionMsgQueryParam queryParam) {
        return AiChatService.listSessionMsg( queryParam );
    }

    /**
     * ChatClient 流式调用
     */
    @GetMapping("/chat")
    public Flux<String> chat(HttpServletResponse response, @RequestParam(defaultValue = "1") long config, @RequestParam(defaultValue = "0") long sessionId,
                             @RequestParam(defaultValue = "你是谁？") String userPrompt) {
        response.setCharacterEncoding( "UTF-8" );
        return AiChatService.chat( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), AuthServiceHelper.getUserName(), config,
                sessionId, userPrompt );
    }
}


