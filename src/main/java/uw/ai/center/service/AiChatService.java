package uw.ai.center.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import uw.ai.center.constant.SessionType;
import uw.ai.center.dto.AiSessionInfoQueryParam;
import uw.ai.center.dto.AiSessionMsgQueryParam;
import uw.ai.center.entity.AiSessionInfo;
import uw.ai.center.entity.AiSessionMsg;
import uw.ai.center.tool.AiToolHelper;
import uw.ai.center.vendor.AiVendorClientWrapper;
import uw.ai.center.vendor.AiVendorHelper;
import uw.ai.center.vo.AiModelConfigData;
import uw.ai.center.vo.SessionConversationData;
import uw.ai.vo.AiToolCallInfo;
import uw.common.app.constant.CommonState;
import uw.common.dto.ResponseData;
import uw.common.util.JsonUtils;
import uw.common.util.SystemClock;
import uw.dao.DaoManager;
import uw.dao.DataList;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;


/**
 * AiChatService。
 */
public class AiChatService {

    private static final Logger logger = LoggerFactory.getLogger(AiChatService.class);
    private static final DaoManager dao = DaoManager.getInstance();

    /**
     * ChatClient 简单调用。
     */
    public static ResponseData<String> generate(long saasId, long userId, int userType, String userInfo, long configId, String systemPrompt, String userPrompt,
                                                List<AiToolCallInfo> toolList, Map<String, Object> toolContext, MultipartFile[] fileList, long[] ragLibIds) {
        // 获取ChatClient
        AiVendorClientWrapper chatClientWrapper = AiVendorHelper.getChatClient(configId);
        if (chatClientWrapper == null) {
            return ResponseData.errorMsg("ChatClient获取失败!");
        }
        //获取基础信息。
        AiModelConfigData configData = chatClientWrapper.getConfigData();
        if (StringUtils.isBlank(systemPrompt)) {
            systemPrompt = configData.getModelParamBox().getParam("systemPrompt", "");
        }
        // 初始化会话信息
        AiSessionInfo sessionInfo = loadSession(saasId, userId, SessionType.COMMON.getValue(), null).getData();
        if (sessionInfo == null) {
            ResponseData<AiSessionInfo> responseData = initSession(saasId, userId, userType, userInfo, configId, SessionType.COMMON.getValue(), userPrompt, null, systemPrompt,
                    toolList, ragLibIds);
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
        AiSessionMsg sessionMsg = initSessionMsg(sessionInfo.getId(), systemPrompt, userPrompt, toolList, fileInfo, ragLibIds, contextData);
        // 设置请求开始时间
        sessionMsg.setResponseStartDate(SystemClock.nowDate());
        ChatClient.ChatClientRequestSpec chatClientRequestSpec = chatClientWrapper.getChatClient().prompt();
        if (StringUtils.isNotBlank(systemPrompt)) {
            chatClientRequestSpec.system(systemPrompt);
        }
        if (StringUtils.isNotBlank(contextData)) {
            userPrompt = userPrompt + contextData;
        }
        chatClientRequestSpec.user(userPrompt);
        // 设置工具调用
        if (toolList != null && !toolList.isEmpty()) {
            chatClientRequestSpec.toolCallbacks(AiToolHelper.getToolCallbacks(toolList));
            Map<String, Object> paramMap = new HashMap<>();
            if (toolContext != null) {
                paramMap.putAll(toolContext);
            }
            paramMap.put("saasId", saasId);
            paramMap.put("userId", userId);
            paramMap.put("userType", userType);
            paramMap.put("userInfo", userInfo);
            chatClientRequestSpec.toolContext(paramMap);
        }
        ChatResponse chatResponse = chatClientRequestSpec.call().chatResponse();
        String responseData = chatResponse.getResult().getOutput().getText();
        Usage tokenUsage = chatResponse.getMetadata().getUsage();
        sessionMsg.setRequestTokens(tokenUsage.getPromptTokens());
        sessionMsg.setResponseTokens(tokenUsage.getCompletionTokens());
        sessionMsg.setResponseEndDate(SystemClock.nowDate());
        sessionMsg.setResponseInfo(responseData);
        // 保存会话信息
        saveSessionMsg(sessionMsg);
        return ResponseData.success(responseData);
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
    public static ResponseData<AiSessionInfo> initSession(long saasId, long userId, int userType, String userInfo, long configId, int sessionType, String sessionName,
                                                          Integer windowSize, String systemPrompt, List<AiToolCallInfo> toolList, long[] ragLibIds) {
        // 获取ChatClient
        AiVendorClientWrapper chatClientWrapper = AiVendorHelper.getChatClient(configId);
        if (chatClientWrapper == null) {
            return ResponseData.errorMsg("ChatClient获取失败!");
        }
        //获取基础信息。
        AiModelConfigData configData = chatClientWrapper.getConfigData();
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
                TikaDocumentReader reader = new TikaDocumentReader(new InputStreamResource(inputStream));
                List<Document> documents = reader.get(); // 假设返回List<Document>
                if (!documents.isEmpty()) {
                    for (Document document : documents) {
                        content.append(document.getText()).append("\n");
                    }
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
    public static AiSessionMsg initSessionMsg(long sessionId, String systemPrompt, String userPrompt, List<AiToolCallInfo> toolList, String fileInfo, long[] ragIds,
                                              String contextInfo) {
        long msgId = dao.getSequenceId(AiSessionMsg.class);
        AiSessionMsg sessionMsg = new AiSessionMsg();
        sessionMsg.setId(msgId);
        sessionMsg.setSessionId(sessionId);
        sessionMsg.setSystemPrompt(systemPrompt);
        sessionMsg.setUserPrompt(userPrompt);
        sessionMsg.setToolConfig(JsonUtils.toString(toolList));
        sessionMsg.setFileConfig(fileInfo);
        sessionMsg.setRagConfig(JsonUtils.toString(ragIds));
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
            String sql = "update ai_session_info set last_update=?, msg_num=msg_num+1,request_tokens=request_tokens+?,response_tokens=response_tokens+? where id=?";
            dao.executeCommand(sql, new Object[]{SystemClock.nowDate(), sessionMsg.getRequestTokens(), sessionMsg.getResponseTokens(), sessionMsg.getSessionId()});
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
    public static Flux<ResponseData<String>> chat(long saasId, long userId, int userType, String userInfo, long sessionId, String systemPrompt, String userPrompt,
                                                  List<AiToolCallInfo> toolList, Map<String, Object> toolContext, MultipartFile[] fileList, long[] ragLibIds) {
        // 初始化会话信息
        AiSessionInfo sessionInfo;
        if (sessionId > 0) {
            sessionInfo = loadSession(saasId, userId, SessionType.CHAT.getValue(), sessionId).getData();
        } else {
            sessionInfo = null;
        }
        if (sessionInfo == null) {
            return Flux.just(ResponseData.errorMsg("Session会话不存在！"));
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
                return Flux.just(readFileData.raw());
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
        AiSessionMsg sessionMsg = initSessionMsg(sessionInfo.getId(), systemPrompt, userPrompt, toolList, fileInfo, ragLibIds, contextData);
        // 获取ChatClient
        AiVendorClientWrapper chatClientWrapper = AiVendorHelper.getChatClient(sessionInfo.getConfigId());
        if (chatClientWrapper == null) {
            return Flux.just(ResponseData.errorMsg("ChatClient获取失败！"));
        }
        // 会话消息的会话ID和消息ID
        SessionConversationData conversationData = new SessionConversationData(sessionMsg.getSessionId(), sessionMsg.getId());
        // 返回信息
        StringBuilder responseData = new StringBuilder();
        // 最后一个ChatResponse信息
        AtomicReference<ChatResponse> lastResponseRef = new AtomicReference<>();
        ChatClient.ChatClientRequestSpec chatClientRequestSpec = chatClientWrapper.getChatClient().prompt();
        if (StringUtils.isNotBlank(systemPrompt)) {
            chatClientRequestSpec.system(systemPrompt);
        }
        if (StringUtils.isNotBlank(contextData)) {
            userPrompt = userPrompt + contextData;
        }
        chatClientRequestSpec.user(userPrompt);
        // 设置工具
        if (toolList != null && !toolList.isEmpty()) {
            chatClientRequestSpec.toolCallbacks(AiToolHelper.getToolCallbacks(toolList));
            Map<String, Object> paramMap = new HashMap<>();
            if (toolContext != null) {
                paramMap.putAll(toolContext);
            }
            paramMap.put("saasId", saasId);
            paramMap.put("userId", userId);
            paramMap.put("userType", userType);
            paramMap.put("userInfo", userInfo);
            chatClientRequestSpec.toolContext(paramMap);
        }
        // 保存会话信息
        return chatClientRequestSpec.advisors(spec -> spec.param(CONVERSATION_ID, conversationData.toString())).stream().chatResponse().doFirst(() -> {
            sessionMsg.setResponseStartDate(SystemClock.nowDate());
        }).doOnComplete(() -> {
            ChatResponse lastResponse = lastResponseRef.get();
            Usage tokenUsage = lastResponse.getMetadata().getUsage();
            sessionMsg.setRequestTokens(tokenUsage.getPromptTokens());
            sessionMsg.setResponseTokens(tokenUsage.getCompletionTokens());
            sessionMsg.setResponseEndDate(SystemClock.nowDate());
            sessionMsg.setResponseInfo(responseData.toString());
            // 保存会话信息
            saveSessionMsg(sessionMsg);
        }).filter(x -> x != null && x.getResult() != null && x.getResult().getOutput() != null && x.getResult().getOutput().getText() != null).map(x -> {
            String content = x.getResult().getOutput().getText();
            responseData.append(content);
            lastResponseRef.set(x);
            return ResponseData.of(0, content, ResponseData.STATE_SUCCESS, null, null);
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
