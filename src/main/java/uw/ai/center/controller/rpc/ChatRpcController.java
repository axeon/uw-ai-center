package uw.ai.center.controller.rpc;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
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

@RestController
@Tag(name = "ChatRPC接口")
@RequestMapping("/rpc/chat")
@Primary
@ResponseAdviceIgnore
public class ChatRpcController {

    /**
     * ChatClient 简单调用
     */
    @GetMapping("/generate")
    public ResponseData<String> generate(@RequestParam(defaultValue = "1") long configId, @RequestParam(defaultValue = "你是谁？") String userPrompt) {
        return AiChatService.generate( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), AuthServiceHelper.getUserName(), configId,
                userPrompt, null, null );
    }


    /**
     * ChatClient 初始化会话.
     *
     * @param configId
     * @param userPrompt
     * @return
     */
    @PostMapping(value = "/initSession")
    public ResponseData<AiSessionInfo> initSession(@RequestParam(defaultValue = "1") long configId, @RequestParam(defaultValue = "你是谁？") String userPrompt) {
        return AiChatService.initSession( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), AuthServiceHelper.getUserName(),
                configId, SessionType.CHAT.getValue(),
                userPrompt, 0, null );
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
    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chat(HttpServletResponse response, @RequestParam(defaultValue = "0") long sessionId,
                                              @RequestParam(defaultValue = "你是谁？") String userPrompt) {
        response.setCharacterEncoding( "UTF-8" );
        return AiChatService.chat( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), sessionId, userPrompt ).map( s -> ServerSentEvent.builder( s ).build() );
    }
}