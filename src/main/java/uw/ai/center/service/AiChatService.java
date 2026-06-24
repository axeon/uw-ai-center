package uw.ai.center.service;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.output.TokenUsage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import uw.ai.center.constant.SessionType;
import uw.ai.center.dto.AiSessionInfoQueryParam;
import uw.ai.center.dto.AiSessionMsgQueryParam;
import uw.ai.center.entity.AiSessionInfo;
import uw.ai.center.entity.AiSessionMsg;
import uw.ai.center.advisor.AiMysqlChatMemory;
import uw.ai.center.tool.AiToolHelper;
import uw.ai.center.vendor.AiVendorHelper;
import uw.ai.center.vendor.client.ChatClient;
import uw.ai.center.vo.AiChatSentEvent;
import uw.ai.center.vo.AiModelConfigData;
import uw.ai.vo.AiToolCallInfo;
import uw.common.app.constant.CommonState;
import uw.common.app.vo.JsonConfigBox;
import uw.common.response.ResponseData;
import uw.common.util.JsonUtils;
import uw.common.util.SystemClock;
import uw.dao.DaoManager;
import uw.common.data.PageList;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * AiChatService。
 */
public class AiChatService {

    private static final Logger logger = LoggerFactory.getLogger(AiChatService.class);
    private static final DaoManager dao = DaoManager.getInstance();

    /**
     * 工具调用最大迭代次数，防止AI模型反复请求工具调用导致无限循环。
     */
    private static final int MAX_TOOL_ITERATIONS = 10;

    /**
     * 工具调用循环总超时(ms),避免下游工具微服务变慢时主调用线程被耗尽。
     * 单次 chatModel.chat() ~120s,工具 RPC ~15s,默认 10 轮理论上界远超 90s,实际配 90s 兜底。
     */
    private static final long TOOL_LOOP_DEADLINE_MILLIS = 90_000L;

    /**
     * 工具调用循环结果。
     * 持有最终 ChatResponse、累计的 token 用量，以及是否因达到最大迭代次数而中止。
     */
    private static final class ToolLoopResult {
        final ChatResponse finalResponse;
        /** 累计 input token（含历史/工具结果重放的每一轮） */
        final int totalInputTokens;
        /** 累计 output token */
        final int totalOutputTokens;
        final boolean maxIterationsReached;

        ToolLoopResult(ChatResponse finalResponse, int totalInputTokens, int totalOutputTokens, boolean maxIterationsReached) {
            this.finalResponse = finalResponse;
            this.totalInputTokens = totalInputTokens;
            this.totalOutputTokens = totalOutputTokens;
            this.maxIterationsReached = maxIterationsReached;
        }
    }

    /**
     * 工具调用循环中 chatModel.chat() 抛异常时使用。
     * 携带已累计的 token 计量,供上层在异常路径下也能落库 token,避免整条请求 token 用量丢失。
     */
    private static final class ToolLoopFailedException extends RuntimeException {
        final int totalInputTokens;
        final int totalOutputTokens;

        ToolLoopFailedException(int totalInputTokens, int totalOutputTokens, Throwable cause) {
            super("工具调用循环中 chatModel.chat() 失败", cause);
            this.totalInputTokens = totalInputTokens;
            this.totalOutputTokens = totalOutputTokens;
        }
    }

    /**
     * 执行工具调用循环（generate/chatGenerate/chat 三处共用，避免逻辑重复）。
     * <p>
     * 每轮累加 token usage（修复原先只取最后一轮导致计量丢失的问题），达到 {@link #MAX_TOOL_ITERATIONS}
     * 仍有工具请求时中止并返回标志，由调用方决定如何兜底。
     *
     * @param chatModel   同步聊天模型
     * @param messages    可变消息列表（循环内会追加 AiMessage 与 ToolExecutionResultMessage）
     * @param toolSpecs   工具规格（每轮都会带上）
     * @param toolContext 工具上下文（合并到每个工具调用入参）
     * @param logKey      日志定位用 key（configId 或 sessionId）
     * @return {@link ToolLoopResult}
     */
    private static ToolLoopResult executeToolLoop(ChatModel chatModel, List<ChatMessage> messages,
                                                  List<ToolSpecification> toolSpecs,
                                                  Map<String, Object> toolContext, Object logKey) {
        long startTime = SystemClock.now();
        ChatResponse chatResponse = null;
        int totalInputTokens = 0;
        int totalOutputTokens = 0;
        int iteration = 0;
        boolean maxReached = false;
        try {
            ChatRequest chatRequest = ChatRequest.builder()
                    .messages(messages)
                    .toolSpecifications(toolSpecs)
                    .build();
            chatResponse = chatModel.chat(chatRequest);
            AiMessage aiMessage = chatResponse.aiMessage();
            totalInputTokens = sumInputTokens(chatResponse.tokenUsage());
            totalOutputTokens = sumOutputTokens(chatResponse.tokenUsage());
            while (aiMessage.hasToolExecutionRequests() && iteration < MAX_TOOL_ITERATIONS) {
                // 每轮迭代前检查总 deadline,避免下游工具微服务变慢时主调用线程被耗尽
                if (SystemClock.now() - startTime > TOOL_LOOP_DEADLINE_MILLIS) {
                    logger.warn("工具调用循环超过总 deadline {}ms, key={}, iteration={}",
                            TOOL_LOOP_DEADLINE_MILLIS, logKey, iteration);
                    break;
                }
                iteration++;
                for (ToolExecutionRequest req : aiMessage.toolExecutionRequests()) {
                    String toolInput = mergeToolContext(req.arguments(), toolContext);
                    String result;
                    try {
                        result = AiToolHelper.executeTool(req.name(), toolInput);
                    } catch (Exception e) {
                        // 单次工具执行失败不中断整条链路：把异常文案作为工具结果回传，让模型据此决定下一步
                        logger.error("工具执行异常, toolName={}, key={}", req.name(), logKey, e);
                        result = "[ERROR] 工具执行异常: " + e.getClass().getSimpleName() + " - " + e.getMessage();
                    }
                    messages.add(aiMessage);
                    messages.add(new ToolExecutionResultMessage(req.id(), req.name(), result));
                }
                chatRequest = ChatRequest.builder()
                        .messages(messages)
                        .toolSpecifications(toolSpecs)
                        .build();
                chatResponse = chatModel.chat(chatRequest);
                aiMessage = chatResponse.aiMessage();
                totalInputTokens += sumInputTokens(chatResponse.tokenUsage());
                totalOutputTokens += sumOutputTokens(chatResponse.tokenUsage());
            }
            if (iteration >= MAX_TOOL_ITERATIONS && chatResponse != null
                    && chatResponse.aiMessage().hasToolExecutionRequests()) {
                maxReached = true;
                logger.warn("工具调用达到最大迭代次数 {}, key={}", MAX_TOOL_ITERATIONS, logKey);
            }
            return new ToolLoopResult(chatResponse, totalInputTokens, totalOutputTokens, maxReached);
        } catch (Exception e) {
            // chatModel.chat() 失败,把已累计 token 通过自定义异常透传给调用方,避免异常路径下 token 用量丢失
            throw new ToolLoopFailedException(totalInputTokens, totalOutputTokens, e);
        }
    }

    /**
     * 合并工具上下文到工具入参 JSON（返回新 JSON 字符串；context 为空或解析失败时原样返回）。
     */
    private static String mergeToolContext(String toolArguments, Map<String, Object> toolContext) {
        if (toolContext == null || toolContext.isEmpty()) {
            return toolArguments;
        }
        Map<String, Object> inputMap = JsonUtils.parse(toolArguments,
                new TypeReference<Map<String, Object>>() {});
        if (inputMap == null) {
            // 解析失败时原样返回，避免丢弃 AI 给出的原始入参
            return toolArguments;
        }
        inputMap.putAll(toolContext);
        return JsonUtils.toString(inputMap);
    }

    /**
     * 无工具分支的流式聊天 + 落库。chat/chatGenerate 两入口的无工具流式逻辑一致,抽出复用。
     * <p>使用 {@link AtomicBoolean#compareAndSet(boolean, boolean)} 保证 onComplete/onError/onDispose 三路回调
     * 中 sessionMsg 只落库一次。客户端取消订阅或上游无回调时由 onDispose 兜底。
     *
     * @param chatClient 流式 ChatClient(内部调 getStreamingChatModel())
     * @param messages   已组装好的消息列表(含 system/user/历史等)
     * @param sessionMsg 待落库的会话消息
     * @param logKey     日志定位用 key(configId 或 sessionId)
     */
    private static Flux<String> streamChatAndPersist(ChatClient chatClient, List<ChatMessage> messages,
                                                     AiSessionMsg sessionMsg, Object logKey) {
        return Flux.create(sink -> {
            sessionMsg.setResponseStartDate(SystemClock.nowDate());
            StringBuilder responseBuilder = new StringBuilder();
            AtomicBoolean saved = new AtomicBoolean(false);
            sink.onDispose(() -> {
                if (saved.compareAndSet(false, true)) {
                    sessionMsg.setResponseEndDate(SystemClock.nowDate());
                    sessionMsg.setResponseInfo(responseBuilder.length() > 0
                            ? responseBuilder.toString()
                            : "[ERROR] AI响应被中断");
                    saveSessionMsg(sessionMsg);
                }
            });
            chatClient.getStreamingChatModel().chat(messages, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String token) {
                    responseBuilder.append(token);
                    sink.next(new AiChatSentEvent<>(token).toString());
                }

                @Override
                public void onCompleteResponse(ChatResponse completeResponse) {
                    if (!saved.compareAndSet(false, true)) {
                        return;
                    }
                    TokenUsage tokenUsage = completeResponse.tokenUsage();
                    sessionMsg.setRequestTokens(tokenUsage != null && tokenUsage.inputTokenCount() != null
                            ? tokenUsage.inputTokenCount() : 0);
                    sessionMsg.setResponseTokens(tokenUsage != null && tokenUsage.outputTokenCount() != null
                            ? tokenUsage.outputTokenCount() : 0);
                    sessionMsg.setResponseEndDate(SystemClock.nowDate());
                    sessionMsg.setResponseInfo(responseBuilder.toString());
                    saveSessionMsg(sessionMsg);
                    sink.complete();
                }

                @Override
                public void onError(Throwable error) {
                    if (!saved.compareAndSet(false, true)) {
                        return;
                    }
                    // 详细原因只进日志(可能含上游厂商 URL/连接细节/Key),DB 与对外都用固定文案脱敏
                    logger.error("流式聊天异常, logKey={}, errClass={}, msg={}", logKey,
                            error.getClass().getSimpleName(), error.getMessage());
                    sessionMsg.setResponseEndDate(SystemClock.nowDate());
                    sessionMsg.setResponseInfo("[ERROR] AI流式响应失败");
                    saveSessionMsg(sessionMsg);
                    sink.error(new RuntimeException("AI流式响应失败，请稍后重试"));
                }
            });
        });
    }

    /**
     * 安全提取 input token 数量（null 安全）。
     */
    private static int sumInputTokens(TokenUsage usage) {
        return usage != null && usage.inputTokenCount() != null ? usage.inputTokenCount() : 0;
    }

    /**
     * 安全提取 output token 数量（null 安全）。
     */
    private static int sumOutputTokens(TokenUsage usage) {
        return usage != null && usage.outputTokenCount() != null ? usage.outputTokenCount() : 0;
    }

    /**
     * 从模型配置参数盒读取 systemPrompt（vendor 未注册或参数盒为空时返回原值，不抛 NPE）。
     */
    private static String resolveSystemPrompt(AiModelConfigData configData, String systemPrompt) {
        if (StringUtils.isNotBlank(systemPrompt) || configData == null) {
            return systemPrompt;
        }
        JsonConfigBox box = configData.getConfigParamBox();
        return box != null ? box.getParam("systemPrompt", "") : systemPrompt;
    }

    /**
     * ChatClient 简单调用。
     */
    public static ResponseData<String> generate(long saasId, long userId, int userType, String userInfo, long configId, String systemPrompt, String userPrompt, List<AiToolCallInfo> toolList, Map<String, Object> toolContext, MultipartFile[] fileList, long[] ragLibIds) {
        ChatClient chatClient;
        try {
            chatClient = AiVendorHelper.getChatClient(configId);
        } catch (IllegalStateException e) {
            logger.warn("generate 获取ChatClient失败, configId={}, errClass={}, msg={}", configId, e.getClass().getSimpleName(), e.getMessage());
            return ResponseData.errorMsg("AI模型配置不存在或未启用，请联系管理员");
        }
        AiModelConfigData configData = chatClient.getConfigData();
        systemPrompt = resolveSystemPrompt(configData, systemPrompt);
        // 初始化会话信息（sessionName 传 userPrompt：会话名展示用户首个问题，符合 ChatGPT 等聊天产品的习惯）
        AiSessionInfo sessionInfo = loadSession(saasId, userId, SessionType.COMMON.getValue(), null).getData();
        if (sessionInfo == null) {
            ResponseData<AiSessionInfo> responseData = initSession(saasId, userId, userType, userInfo, configId, SessionType.COMMON.getValue(), userPrompt, null, systemPrompt, toolList, ragLibIds);
            if (responseData.isNotSuccess()) {
                return responseData.raw();
            }
            sessionInfo = responseData.getData();
            if (sessionInfo == null) {
                return ResponseData.errorMsg("初始化会话失败，请稍后重试");
            }
        }
        // 构建附件信息
        String fileInfo = null;
        String fileContent = null;
        if (fileList != null) {
            ResponseData<String[]> readFileData = readFileData(fileList);
            if (readFileData.isNotSuccess()) {
                return readFileData.raw();
            } else {
                String[] fileData = readFileData.getData();
                fileInfo = fileData[0];
                fileContent = fileData[1];
            }
        }
        //检查rag信息。
        String ragContent = null;
        String ragSource = "none";
        if (ragLibIds == null || ragLibIds.length == 0) {
            if (StringUtils.isNotBlank(sessionInfo.getRagConfig())) {
                ragLibIds = JsonUtils.parse(sessionInfo.getRagConfig(), long[].class);
                ragSource = "session";
            }
        } else {
            ragSource = "request";
        }
        if (ragLibIds != null && ragLibIds.length > 0) {
            ragContent = queryRagInfo(ragLibIds, userPrompt).getData();
            logger.info("RAG已启用, sessionId={}, source={}, ragLibIds={}, ragContentLen={}, userPrompt=[{}]",
                    sessionInfo.getId(), ragSource, Arrays.toString(ragLibIds), ragContent == null ? 0 : ragContent.length(), truncateForLog(userPrompt, 100));
        } else {
            logger.debug("RAG未启用, sessionId={}, userPrompt=[{}]", sessionInfo.getId(), truncateForLog(userPrompt, 100));
        }
        String contextData = null;
        if (StringUtils.isNotBlank(ragContent) || StringUtils.isNotBlank(fileContent)) {
            contextData = buildContextInfo(ragContent, fileContent);
        }
        // 初始化会话消息
        AiSessionMsg sessionMsg = initSessionMsg(saasId, userId, userType, userInfo, configId, sessionInfo.getId(), systemPrompt, userPrompt, toolList, fileInfo, ragLibIds, contextData);
        sessionMsg.setResponseStartDate(SystemClock.nowDate());

        // === LangChain4j 调用 ===
        List<ChatMessage> messages = new ArrayList<>();
        if (StringUtils.isNotBlank(systemPrompt)) {
            messages.add(new SystemMessage(systemPrompt));
        }
        String finalPrompt = StringUtils.isNotBlank(contextData) ? userPrompt + contextData : userPrompt;
        messages.add(new UserMessage(finalPrompt));

        List<ToolSpecification> toolSpecs = (toolList != null && !toolList.isEmpty())
                ? AiToolHelper.getToolSpecifications(toolList) : null;

        ChatResponse chatResponse;
        try {
            if (toolSpecs != null && !toolSpecs.isEmpty()) {
                ToolLoopResult loopResult = executeToolLoop(chatClient.getChatModel(), messages,
                        toolSpecs, toolContext, configId);
                chatResponse = loopResult.finalResponse;
                // 达到最大迭代次数仍有工具请求：模型未产出有效文本，给固定提示而非落库 null
                if (loopResult.maxIterationsReached && StringUtils.isBlank(chatResponse.aiMessage().text())) {
                    sessionMsg.setRequestTokens(loopResult.totalInputTokens);
                    sessionMsg.setResponseTokens(loopResult.totalOutputTokens);
                    sessionMsg.setResponseEndDate(SystemClock.nowDate());
                    sessionMsg.setResponseInfo("[ERROR] 工具调用次数超限，请重试或简化请求");
                    saveSessionMsg(sessionMsg);
                    return ResponseData.errorMsg("工具调用次数超限，请重试或简化请求");
                }
                sessionMsg.setRequestTokens(loopResult.totalInputTokens);
                sessionMsg.setResponseTokens(loopResult.totalOutputTokens);
            } else {
                chatResponse = chatClient.getChatModel().chat(messages);
                TokenUsage tokenUsage = chatResponse.tokenUsage();
                sessionMsg.setRequestTokens(sumInputTokens(tokenUsage));
                sessionMsg.setResponseTokens(sumOutputTokens(tokenUsage));
            }
        } catch (Exception e) {
            logger.error("AI模型调用失败, configId={}", configId, e);
            // 异常路径下也落库 sessionMsg，避免会话历史出现只问不答的"幽灵"消息
            sessionMsg.setResponseEndDate(SystemClock.nowDate());
            sessionMsg.setResponseInfo("[ERROR] AI模型调用失败");
            // 工具调用循环异常时,从自定义异常透传已累计 token,避免整条请求 token 用量丢失
            if (e instanceof ToolLoopFailedException tf) {
                sessionMsg.setRequestTokens(tf.totalInputTokens);
                sessionMsg.setResponseTokens(tf.totalOutputTokens);
            }
            saveSessionMsg(sessionMsg);
            return ResponseData.errorMsg("AI模型调用失败，请稍后重试");
        }

        String responseData = chatResponse.aiMessage().text();
        sessionMsg.setResponseEndDate(SystemClock.nowDate());
        sessionMsg.setResponseInfo(responseData);
        saveSessionMsg(sessionMsg);
        return ResponseData.success(responseData);
    }

    /**
     * ChatClient 流式调用
     */
    public static Flux<String> chatGenerate(long saasId, long userId, int userType, String userInfo, long configId, String systemPrompt, String userPrompt, List<AiToolCallInfo> toolList, Map<String, Object> toolContext, MultipartFile[] fileList, long[] ragLibIds) {
        ChatClient chatClient;
        try {
            chatClient = AiVendorHelper.getChatClient(configId);
        } catch (IllegalStateException e) {
            logger.warn("chatGenerate 获取ChatClient失败, configId={}, errClass={}, msg={}", configId, e.getClass().getSimpleName(), e.getMessage());
            return Flux.just(ResponseData.errorMsg("AI模型配置不存在或未启用，请联系管理员").toString());
        }
        AiModelConfigData configData = chatClient.getConfigData();
        systemPrompt = resolveSystemPrompt(configData, systemPrompt);
        // 初始化会话信息（sessionName 传 userPrompt：会话名展示用户首个问题，符合 ChatGPT 等聊天产品的习惯）
        AiSessionInfo sessionInfo = loadSession(saasId, userId, SessionType.COMMON.getValue(), null).getData();
        if (sessionInfo == null) {
            ResponseData<AiSessionInfo> responseData = initSession(saasId, userId, userType, userInfo, configId, SessionType.COMMON.getValue(), userPrompt, null, systemPrompt, toolList, ragLibIds);
            if (responseData.isNotSuccess()) {
                return Flux.just(responseData.toString());
            }
            sessionInfo = responseData.getData();
            if (sessionInfo == null) {
                return Flux.just(ResponseData.errorMsg("初始化会话失败，请稍后重试").toString());
            }
        }
        // 构建附件信息
        String fileInfo = null;
        String fileContent = null;
        if (fileList != null) {
            ResponseData<String[]> readFileData = readFileData(fileList);
            if (readFileData.isNotSuccess()) {
                return Flux.just(readFileData.toString());
            } else {
                String[] fileData = readFileData.getData();
                fileInfo = fileData[0];
                fileContent = fileData[1];
            }
        }
        //检查rag信息。
        String ragContent = null;
        String ragSource = "none";
        if (ragLibIds == null || ragLibIds.length == 0) {
            if (StringUtils.isNotBlank(sessionInfo.getRagConfig())) {
                ragLibIds = JsonUtils.parse(sessionInfo.getRagConfig(), long[].class);
                ragSource = "session";
            }
        } else {
            ragSource = "request";
        }
        if (ragLibIds != null && ragLibIds.length > 0) {
            ragContent = queryRagInfo(ragLibIds, userPrompt).getData();
            logger.info("RAG已启用, sessionId={}, source={}, ragLibIds={}, ragContentLen={}, userPrompt=[{}]",
                    sessionInfo.getId(), ragSource, Arrays.toString(ragLibIds), ragContent == null ? 0 : ragContent.length(), truncateForLog(userPrompt, 100));
        } else {
            logger.debug("RAG未启用, sessionId={}, userPrompt=[{}]", sessionInfo.getId(), truncateForLog(userPrompt, 100));
        }
        String contextData = null;
        if (StringUtils.isNotBlank(ragContent) || StringUtils.isNotBlank(fileContent)) {
            contextData = buildContextInfo(ragContent, fileContent);
        }
        // 初始化会话消息
        AiSessionMsg sessionMsg = initSessionMsg(saasId, userId, userType, userInfo, configId, sessionInfo.getId(), systemPrompt, userPrompt, toolList, fileInfo, ragLibIds, contextData);

        // === LangChain4j 流式调用 ===
        List<ChatMessage> messages = new ArrayList<>();
        if (StringUtils.isNotBlank(systemPrompt)) {
            messages.add(new SystemMessage(systemPrompt));
        }
        String finalPrompt = StringUtils.isNotBlank(contextData) ? userPrompt + contextData : userPrompt;
        messages.add(new UserMessage(finalPrompt));

        List<ToolSpecification> toolSpecs = (toolList != null && !toolList.isEmpty())
                ? AiToolHelper.getToolSpecifications(toolList) : null;

        boolean hasTools = toolSpecs != null && !toolSpecs.isEmpty();

        // 如果有工具，先同步执行工具调用循环，再流式返回最终结果
        // 注：LangChain4j 的工具调用在同步 ChatModel 上完成，无法在工具决策阶段流式，
        // 仅最终回答阶段可流式；当前实现为工具循环完成后一次性返回 finalText，并非真正的端到端流式。
        // TODO: 后续接入 LangChain4j 流式 + 工具的整合 API 时，改为工具决策完成后流式产出回答文本
        if (hasTools) {
            try {
                ToolLoopResult loopResult = executeToolLoop(chatClient.getChatModel(), messages,
                        toolSpecs, toolContext, configId);
                ChatResponse response = loopResult.finalResponse;
                String finalText = response.aiMessage().text();
                // 达到最大迭代次数仍未产出文本：返回固定提示，避免落库 null
                if (loopResult.maxIterationsReached && StringUtils.isBlank(finalText)) {
                    sessionMsg.setResponseStartDate(SystemClock.nowDate());
                    sessionMsg.setRequestTokens(loopResult.totalInputTokens);
                    sessionMsg.setResponseTokens(loopResult.totalOutputTokens);
                    sessionMsg.setResponseEndDate(SystemClock.nowDate());
                    sessionMsg.setResponseInfo("[ERROR] 工具调用次数超限，请重试或简化请求");
                    saveSessionMsg(sessionMsg);
                    return Flux.just(ResponseData.errorMsg("工具调用次数超限，请重试或简化请求").toString());
                }
                sessionMsg.setResponseStartDate(SystemClock.nowDate());
                sessionMsg.setRequestTokens(loopResult.totalInputTokens);
                sessionMsg.setResponseTokens(loopResult.totalOutputTokens);
                sessionMsg.setResponseEndDate(SystemClock.nowDate());
                sessionMsg.setResponseInfo(finalText);
                saveSessionMsg(sessionMsg);
                return Flux.just(new AiChatSentEvent<>(finalText).toString());
            } catch (Exception e) {
                logger.error("流式工具调用失败, configId={}", configId, e);
                // 异常路径下也落库 sessionMsg,与无工具分支 onError 行为一致,避免整条请求历史丢失
                sessionMsg.setResponseEndDate(SystemClock.nowDate());
                sessionMsg.setResponseInfo("[ERROR] AI工具调用失败");
                if (e instanceof ToolLoopFailedException tf) {
                    sessionMsg.setRequestTokens(tf.totalInputTokens);
                    sessionMsg.setResponseTokens(tf.totalOutputTokens);
                }
                saveSessionMsg(sessionMsg);
                return Flux.just(ResponseData.errorMsg("AI工具调用失败，请稍后重试").toString());
            }
        }

        // 无工具:直接流式调用
        return streamChatAndPersist(chatClient, messages, sessionMsg, configId);
    }


    /**
     * 根据saasId、userId、sessionId获取session.
     *
     * @param saasId
     * @param userId
     * @param sessionId
     * @return
     */
    public static ResponseData<AiSessionInfo> loadSession(Long saasId, Long userId, Integer sessionType, Long sessionId) {
        // sessionId 为空时按 saasId+userId+sessionType 查询，多记录时按 id 倒序取最新一条
        AiSessionInfoQueryParam queryParam = new AiSessionInfoQueryParam(saasId).userId(userId).sessionType(sessionType).id(sessionId);
        if (sessionId == null) {
            queryParam.ADD_SORT("id", uw.common.dto.QueryParam.SORT_DESC);
        }
        return dao.queryForObject(AiSessionInfo.class, queryParam);
    }

    /**
     * 初始化session.
     *
     * @param saasId
     * @param userId
     * @param userType
     * @param userInfo
     * @param sessionName
     * @param systemPrompt
     * @param windowSize
     * @return
     */
    public static ResponseData<AiSessionInfo> initSession(long saasId, long userId, int userType, String userInfo, long configId, int sessionType, String sessionName, Integer windowSize, String systemPrompt, List<AiToolCallInfo> toolList, long[] ragLibIds) {
        AiModelConfigData configData;
        try {
            configData = AiVendorHelper.getModelConfigData(configId);
        } catch (Exception e) {
            logger.warn("获取模型配置失败, configId={}, 将使用默认值: {}", configId, e.getMessage());
            return ResponseData.errorMsg("模型不可用，请稍后再试");
        }
        if (configData != null) {
            systemPrompt = resolveSystemPrompt(configData, systemPrompt);
        }
        // 调用方未显式传会话名时使用"新会话"兜底（如 AiChatUserController 显式初始化空会话的场景）。
        if (StringUtils.isBlank(sessionName)) {
            sessionName = "新会话";
        }
        long sessionId = dao.getSequenceId(AiSessionInfo.class);
        AiSessionInfo sessionInfo = new AiSessionInfo();
        sessionInfo.setId(sessionId);
        sessionInfo.setSaasId(saasId);
        sessionInfo.setUserId(userId);
        sessionInfo.setUserType(userType);
        sessionInfo.setUserInfo(userInfo);
        sessionInfo.setConfigId(configId);
        sessionInfo.setSessionType(sessionType);
        sessionInfo.setSessionName(truncateWithEllipsis(sessionName, 200));
        sessionInfo.setSystemPrompt(systemPrompt);
        sessionInfo.setMsgNum(0);
        if (windowSize != null) {
            sessionInfo.setWindowSize(windowSize);
        }
        sessionInfo.setToolConfig(JsonUtils.toString(toolList));
        sessionInfo.setRagConfig(JsonUtils.toString(ragLibIds));
        sessionInfo.setRequestTokens(0);
        sessionInfo.setResponseTokens(0);
        sessionInfo.setCreateDate(SystemClock.nowDate());
        sessionInfo.setLastUpdate(null);
        sessionInfo.setState(CommonState.ENABLED.getValue());
        return dao.save(sessionInfo);
    }

    /**
     * 单个文件大小上限：50MB（与 RAG 文档入库一致）。
     */
    private static final long MAX_UPLOAD_FILE_SIZE = 50L * 1024 * 1024;

    /**
     * 允许上传的文件扩展名白名单（与 Apache Tika 解析能力对齐：纯文本/文档/表格/演示/网页）。
     */
    private static final Set<String> ALLOWED_FILE_EXTENSIONS = Set.of(
            "txt", "md", "csv", "log",
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "html", "htm", "xml", "json", "rtf");

    /**
     * 校验上传文件：非空、大小、扩展名白名单。校验失败返回错误响应。
     */
    private static ResponseData validateUploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseData.errorMsg("文件为空!");
        }
        if (file.getSize() > MAX_UPLOAD_FILE_SIZE) {
            return ResponseData.errorMsg("文件[" + file.getOriginalFilename() + "]超过大小限制(50MB)!");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            return ResponseData.errorMsg("文件名为空!");
        }
        // 路径穿越/控制字符检查
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")
                || filename.chars().anyMatch(c -> c < 0x20)) {
            return ResponseData.errorMsg("文件名[" + filename + "]包含非法字符!");
        }
        int dotIdx = filename.lastIndexOf('.');
        if (dotIdx < 0 || dotIdx == filename.length() - 1) {
            return ResponseData.errorMsg("文件[" + filename + "]缺少扩展名!");
        }
        String ext = filename.substring(dotIdx + 1).toLowerCase();
        if (!ALLOWED_FILE_EXTENSIONS.contains(ext)) {
            return ResponseData.errorMsg("文件[" + filename + "]类型不支持，允许: " + ALLOWED_FILE_EXTENSIONS);
        }
        return null;
    }

    /**
     * 读取文件内容。
     *
     * @param files
     * @return String[0] 文件信息 String[1] 文件内容
     */
    public static ResponseData<String[]> readFileData(MultipartFile[] files) {
        if (files == null) {
            return ResponseData.errorMsg("文件为空!");
        }
        LinkedHashMap<String, Long> infoMap = new LinkedHashMap<>();
        StringBuilder content = new StringBuilder(8192);
        for (MultipartFile file : files) {
            ResponseData validateResult = validateUploadFile(file);
            if (validateResult != null) {
                return validateResult;
            }
            infoMap.put(file.getOriginalFilename(), file.getSize());
            content.append("文件名：").append(file.getOriginalFilename()).append("的内容：\n\n");
            try (InputStream inputStream = file.getInputStream()) {
                ApacheTikaDocumentParser parser = new ApacheTikaDocumentParser();
                dev.langchain4j.data.document.Document lc4jDoc = parser.parse(inputStream);
                String text = lc4jDoc.text();
                if (text != null && !text.isEmpty()) {
                    content.append(text).append("\n");
                } else {
                    return ResponseData.warnMsg("文件[" + file.getOriginalFilename() + "]内容为空，无法提取文本!");
                }
            } catch (IOException e) {
                logger.error("处理文件[{}]时发生错误!{}", file.getOriginalFilename(), e.getMessage(), e);
                return ResponseData.errorMsg("处理文件[" + file.getOriginalFilename() + "]时发生错误!");
            }
        }
        String fileInfo = JsonUtils.toString(infoMap);
        return ResponseData.success(new String[]{fileInfo, content.toString()});
    }

    /**
     * 查询Rag信息。
     *
     * @param ragLibIds
     * @param userPrompt
     * @return
     */
    public static ResponseData<String> queryRagInfo(long[] ragLibIds, String userPrompt) {
        StringBuilder sb = new StringBuilder(1280);
        if (ragLibIds == null || ragLibIds.length == 0) {
            return ResponseData.success(sb.toString());
        }
        long startMs = SystemClock.now();
        logger.info("RAG检索开始, ragLibIds={}, userPrompt=[{}]", Arrays.toString(ragLibIds), truncateForLog(userPrompt, 100));
        for (int i = 0; i < ragLibIds.length; i++) {
            sb.append(AiRagService.query(ragLibIds[i], userPrompt));
        }
        logger.info("RAG检索完成, libCount={}, resultLen={}, totalMs={}", ragLibIds.length, sb.length(), SystemClock.now() - startMs);
        return ResponseData.success(sb.toString());
    }

    /**
     * 截断字符串用于日志输出（null 安全、超长补省略号），避免 query 刷屏。
     */
    private static String truncateForLog(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }

    /**
     * 构建上下文信息。
     *
     * @param contextInfos
     * @return
     */
    public static String buildContextInfo(String... contextInfos) {
        StringBuilder content = new StringBuilder(8192);
        content.append("\n\n以下内容是附件信息，在你回答问题时可以参考下面的内容，如果问题答案不在其中，请回答不知道。\n");
        content.append("---------------------\n");
        for (String contextInfo : contextInfos) {
            if (contextInfo != null) {
                content.append(contextInfo).append("\n");
            }
        }
        content.append("---------------------\n");
        return content.toString();
    }

    /**
     * 初始化sessionMsg.
     *
     * @param sessionId
     * @param userPrompt
     * @return
     */
    public static AiSessionMsg initSessionMsg(long saasId, long userId, int userType, String userInfo, long configId, long sessionId, String systemPrompt, String userPrompt, List<AiToolCallInfo> toolList, String fileInfo, long[] ragIds, String contextInfo) {
        long msgId = dao.getSequenceId(AiSessionMsg.class);
        AiSessionMsg sessionMsg = new AiSessionMsg();
        sessionMsg.setId(msgId);
        sessionMsg.setSaasId(saasId);
        sessionMsg.setUserId(userId);
        sessionMsg.setUserType(userType);
        sessionMsg.setUserInfo(userInfo);
        sessionMsg.setConfigId(configId);
        sessionMsg.setSessionId(sessionId);
        sessionMsg.setSystemPrompt(systemPrompt);
        sessionMsg.setUserPrompt(userPrompt);
        if (toolList != null) {
            sessionMsg.setToolConfig(JsonUtils.toString(toolList));
        }
        if (ragIds != null) {
            sessionMsg.setRagConfig(JsonUtils.toString(ragIds));
        }
        sessionMsg.setFileConfig(fileInfo);
        sessionMsg.setContextData(contextInfo);
        sessionMsg.setState(CommonState.ENABLED.getValue());
        sessionMsg.setRequestDate(SystemClock.nowDate());
        return sessionMsg;
    }

    /**
     * 保存sessionMsg.
     *
     * @param sessionMsg
     * @return
     */
    public static ResponseData<AiSessionMsg> saveSessionMsg(AiSessionMsg sessionMsg) {
        // 更新sessionMsg
        return dao.save(sessionMsg).onSuccess(savedEntity -> {
            // 更新session会话
            String sql = "update ai_session_info set last_update=?, msg_num=msg_num+1,request_tokens=request_tokens+?,response_tokens=response_tokens+? where saas_id=? and id=?";
            ResponseData<Integer> updateResp = dao.execute(sql, new Object[]{SystemClock.nowDate(), sessionMsg.getRequestTokens(), sessionMsg.getResponseTokens(), sessionMsg.getSaasId(), sessionMsg.getSessionId()});
            // update 影响行数为 0 通常意味着 session 已被并发软删,sessionMsg 已落库但 session_info 未更新,记录 warn 便于排查
            if (updateResp == null || updateResp.getData() == null || updateResp.getData() == 0) {
                logger.warn("更新 session_info 失败或影响行数为 0, saasId={}, sessionId={}, code={}",
                        sessionMsg.getSaasId(), sessionMsg.getSessionId(),
                        updateResp == null ? "null" : updateResp.getCode());
            }
        });
    }

    /**
     * 截断字符串，如果长度大于 truncLen，则截断并添加省略号。
     *
     * @param input
     * @param truncLen
     * @return
     */
    public static String truncateWithEllipsis(String input, int truncLen) {
        if (input == null) {
            return null;
        }
        if (input.length() > truncLen) {
            return StringUtils.substring(input, 0, truncLen - 3) + "...";
        } else {
            // No truncation needed
            return input;
        }
    }

    /**
     * ChatClient 流式调用
     */
    public static Flux<String> chat(long saasId, long userId, int userType, String userInfo, long configId, long sessionId, String systemPrompt, String userPrompt, List<AiToolCallInfo> toolList, Map<String, Object> toolContext, MultipartFile[] fileList, long[] ragLibIds) {
        // 初始化会话信息
        AiSessionInfo sessionInfo;
        if (sessionId > 0) {
            sessionInfo = loadSession(saasId, userId, SessionType.CHAT.getValue(), sessionId).getData();
        } else {
            sessionInfo = null;
        }
        if (sessionInfo == null) {
            return Flux.just(ResponseData.errorMsg("Session会话不存在！").toString());
        }
        // 检查configId，如果不存在则使用会话的
        if(configId <= 0){
            configId = sessionInfo.getConfigId();
        }
        // 如何没有系统提示语，则使用会话的
        if (StringUtils.isBlank(systemPrompt)) {
            systemPrompt = sessionInfo.getSystemPrompt();
        }
        // 构建附件信息
        String fileInfo = null;
        String fileContent = null;
        if (fileList != null) {
            ResponseData<String[]> readFileData = readFileData(fileList);
            if (readFileData.isNotSuccess()) {
                AiSessionMsg errSessionMsg = initSessionMsg(saasId, userId, userType, userInfo, configId, sessionInfo.getId(), systemPrompt, userPrompt, toolList, null, ragLibIds, null);
                errSessionMsg.setResponseEndDate(SystemClock.nowDate());
                errSessionMsg.setResponseInfo("[ERROR] 文件读取失败: " + readFileData.getMsg());
                saveSessionMsg(errSessionMsg);
                return Flux.just(readFileData.toString());
            } else {
                String[] fileData = readFileData.getData();
                fileInfo = fileData[0];
                fileContent = fileData[1];
            }
        }
        //检查rag信息。
        String ragContent = null;
        String ragSource = "none";
        if (ragLibIds == null || ragLibIds.length == 0) {
            if (StringUtils.isNotBlank(sessionInfo.getRagConfig())) {
                ragLibIds = JsonUtils.parse(sessionInfo.getRagConfig(), long[].class);
                ragSource = "session";
            }
        } else {
            ragSource = "request";
        }
        if (ragLibIds != null && ragLibIds.length > 0) {
            ragContent = queryRagInfo(ragLibIds, userPrompt).getData();
            logger.info("RAG已启用, sessionId={}, source={}, ragLibIds={}, ragContentLen={}, userPrompt=[{}]",
                    sessionInfo.getId(), ragSource, Arrays.toString(ragLibIds), ragContent == null ? 0 : ragContent.length(), truncateForLog(userPrompt, 100));
        } else {
            logger.debug("RAG未启用, sessionId={}, userPrompt=[{}]", sessionInfo.getId(), truncateForLog(userPrompt, 100));
        }
        String contextData = null;
        if (StringUtils.isNotBlank(ragContent) || StringUtils.isNotBlank(fileContent)) {
            contextData = buildContextInfo(ragContent, fileContent);
        }
        // 初始化会话消息
        AiSessionMsg sessionMsg = initSessionMsg(saasId, userId, userType, userInfo, configId, sessionInfo.getId(), systemPrompt, userPrompt, toolList, fileInfo, ragLibIds, contextData);
        // 获取LangChain4j客户端（使用请求中的configId，而非会话中的，因为会话可能由图片生成创建而configId=0）
        ChatClient chatClient;
        try {
            chatClient = AiVendorHelper.getChatClient(configId);
        } catch (IllegalStateException e) {
            logger.warn("chat 获取ChatClient失败, configId={}, errClass={}, msg={}", configId, e.getClass().getSimpleName(), e.getMessage());
            sessionMsg.setResponseEndDate(SystemClock.nowDate());
            sessionMsg.setResponseInfo("[ERROR] AI模型配置不存在或未启用");
            saveSessionMsg(sessionMsg);
            return Flux.just(ResponseData.errorMsg("AI模型配置不存在或未启用，请联系管理员").toString());
        }

        // === LangChain4j 流式调用（加载历史消息） ===
        List<ChatMessage> messages = AiMysqlChatMemory.load(sessionInfo.getSaasId(), sessionInfo.getId(), sessionInfo.getWindowSize());
        if (StringUtils.isNotBlank(systemPrompt)) {
            messages.add(new SystemMessage(systemPrompt));
        }
        String finalPrompt = StringUtils.isNotBlank(contextData) ? userPrompt + contextData : userPrompt;
        messages.add(new UserMessage(finalPrompt));

        List<ToolSpecification> toolSpecs = (toolList != null && !toolList.isEmpty())
                ? AiToolHelper.getToolSpecifications(toolList) : null;

        boolean hasTools = toolSpecs != null && !toolSpecs.isEmpty();

        // 如果有工具，先同步执行工具调用循环，再流式返回最终结果
        if (hasTools) {
            try {
                ToolLoopResult loopResult = executeToolLoop(chatClient.getChatModel(), messages,
                        toolSpecs, toolContext, sessionInfo.getId());
                ChatResponse response = loopResult.finalResponse;
                String finalText = response.aiMessage().text();
                if (loopResult.maxIterationsReached && StringUtils.isBlank(finalText)) {
                    sessionMsg.setResponseStartDate(SystemClock.nowDate());
                    sessionMsg.setRequestTokens(loopResult.totalInputTokens);
                    sessionMsg.setResponseTokens(loopResult.totalOutputTokens);
                    sessionMsg.setResponseEndDate(SystemClock.nowDate());
                    sessionMsg.setResponseInfo("[ERROR] 工具调用次数超限，请重试或简化请求");
                    saveSessionMsg(sessionMsg);
                    return Flux.just(ResponseData.errorMsg("工具调用次数超限，请重试或简化请求").toString());
                }
                sessionMsg.setResponseStartDate(SystemClock.nowDate());
                sessionMsg.setRequestTokens(loopResult.totalInputTokens);
                sessionMsg.setResponseTokens(loopResult.totalOutputTokens);
                sessionMsg.setResponseEndDate(SystemClock.nowDate());
                sessionMsg.setResponseInfo(finalText);
                saveSessionMsg(sessionMsg);
                return Flux.just(new AiChatSentEvent<>(finalText).toString());
            } catch (Exception e) {
                logger.error("流式工具调用失败, sessionId={}", sessionInfo.getId(), e);
                return Flux.just(ResponseData.errorMsg("AI工具调用失败，请稍后重试").toString());
            }
        }

        // 无工具：直接流式调用
        return streamChatAndPersist(chatClient, messages, sessionMsg, sessionInfo.getId());
    }

    /**
     * 列表SessionInfo，并填充modelType（从FusionCache获取，无额外DB查询）。
     *
     * @return
     */
    public static ResponseData<PageList<AiSessionInfo>> listSessionInfo(AiSessionInfoQueryParam queryParam) {
        ResponseData<PageList<AiSessionInfo>> result = dao.list(AiSessionInfo.class, queryParam);
        if (result.isSuccess() && result.getData() != null) {
            for (AiSessionInfo session : result.getData()) {
                if (session.getConfigId() > 0) {
                    try {
                        AiModelConfigData configData = AiVendorHelper.getModelConfigData(session.getConfigId());
                        if (configData != null) {
                            session.setModelType(configData.getModelType());
                        }
                    } catch (Exception e) {
                        // 配置不在缓存中（可能已禁用），忽略
                        logger.debug("会话configId={}的模型配置未找到", session.getConfigId());
                    }
                }
            }
        }
        return result;
    }

    /**
     * 列表SessionMsg.
     *
     * @return
     */
    public static ResponseData<PageList<AiSessionMsg>> listSessionMsg(AiSessionMsgQueryParam queryParam) {
        return dao.list(AiSessionMsg.class, queryParam);
    }

}
