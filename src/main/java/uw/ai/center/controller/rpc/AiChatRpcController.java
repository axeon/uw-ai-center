package uw.ai.center.controller.rpc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.WebDataBinder;
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
import uw.ai.vo.AiToolCallInfo;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.auth.service.constant.UserType;
import uw.common.response.ResponseData;
import uw.common.data.PageList;
import uw.common.util.JsonUtils;

import java.beans.PropertyEditorSupport;

/**
 * 聊天 RPC 接口（供其他微服务通过 RPC 调用 AI 对话能力）。
 * <p>实现 {@link AiChatRpc}，提供同步生成、流式生成（SSE）、多轮聊天、会话初始化与历史查询。
 * configId 与 configCode 二选一，通过 {@link AiVendorHelper#resolveConfigId} 解析。
 */
@RestController
@Tag(name = "AI聊天接口")
@RequestMapping("/rpc/chat")
@Primary
@ResponseAdviceIgnore
public class AiChatRpcController implements AiChatRpc {

    private static final Logger log = LoggerFactory.getLogger(AiChatRpcController.class);

    /**
     * 注册 String→AiToolCallInfo 的属性编辑器。
     * <p>RPC 客户端（uw-ai jar）以 multipart 表单提交 toolList，对象会被序列化成 JSON 字符串，
     * 此处将其反序列化回 AiToolCallInfo，避免 @ModelAttribute 数据绑定报 typeMismatch 400。
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(AiToolCallInfo.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text == null || text.isBlank()) {
                    setValue(null);
                    return;
                }
                setValue(JsonUtils.parse(text, AiToolCallInfo.class));
            }
        });
    }

    /**
     * 同步生成：单轮同步对话，支持工具调用/RAG/附件。
     *
     * @param param 聊天生成参数（含身份、configId/configCode、提示词、工具列表、文件、RAG库）
     * @return 生成的文本
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
     * 流式生成：SSE 流式下发单轮对话结果。
     *
     * @param param 聊天生成参数
     * @return SSE 事件流
     */
    @Override
    @PostMapping(value = "/chatGenerate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
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
     * 多轮聊天：基于 sessionId 加载历史、流式返回（SSE）。
     *
     * @param response HTTP 响应（设置 UTF-8 编码）
     * @param param    聊天参数（含 sessionId、提示词、工具列表、文件、RAG库）
     * @return SSE 事件流
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
     * 初始化一个 CHAT 类型会话，返回 sessionId。
     *
     * @param param 会话参数（含身份、configId、sessionName、windowSize、systemPrompt、工具、RAG库）
     * @return 新建的会话信息
     */
    @PostMapping(value = "/initSession")
    @Operation(summary = "初始化会话", description = "初始化会话")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<AiSessionInfo> initSession(@ModelAttribute AiChatSessionParam param) {
        return AiChatService.initSession(param.getSaasId(), param.getUserId(), param.getUserType(), param.getUserInfo(), param.getConfigId(), SessionType.CHAT.getValue(), param.getUserPrompt(), param.getWindowSize(), param.getSystemPrompt(), param.getToolList(), param.getRagLibIds());
    }

    /**
     * 列出会话信息（分页）。
     * <p>强制绑定调用方 saasId，防止 RPC 调用方查询其他租户的会话数据。
     *
     * @param queryParam 查询参数（自动绑定调用方 saasId）
     * @return 会话分页列表
     */
    @GetMapping("/listSessionInfo")
    @Operation(summary = "列出会话信息", description = "列出会话信息")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<PageList<AiSessionInfo>> listSessionInfo(AiSessionInfoQueryParam queryParam) {
        queryParam.saasId(AuthServiceHelper.getSaasId());
        return AiChatService.listSessionInfo(queryParam);
    }

    /**
     * 列出会话消息（分页）。
     * <p>强制绑定调用方 saasId，防止 RPC 调用方查询其他租户的会话消息。
     *
     * @param queryParam 查询参数（自动绑定调用方 saasId）
     * @return 会话消息分页列表
     */
    @GetMapping("/listSessionMsg")
    @Operation(summary = "列出会话消息", description = "列出会话消息")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<PageList<AiSessionMsg>> listSessionMsg(AiSessionMsgQueryParam queryParam) {
        queryParam.saasId(AuthServiceHelper.getSaasId());
        return AiChatService.listSessionMsg(queryParam);
    }

}