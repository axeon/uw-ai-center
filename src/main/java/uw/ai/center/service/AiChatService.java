package uw.ai.center.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;
import uw.ai.center.constant.SessionType;
import uw.ai.center.dto.AiSessionInfoQueryParam;
import uw.ai.center.dto.AiSessionMsgQueryParam;
import uw.ai.center.entity.AiSessionInfo;
import uw.ai.center.entity.AiSessionMsg;
import uw.ai.center.vendor.AiVendorHelper;
import uw.ai.center.vo.ConversationData;
import uw.common.constant.StateCommon;
import uw.common.dto.ResponseData;
import uw.dao.DaoFactory;
import uw.dao.DataList;
import uw.dao.TransactionException;
import uw.httpclient.json.JsonInterfaceHelper;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * AiChatService。
 */
public class AiChatService {

    private static final Logger logger = LoggerFactory.getLogger( AiChatService.class );
    private static final DaoFactory dao = DaoFactory.getInstance();

    /**
     * ChatClient 简单调用。
     */
    public static ResponseData<String> generate(long saasId, long userId, int userType, String userInfo, long configId, String userPrompt, String systemPrompt, String toolInfo) {
        // 获取ChatClient
        AiVendorHelper.ChatClientWrapper chatClientWrapper = AiVendorHelper.getChatClient( configId );
        if (chatClientWrapper == null) {
            return ResponseData.errorMsg( "ChatClient获取失败" );
        }
        // 初始化会话信息
        AiSessionInfo sessionInfo = loadSession( saasId, userId, SessionType.COMMON.getValue(), null ).getData();
        if (sessionInfo == null) {
            ResponseData<AiSessionInfo> responseData = initSession( saasId, userId, userType, userInfo, configId, SessionType.COMMON.getValue(), userPrompt,null, systemPrompt);
            if (responseData.isNotSuccess()) {
                return responseData.prototype();
            } else {
                sessionInfo = responseData.getData();
            }
        }
        // 初始化会话消息
        AiSessionMsg sessionMsg = initSessionMsg( sessionInfo.getId(), systemPrompt, userPrompt, toolInfo );
        // 设置请求开始时间
        sessionMsg.setResponseStartDate( new Date() );
        ChatResponse chatResponse = chatClientWrapper.chatClient().prompt().user( userPrompt ).call().chatResponse();
        String responseData = chatResponse.getResult().getOutput().getContent();
        Usage tokenUsage = chatResponse.getMetadata().getUsage();
        sessionMsg.setRequestTokens( tokenUsage.getPromptTokens() );
        sessionMsg.setResponseTokens( tokenUsage.getGenerationTokens() );
        sessionMsg.setResponseEndDate( new Date() );
        sessionMsg.setResponseInfo( responseData );
        // 保存会话信息
        saveSessionMsg( sessionMsg );
        return ResponseData.success( responseData );
    }

    /**
     * ChatClient 流式调用
     */
    public static Flux<String> chat(long saasId, long userId, long sessionId, String userPrompt) {
        // 初始化会话信息
        AiSessionInfo sessionInfo = null;
        if (sessionId > 0) {
            sessionInfo = loadSession( saasId, userId, SessionType.CHAT.getValue(), sessionId ).getData();
        }
        if (sessionInfo == null) {
            return Flux.just( ResponseData.errorMsg( "会话不存在" ).toString() );
        }
        // 获取ChatClient
        AiVendorHelper.ChatClientWrapper chatClientWrapper = AiVendorHelper.getChatClient( sessionInfo.getConfigId() );
        if (chatClientWrapper == null) {
            return Flux.just( ResponseData.errorMsg( "ChatClient获取失败" ).toString() );
        }
        // 初始化会话消息
        AiSessionMsg sessionMsg = initSessionMsg( sessionInfo.getId(), null, userPrompt,null );
        // 会话消息的会话ID和消息ID
        ConversationData conversationData = new ConversationData( sessionMsg.getSessionId(), sessionMsg.getId() );
        // 返回信息
        StringBuilder responseData = new StringBuilder();
        // 最后一个ChatResponse信息
        AtomicReference<ChatResponse> lastResponseRef = new AtomicReference<>();
        Flux<String> chatResponse = chatClientWrapper.chatClient().prompt().user( userPrompt ).advisors( spec -> spec.param( CHAT_MEMORY_CONVERSATION_ID_KEY,
                conversationData.toString() ).param( CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10 ) ).stream().chatResponse().doFirst( () -> {
            sessionMsg.setResponseStartDate( new Date() );
        } ).doOnComplete( () -> {
            ChatResponse lastResponse = lastResponseRef.get();
            Usage tokenUsage = lastResponse.getMetadata().getUsage();
            sessionMsg.setRequestTokens( tokenUsage.getPromptTokens() );
            sessionMsg.setResponseTokens( tokenUsage.getGenerationTokens() );
            sessionMsg.setResponseEndDate( new Date() );
            sessionMsg.setResponseInfo( responseData.toString() );
            // 保存会话信息
            saveSessionMsg( sessionMsg );
        } ).map( x -> {
            String content = x.getResult().getOutput().getContent();
            responseData.append( content );
            lastResponseRef.set( x );
            return content;
        } );
        return Flux.concat( Flux.just( JsonInterfaceHelper.JSON_CONVERTER.toString( sessionInfo ) ), chatResponse );
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
        try {
            return ResponseData.success( dao.queryForSingleObject( AiSessionInfo.class,
                    new AiSessionInfoQueryParam( saasId ).userId( userId ).sessionType( sessionType ).id( sessionId ) ) );
        } catch (TransactionException e) {
            logger.error( e.getMessage(), e );
            return ResponseData.errorMsg( e.getMessage() );
        }
    }

    /**
     * 列表SessionInfo.
     *
     * @return
     */
    public static ResponseData<DataList<AiSessionInfo>> listSessionInfo(AiSessionInfoQueryParam queryParam) {
        try {
            return ResponseData.success( dao.list( AiSessionInfo.class, queryParam ) );
        } catch (TransactionException e) {
            logger.error( e.getMessage(), e );
            return ResponseData.errorMsg( e.getMessage() );
        }
    }

    /**
     * 列表SessionMsg.
     *
     * @return
     */
    public static ResponseData<DataList<AiSessionMsg>> listSessionMsg(AiSessionMsgQueryParam queryParam) {
        try {
            return ResponseData.success( dao.list( AiSessionMsg.class, queryParam ) );
        } catch (TransactionException e) {
            logger.error( e.getMessage(), e );
            return ResponseData.errorMsg( e.getMessage() );
        }
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
                                                           Integer windowSize,String systemPrompt) {
        long sessionId = dao.getSequenceId( AiSessionInfo.class );
        AiSessionInfo sessionInfo = new AiSessionInfo();
        sessionInfo.setId( sessionId );
        sessionInfo.setSaasId( saasId );
        sessionInfo.setUserId( userId );
        sessionInfo.setUserType( userType );
        sessionInfo.setUserInfo( userInfo );
        sessionInfo.setConfigId( configId );
        sessionInfo.setSessionType( sessionType );
        sessionInfo.setSessionName( truncateWithEllipsis( sessionName, 200 ) );
        sessionInfo.setSystemPrompt( systemPrompt );
        sessionInfo.setMsgNum( 0 );
        sessionInfo.setWindowSize( windowSize );
        sessionInfo.setRequestTokens( 0 );
        sessionInfo.setResponseTokens( 0 );
        sessionInfo.setCreateDate( new java.util.Date() );
        sessionInfo.setLastUpdate( null );
        sessionInfo.setState( StateCommon.ENABLED.getValue() );
        try {
            return ResponseData.success( dao.save( sessionInfo ) );
        } catch (Exception e) {
            logger.error( e.getMessage(), e );
            return ResponseData.errorMsg( e.getMessage() );
        }
    }

    /**
     * 初始化sessionMsg.
     *
     * @param sessionId
     * @param userPrompt
     * @return
     */
    public static AiSessionMsg initSessionMsg(long sessionId, String systemPrompt, String userPrompt, String toolInfo) {
        long msgId = dao.getSequenceId( AiSessionMsg.class );
        AiSessionMsg sessionMsg = new AiSessionMsg();
        sessionMsg.setId( msgId );
        sessionMsg.setSessionId( sessionId );
        sessionMsg.setSystemPrompt( systemPrompt );
        sessionMsg.setUserPrompt( userPrompt );
        sessionMsg.setToolInfo( toolInfo );
        sessionMsg.setState( StateCommon.ENABLED.getValue() );
        sessionMsg.setRequestDate( new Date() );
        return sessionMsg;
    }

    /**
     * 保存sessionMsg.
     *
     * @param sessionMsg
     * @return
     */
    public static AiSessionMsg saveSessionMsg(AiSessionMsg sessionMsg) {
        try {
            // 更新sessionMsg
            dao.save( sessionMsg );
            // 更新session信息
            String sql = "update ai_session_info set last_update=?, msg_num=msg_num+1,request_tokens=request_tokens+?,response_tokens=response_tokens+? where id=?";
            dao.executeCommand( sql, new Object[]{new java.util.Date(), sessionMsg.getRequestTokens(), sessionMsg.getResponseTokens(), sessionMsg.getSessionId()} );
        } catch (TransactionException e) {
            logger.error( e.getMessage(), e );
        }
        return sessionMsg;
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
            return StringUtils.substring( input, 0, truncLen - 3 ) + "...";
        } else {
            // No truncation needed
            return input;
        }
    }

}
