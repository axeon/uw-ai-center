package uw.ai.center.controller.rpc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import uw.ai.center.vendor.AiVendorHelper;
import uw.ai.rpc.AiChatRpc;
import uw.ai.vo.AiChatGenerateParam;
import uw.ai.vo.AiChatMsgParam;
import uw.ai.vo.AiChatSessionParam;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.auth.service.constant.UserType;
import uw.common.response.ResponseData;
import uw.common.data.PageList;

/**
 * 聊天 RPC 接口（供其他微服务通过 RPC 调用 AI 对话能力）。
 * <p>实现 {@link AiChatRpc}，提供同步生成、流式生成（SSE）、多轮聊天、会话初始化与历史查询。
 * configId 与 configCode 二选一，通过 {@link AiVendorHelper#resolveConfigId} 解析。
 */
@RestController
@Tag(name = "ChatRPC接口")
@RequestMapping("/rpc/chat")
@Primary
@ResponseAdviceIgnore
public class AiChatRpcController implements AiChatRpc {

    private static final Logger log = LoggerFactory.getLogger(AiChatRpcController.class);

    /**
     * ChatClient 简单调用
     */
    @Override
    @PostMapping("/generate")
    @Operation(summary = "生成数据", description = "生成数据")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<String> generate(@ModelAttribute AiChatGenerateParam param) {
        Long configId = AiVendorHelper.resolveConfigId(param.getConfigId(), param.getConfigCode());
        if (configId == null) {
            return ResponseData.errorMsg("configId 和 configCode 不能同时为空，或 configCode 无效");
        }
        return AiChatService.generate(param.getSaasId(), param.getUserId(), param.getUserType(), param.getUserInfo(), configId, param.getSystemPrompt(), param.getUserPrompt(), param.getToolList(), param.getToolContext(), param.getFileList(), param.getRagLibIds());
    }

    /**
     * ChatClient 简单调用
     */
    @Override
    @PostMapping(value = "chatGenerate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "生成数据", description = "生成数据")
    @MscPermDeclare(user = UserType.RPC)
    public Flux<String> chatGenerate(@ModelAttribute AiChatGenerateParam param) {
        Long configId = AiVendorHelper.resolveConfigId(param.getConfigId(), param.getConfigCode());
        if (configId == null) {
            return Flux.just(ResponseData.errorMsg("configId 和 configCode 不能同时为空，或 configCode 无效").toString());
        }
        return AiChatService.chatGenerate(param.getSaasId(), param.getUserId(), param.getUserType(), param.getUserInfo(), configId, param.getSystemPrompt(), param.getUserPrompt(), param.getToolList(), param.getToolContext(), param.getFileList(), param.getRagLibIds());
    }

    /**
     * ChatClient 流式调用
     */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "聊天", description = "聊天")
    @MscPermDeclare(user = UserType.RPC)
    public Flux<ServerSentEvent<String>> chat(HttpServletResponse response, @ModelAttribute AiChatMsgParam param) {
        response.setCharacterEncoding("UTF-8");
        Long configId = AiVendorHelper.resolveConfigId(param.getConfigId(), param.getConfigCode());
        if (configId == null) {
            return Flux.just(ServerSentEvent.builder(ResponseData.errorMsg("configId 和 configCode 不能同时为空，或 configCode 无效").toString()).build());
        }
        return AiChatService.chat(param.getSaasId(), param.getUserId(), param.getUserType(), param.getUserInfo(), configId, param.getSessionId(), param.getSystemPrompt(), param.getUserPrompt(), param.getToolList(), param.getToolContext(), param.getFileList(), param.getRagLibIds()).map(s -> ServerSentEvent.builder(s == null ? "" : s).build());
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
        return AiChatService.initSession(param.getSaasId(), param.getUserId(), param.getUserType(), param.getUserInfo(), param.getConfigId(), SessionType.CHAT.getValue(), param.getUserPrompt(), 0, param.getSystemPrompt(), param.getToolList(), param.getRagLibIds());
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
    public ResponseData<PageList<AiSessionInfo>> listSessionInfo(AiSessionInfoQueryParam queryParam) {
        return AiChatService.listSessionInfo(queryParam);
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
    public ResponseData<PageList<AiSessionMsg>> listSessionMsg(AiSessionMsgQueryParam queryParam) {
        return AiChatService.listSessionMsg(queryParam);
    }

}