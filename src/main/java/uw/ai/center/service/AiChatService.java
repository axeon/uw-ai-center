package uw.ai.center.service;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
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
import uw.ai.center.vendor.AiVendorClientWrapper;
import uw.ai.center.vendor.AiVendorHelper;
import uw.ai.center.vo.AiChatSentEvent;
import uw.ai.center.vo.AiModelConfigData;
import uw.ai.vo.AiToolCallInfo;
import uw.common.app.constant.CommonState;
import uw.common.dto.ResponseData;
import uw.common.util.JsonUtils;
import uw.common.util.SystemClock;
import uw.dao.DaoManager;
import uw.dao.DataList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * AiChatService。
 */
public class AiChatService {

    private static final Logger logger = LoggerFactory.getLogger(AiChatService.class);
    private static final DaoManager dao = DaoManager.getInstance();

    /**
     * ChatClient 简单调用。
     */
    public static ResponseData<String> generate(long saasId, long userId, int userType, String userInfo, long configId, String systemPrompt, String userPrompt, List<AiToolCallInfo> toolList, Map<String, Object> toolContext, MultipartFile[] fileList, long[] ragLibIds) {
        AiVendorClientWrapper vendorWrapper = AiVendorHelper.getClientWrapper(configId);
        if (vendorWrapper == null) {
            return ResponseData.errorMsg("ChatClient获取失败!");
        }
        AiModelConfigData configData = vendorWrapper.getConfigData();
        if (StringUtils.isBlank(systemPrompt)) {
            systemPrompt = configData.getModelParamBox().getParam("systemPrompt", "");
        }
        // 初始化会话信息
        AiSessionInfo sessionInfo = loadSession(saasId, userId, SessionType.COMMON.getValue(), null).getData();
        if (sessionInfo == null) {
            ResponseData<AiSessionInfo> responseData = initSession(saasId, userId, userType, userInfo, configId, SessionType.COMMON.getValue(), userPrompt, null, systemPrompt, toolList, ragLibIds);
            if (responseData.isNotSuccess()) {
                return responseData.raw();
            } else {
                sessionInfo = responseData.getData();
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
        if (ragLibIds == null || ragLibIds.length == 0) {
            if (StringUtils.isNotBlank(sessionInfo.getRagConfig())) {
                ragLibIds = JsonUtils.parse(sessionInfo.getRagConfig(), long[].class);
            }
        }
        if (ragLibIds != null && ragLibIds.length > 0) {
            ragContent = queryRagInfo(ragLibIds, userPrompt).getData();
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
                ChatRequest chatRequest = ChatRequest.builder()
                        .messages(messages)
                        .toolSpecifications(toolSpecs)
                        .build();
                chatResponse = vendorWrapper.getChatModel().chat(chatRequest);
                AiMessage aiMessage = chatResponse.aiMessage();
                // 工具调用循环
                while (aiMessage.hasToolExecutionRequests()) {
                    for (ToolExecutionRequest req : aiMessage.toolExecutionRequests()) {
                        // 合并工具上下文到 toolInput
                        String toolInput = req.arguments();
                        if (toolContext != null && !toolContext.isEmpty()) {
                            Map<String, Object> inputMap = JsonUtils.parse(toolInput,
                                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                            if (inputMap == null) {
                                inputMap = new java.util.HashMap<>();
                            }
                            inputMap.putAll(toolContext);
                            toolInput = JsonUtils.toString(inputMap);
                        }
                        String result = AiToolHelper.executeTool(req.name(), toolInput);
                        messages.add(aiMessage);
                        messages.add(new ToolExecutionResultMessage(req.id(), req.name(), result));
                    }
                    chatRequest = ChatRequest.builder()
                            .messages(messages)
                            .toolSpecifications(toolSpecs)
                            .build();
                    chatResponse = vendorWrapper.getChatModel().chat(chatRequest);
                    aiMessage = chatResponse.aiMessage();
                }
            } else {
                chatResponse = vendorWrapper.getChatModel().chat(messages);
            }
        } catch (Exception e) {
            logger.error("AI模型调用失败, configId={}, modelMain={}", configId, configData.getModelMain(), e);
            return ResponseData.errorMsg("AI模型调用失败: " + e.getMessage());
        }

        String responseData = chatResponse.aiMessage().text();
        TokenUsage tokenUsage = chatResponse.tokenUsage();
        sessionMsg.setRequestTokens(tokenUsage != null && tokenUsage.inputTokenCount() != null
                ? tokenUsage.inputTokenCount() : 0);
        sessionMsg.setResponseTokens(tokenUsage != null && tokenUsage.outputTokenCount() != null
                ? tokenUsage.outputTokenCount() : 0);
        sessionMsg.setResponseEndDate(SystemClock.nowDate());
        sessionMsg.setResponseInfo(responseData);
        saveSessionMsg(sessionMsg);
        return ResponseData.success(responseData);
    }

    /**
     * ChatClient 流式调用
     */
    public static Flux<String> chatGenerate(long saasId, long userId, int userType, String userInfo, long configId, String systemPrompt, String userPrompt, List<AiToolCallInfo> toolList, Map<String, Object> toolContext, MultipartFile[] fileList, long[] ragLibIds) {
        AiVendorClientWrapper vendorWrapper = AiVendorHelper.getClientWrapper(configId);
        if (vendorWrapper == null) {
            return Flux.just(ResponseData.errorMsg("ChatClient获取失败！").toString());
        }
        AiModelConfigData configData = vendorWrapper.getConfigData();
        if (StringUtils.isBlank(systemPrompt)) {
            systemPrompt = configData.getModelParamBox().getParam("systemPrompt", "");
        }
        // 初始化会话信息
        AiSessionInfo sessionInfo = loadSession(saasId, userId, SessionType.COMMON.getValue(), null).getData();
        if (sessionInfo == null) {
            ResponseData<AiSessionInfo> responseData = initSession(saasId, userId, userType, userInfo, configId, SessionType.COMMON.getValue(), userPrompt, null, systemPrompt, toolList, ragLibIds);
            if (responseData.isNotSuccess()) {
                return Flux.just(responseData.toString());
            } else {
                sessionInfo = responseData.getData();
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
        if (ragLibIds == null || ragLibIds.length == 0) {
            if (StringUtils.isNotBlank(sessionInfo.getRagConfig())) {
                ragLibIds = JsonUtils.parse(sessionInfo.getRagConfig(), long[].class);
            }
        }
        if (ragLibIds != null && ragLibIds.length > 0) {
            ragContent = queryRagInfo(ragLibIds, userPrompt).getData();
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
        if (hasTools) {
            ChatRequest chatRequest = ChatRequest.builder()
                    .messages(messages)
                    .toolSpecifications(toolSpecs)
                    .build();
            ChatResponse response = vendorWrapper.getChatModel().chat(chatRequest);
            AiMessage aiMessage = response.aiMessage();
            while (aiMessage.hasToolExecutionRequests()) {
                for (ToolExecutionRequest req : aiMessage.toolExecutionRequests()) {
                    String toolInput = req.arguments();
                    if (toolContext != null && !toolContext.isEmpty()) {
                        Map<String, Object> inputMap = JsonUtils.parse(toolInput,
                                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                        if (inputMap == null) {
                            inputMap = new java.util.HashMap<>();
                        }
                        inputMap.putAll(toolContext);
                        toolInput = JsonUtils.toString(inputMap);
                    }
                    String result = AiToolHelper.executeTool(req.name(), toolInput);
                    messages.add(aiMessage);
                    messages.add(new ToolExecutionResultMessage(req.id(), req.name(), result));
                }
                chatRequest = ChatRequest.builder()
                        .messages(messages)
                        .toolSpecifications(toolSpecs)
                        .build();
                response = vendorWrapper.getChatModel().chat(chatRequest);
                aiMessage = response.aiMessage();
            }
            // 工具执行完毕后用流式返回最终文本
            String finalText = aiMessage.text();
            TokenUsage tokenUsage = response.tokenUsage();
            sessionMsg.setResponseStartDate(SystemClock.nowDate());
            sessionMsg.setRequestTokens(tokenUsage != null && tokenUsage.inputTokenCount() != null
                    ? tokenUsage.inputTokenCount() : 0);
            sessionMsg.setResponseTokens(tokenUsage != null && tokenUsage.outputTokenCount() != null
                    ? tokenUsage.outputTokenCount() : 0);
            sessionMsg.setResponseEndDate(SystemClock.nowDate());
            sessionMsg.setResponseInfo(finalText);
            saveSessionMsg(sessionMsg);
            return Flux.just(new AiChatSentEvent<>(finalText).toString());
        }

        // 无工具：直接流式调用
        return Flux.create(sink -> {
            sessionMsg.setResponseStartDate(SystemClock.nowDate());
            StringBuilder responseBuilder = new StringBuilder();
            java.util.concurrent.atomic.AtomicReference<ChatResponse> lastResponseRef =
                    new java.util.concurrent.atomic.AtomicReference<>();

            vendorWrapper.getStreamingChatModel().chat(messages, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String token) {
                    responseBuilder.append(token);
                    sink.next(new AiChatSentEvent<>(token).toString());
                }

                @Override
                public void onCompleteResponse(ChatResponse completeResponse) {
                    lastResponseRef.set(completeResponse);
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
                    logger.error("流式聊天异常", error);
                    sessionMsg.setResponseEndDate(SystemClock.nowDate());
                    sessionMsg.setResponseInfo("[ERROR] " + error.getMessage());
                    saveSessionMsg(sessionMsg);
                    sink.error(error);
                }
            });
        });
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
        return dao.queryForSingleObject(AiSessionInfo.class, new AiSessionInfoQueryParam(saasId).userId(userId).sessionType(sessionType).id(sessionId));
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
        AiVendorClientWrapper vendorWrapper = AiVendorHelper.getClientWrapper(configId);
        if (vendorWrapper == null) {
            return ResponseData.errorMsg("ChatClient获取失败!");
        }
        AiModelConfigData configData = vendorWrapper.getConfigData();
        if (StringUtils.isBlank(systemPrompt)) {
            systemPrompt = configData.getModelParamBox().getParam("systemPrompt", "");
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
                return ResponseData.errorMsg("处理文件[" + file.getOriginalFilename() + "]时发生错误!" + e.getMessage());
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
        for (int i = 0; i < ragLibIds.length; i++) {
            sb.append(AiRagService.query(ragLibIds[i], userPrompt));
        }
        return ResponseData.success(sb.toString());
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
            dao.executeCommand(sql, new Object[]{SystemClock.nowDate(), sessionMsg.getRequestTokens(), sessionMsg.getResponseTokens(), sessionMsg.getSaasId(), sessionMsg.getSessionId()});
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
                return Flux.just(readFileData.toString());
            } else {
                String[] fileData = readFileData.getData();
                fileInfo = fileData[0];
                fileContent = fileData[1];
            }
        }
        //检查rag信息。
        String ragContent = null;
        if (ragLibIds == null || ragLibIds.length == 0) {
            if (StringUtils.isNotBlank(sessionInfo.getRagConfig())) {
                ragLibIds = JsonUtils.parse(sessionInfo.getRagConfig(), long[].class);
            }
        }
        if (ragLibIds != null && ragLibIds.length > 0) {
            ragContent = queryRagInfo(ragLibIds, userPrompt).getData();
        }
        String contextData = null;
        if (StringUtils.isNotBlank(ragContent) || StringUtils.isNotBlank(fileContent)) {
            contextData = buildContextInfo(ragContent, fileContent);
        }
        // 初始化会话消息
        AiSessionMsg sessionMsg = initSessionMsg(saasId, userId, userType, userInfo, configId, sessionInfo.getId(), systemPrompt, userPrompt, toolList, fileInfo, ragLibIds, contextData);
        // 获取LangChain4j客户端
        AiVendorClientWrapper vendorWrapper = AiVendorHelper.getClientWrapper(sessionInfo.getConfigId());
        if (vendorWrapper == null) {
            return Flux.just(ResponseData.errorMsg("ChatClient获取失败！").toString());
        }

        // === LangChain4j 流式调用（加载历史消息） ===
        List<ChatMessage> messages = AiMysqlChatMemory.load(sessionInfo.getId());
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
            ChatRequest chatRequest = ChatRequest.builder()
                    .messages(messages)
                    .toolSpecifications(toolSpecs)
                    .build();
            ChatResponse response = vendorWrapper.getChatModel().chat(chatRequest);
            AiMessage aiMessage = response.aiMessage();
            while (aiMessage.hasToolExecutionRequests()) {
                for (ToolExecutionRequest req : aiMessage.toolExecutionRequests()) {
                    String toolInput = req.arguments();
                    if (toolContext != null && !toolContext.isEmpty()) {
                        Map<String, Object> inputMap = JsonUtils.parse(toolInput,
                                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                        if (inputMap == null) {
                            inputMap = new java.util.HashMap<>();
                        }
                        inputMap.putAll(toolContext);
                        toolInput = JsonUtils.toString(inputMap);
                    }
                    String result = AiToolHelper.executeTool(req.name(), toolInput);
                    messages.add(aiMessage);
                    messages.add(new ToolExecutionResultMessage(req.id(), req.name(), result));
                }
                chatRequest = ChatRequest.builder()
                        .messages(messages)
                        .toolSpecifications(toolSpecs)
                        .build();
                response = vendorWrapper.getChatModel().chat(chatRequest);
                aiMessage = response.aiMessage();
            }
            String finalText = aiMessage.text();
            TokenUsage tokenUsage = response.tokenUsage();
            sessionMsg.setResponseStartDate(SystemClock.nowDate());
            sessionMsg.setRequestTokens(tokenUsage != null && tokenUsage.inputTokenCount() != null
                    ? tokenUsage.inputTokenCount() : 0);
            sessionMsg.setResponseTokens(tokenUsage != null && tokenUsage.outputTokenCount() != null
                    ? tokenUsage.outputTokenCount() : 0);
            sessionMsg.setResponseEndDate(SystemClock.nowDate());
            sessionMsg.setResponseInfo(finalText);
            saveSessionMsg(sessionMsg);
            return Flux.just(new AiChatSentEvent<>(finalText).toString());
        }

        // 无工具：直接流式调用
        return Flux.create(sink -> {
            sessionMsg.setResponseStartDate(SystemClock.nowDate());
            StringBuilder responseBuilder = new StringBuilder();
            java.util.concurrent.atomic.AtomicReference<ChatResponse> lastResponseRef =
                    new java.util.concurrent.atomic.AtomicReference<>();

            vendorWrapper.getStreamingChatModel().chat(messages, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String token) {
                    responseBuilder.append(token);
                    sink.next(new AiChatSentEvent<>(token).toString());
                }

                @Override
                public void onCompleteResponse(ChatResponse completeResponse) {
                    lastResponseRef.set(completeResponse);
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
                    logger.error("流式聊天异常", error);
                    sessionMsg.setResponseEndDate(SystemClock.nowDate());
                    sessionMsg.setResponseInfo("[ERROR] " + error.getMessage());
                    saveSessionMsg(sessionMsg);
                    sink.error(error);
                }
            });
        });
    }

    /**
     * 列表SessionInfo.
     *
     * @return
     */
    public static ResponseData<DataList<AiSessionInfo>> listSessionInfo(AiSessionInfoQueryParam queryParam) {
        return dao.list(AiSessionInfo.class, queryParam);
    }

    /**
     * 列表SessionMsg.
     *
     * @return
     */
    public static ResponseData<DataList<AiSessionMsg>> listSessionMsg(AiSessionMsgQueryParam queryParam) {
        return dao.list(AiSessionMsg.class, queryParam);
    }

}
