package uw.ai.center.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;
import uw.ai.center.entity.AiSessionInfo;
import uw.ai.center.entity.AiSessionMsg;
import uw.ai.center.vendor.AiVendorHelper;
import uw.ai.center.vo.ConversationData;
import uw.common.dto.ResponseData;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * AiChatService。
 */
public class AiChatService {

    /**
     * ChatClient 简单调用。
     */
    public static ResponseData<String> generate(long configId, long saasId, long userId, int userType, String userInfo, String userPrompt) {
        // 获取ChatClient
        ChatClient chatClient = AiVendorHelper.getChatClient( configId );
        if (chatClient == null) {
            return ResponseData.errorMsg( "ChatClient获取失败" );
        }
        // 初始化会话信息
        AiSessionInfo sessionInfo = AiSessionService.loadSession( saasId, userId, 0, null );
        if (sessionInfo == null) {
            sessionInfo = AiSessionService.initSession( saasId, userId, userType, userInfo, 0, userPrompt, "未知", 0 );
        }
        // 初始化会话消息
        AiSessionMsg sessionMsg = AiSessionService.initSessionMsg( sessionInfo.getId(), userPrompt );
        // 设置请求开始时间
        sessionMsg.setResponseStartDate( new Date() );
        ChatResponse chatResponse = AiVendorHelper.getChatClient( configId ).prompt().user( userPrompt ).call().chatResponse();
        String responseData = chatResponse.getResult().getOutput().getContent();
        Usage tokenUsage = chatResponse.getMetadata().getUsage();
        sessionMsg.setRequestTokens( tokenUsage.getPromptTokens() );
        sessionMsg.setResponseTokens( tokenUsage.getGenerationTokens() );
        sessionMsg.setResponseEndDate( new Date() );
        sessionMsg.setResponseInfo( responseData );
        // 保存会话信息
        AiSessionService.saveSessionMsg( sessionMsg );
        return ResponseData.success( responseData );
    }

    /**
     * ChatClient 流式调用
     */
    public static Flux<String> chat(long configId, long saasId, long userId, int userType, String userInfo, long sessionId, String userPrompt) {
        // 获取ChatClient
        ChatClient chatClient = AiVendorHelper.getChatClient( configId );
        if (chatClient == null) {
            return Flux.just( ResponseData.errorMsg( "ChatClient获取失败" ).toString() );
        }
        // 初始化会话信息
        AiSessionInfo sessionInfo = null;
        if (sessionId > 0) {
            sessionInfo = AiSessionService.loadSession( saasId, userId, 1, sessionId );
            if (sessionInfo == null) {
                return Flux.just( ResponseData.errorMsg( "会话不存在" ).toString() );
            }
        } else {
            sessionInfo = AiSessionService.initSession( saasId, userId, userType, userInfo, 1, userPrompt, "未知", 10 );
            if (sessionInfo == null) {
                return Flux.just( ResponseData.errorMsg( "会话创建失败" ).toString() );
            }
        }
        // 初始化会话消息
        AiSessionMsg sessionMsg = AiSessionService.initSessionMsg( sessionInfo.getId(), userPrompt );
        // 会话消息的会话ID和消息ID
        ConversationData conversationData = new ConversationData( sessionMsg.getSessionId(), sessionMsg.getId() );
        // 返回信息
        StringBuilder responseData = new StringBuilder();
        // 最后一个ChatResponse信息
        AtomicReference<ChatResponse> lastResponseRef = new AtomicReference<>();
        return AiVendorHelper.getChatClient( configId ).prompt().user( userPrompt ).advisors( spec -> spec.param( CHAT_MEMORY_CONVERSATION_ID_KEY, conversationData.toString() ).param( CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10 ) ).stream().chatResponse().doFirst( () -> {
            sessionMsg.setResponseStartDate( new Date() );
        } ).doOnComplete( () -> {
            ChatResponse lastResponse = lastResponseRef.get();
            Usage tokenUsage = lastResponse.getMetadata().getUsage();
            sessionMsg.setRequestTokens( tokenUsage.getPromptTokens() );
            sessionMsg.setResponseTokens( tokenUsage.getGenerationTokens() );
            sessionMsg.setResponseEndDate( new Date() );
            sessionMsg.setResponseInfo( responseData.toString() );
            // 保存会话信息
            AiSessionService.saveSessionMsg( sessionMsg );
        } ).map( x -> {
            String content = x.getResult().getOutput().getContent();
            responseData.append( content );
            lastResponseRef.set( x );
            return content;
        } );
    }
}
