package uw.ai.center.controller.rpc;

import io.swagger.v3.oas.annotations.Operation;
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
import uw.ai.rpc.AiChatRpc;
import uw.ai.vo.AiChatGenerateParam;
import uw.ai.vo.AiChatMsgParam;
import uw.ai.vo.AiChatSessionParam;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.auth.service.constant.UserType;
import uw.common.dto.ResponseData;
import uw.dao.DataList;

@RestController
@Tag(name = "ChatRPC接口")
@RequestMapping("/rpc/chat")
@Primary
@ResponseAdviceIgnore
public class AiChatRpcController implements AiChatRpc {

    /**
     * ChatClient 简单调用
     */
    @Override
    @PostMapping("/generate")
    @Operation(summary = "生成数据", description = "生成数据")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<String> generate(@ModelAttribute AiChatGenerateParam param) {
        return AiChatService.generate( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), AuthServiceHelper.getUserName(),
                param.getConfigId(), param.getSystemPrompt(), param.getUserPrompt(), param.getToolList(), param.getToolContext(), param.getFileList(), param.getRagLibIds() );
    }

    /**
     * ChatClient 流式调用
     */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "聊天", description = "聊天")
    @MscPermDeclare(user = UserType.RPC)
    public Flux<ServerSentEvent<String>> chat(HttpServletResponse response, @ModelAttribute AiChatMsgParam param) {
        response.setCharacterEncoding( "UTF-8" );
        return AiChatService.chat(AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), AuthServiceHelper.getUserName(), param.getSessionId(), param.getSystemPrompt(), param.getUserPrompt(), param.getToolList(), param.getToolContext(), param.getFileList(), param.getRagLibIds()).map(s -> ServerSentEvent.builder(s == null ? "" : s).build());
    }

    /**
     * ChatClient 初始化会话.
     *
     * @return
     */
    @PostMapping(value = "/initSession")
    @Operation(summary = "初始化会话", description = "初始化会话")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<AiSessionInfo> initSession(@ModelAttribute AiChatSessionParam param) {
        return AiChatService.initSession( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), AuthServiceHelper.getUserName(),
                param.getConfigId(), SessionType.CHAT.getValue(), param.getUserPrompt(), 0, param.getSystemPrompt(), param.getToolList(), param.getRagLibIds() );
    }

    /**
     * ChatClient 列出会话信息.
     *
     * @param queryParam
     * @return
     */
    @GetMapping("/listSessionInfo")
    @Operation(summary = "列出会话信息", description = "列出会话信息")
    @MscPermDeclare(user = UserType.RPC)
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
    @Operation(summary = "列出会话消息", description = "列出会话消息")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<DataList<AiSessionMsg>> listSessionMsg(AiSessionMsgQueryParam queryParam) {
        return AiChatService.listSessionMsg( queryParam );
    }

}