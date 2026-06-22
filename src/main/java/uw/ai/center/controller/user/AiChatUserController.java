package uw.ai.center.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
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
import uw.ai.vo.AiChatGenerateParam;
import uw.ai.vo.AiChatMsgParam;
import uw.ai.vo.AiChatSessionParam;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.common.response.ResponseData;
import uw.common.data.PageList;

/**
 * 普通用户聊天接口。
 * <p>面向 C 端用户，提供同步生成、流式聊天（SSE）、会话初始化与历史查询能力。
 * 用户身份（saasId/userId 等）由 AuthServiceHelper 自动注入，无需前端传递。
 */
@RestController
@Tag(name = "ChatUser接口")
@RequestMapping("/user/chat")
@ResponseAdviceIgnore
public class AiChatUserController {

    /**
     * ChatClient 简单调用
     */
    @PostMapping("/generate")
    @Operation(summary = "生成数据", description = "生成数据")
    @MscPermDeclare(auth = AuthType.NONE, log = ActionLog.BASE)
    public ResponseData<String> generate(@ModelAttribute AiChatGenerateParam param) {
        return AiChatService.generate(AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), AuthServiceHelper.getUserName(), param.getConfigId(), param.getSystemPrompt(), param.getUserPrompt(), param.getToolList(), param.getToolContext(), param.getFileList(), param.getRagLibIds());
    }

    /**
     * ChatClient 流式调用
     */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "聊天", description = "聊天")
    @MscPermDeclare(auth = AuthType.NONE, log = ActionLog.BASE)
    public Flux<ServerSentEvent<String>> chat(HttpServletResponse response, @ModelAttribute AiChatMsgParam param) {
        response.setCharacterEncoding("UTF-8");
        return AiChatService.chat(AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), AuthServiceHelper.getUserName(), param.getConfigId(), param.getSessionId(), param.getSystemPrompt(), param.getUserPrompt(), param.getToolList(), param.getToolContext(), param.getFileList(), param.getRagLibIds()).map(s -> ServerSentEvent.builder(s == null ? "" : s).build());
    }

    /**
     * ChatClient 初始化会话.
     *
     * @param param 会话参数（含 configId、sessionName、windowSize、systemPrompt、工具、RAG库）
     * @return 新建的会话信息
     */
    @PostMapping(value = "/initSession")
    @Operation(summary = "初始化会话", description = "初始化会话")
    @MscPermDeclare(auth = AuthType.NONE, log = ActionLog.BASE)
    public ResponseData<AiSessionInfo> initSession(@ModelAttribute AiChatSessionParam param) {
        return AiChatService.initSession(AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), AuthServiceHelper.getUserName(), param.getConfigId(), SessionType.CHAT.getValue(), param.getUserPrompt(), param.getWindowSize(), param.getSystemPrompt(), param.getToolList(), param.getRagLibIds());
    }

    /**
     * ChatClient 列出会话信息.
     *
     * @param queryParam 查询参数（自动绑定当前用户 saasId/userId，防越权）
     * @return 会话分页列表
     */
    @GetMapping("/listSessionInfo")
    @Operation(summary = "列出会话信息", description = "列出会话信息")
    @MscPermDeclare(auth = AuthType.NONE, log = ActionLog.BASE)
    public ResponseData<PageList<AiSessionInfo>> listSessionInfo(AiSessionInfoQueryParam queryParam) {
        // 显式覆盖身份字段，防止前端篡改 userId 读取同租户其他用户的会话（水平越权防护）
        queryParam.saasId(AuthServiceHelper.getSaasId()).userId(AuthServiceHelper.getUserId());
        return AiChatService.listSessionInfo(queryParam);
    }

    /**
     * ChatClient 列出会话消息.
     *
     * @param queryParam 查询参数（自动绑定当前用户 saasId/userId，防越权）
     * @return 会话消息分页列表
     */
    @GetMapping("/listSessionMsg")
    @Operation(summary = "列出会话消息", description = "列出会话消息")
    @MscPermDeclare(auth = AuthType.NONE, log = ActionLog.BASE)
    public ResponseData<PageList<AiSessionMsg>> listSessionMsg(AiSessionMsgQueryParam queryParam) {
        // 显式覆盖身份字段，防止前端篡改 userId 读取同租户其他用户的会话消息（水平越权防护）
        queryParam.saasId(AuthServiceHelper.getSaasId()).userId(AuthServiceHelper.getUserId());
        return AiChatService.listSessionMsg(queryParam);
    }

}