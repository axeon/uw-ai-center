package uw.ai.center.controller.open;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import uw.ai.center.entity.AiSessionInfo;
import uw.ai.center.entity.AiSessionMsg;
import uw.ai.center.service.AiSessionService;
import uw.ai.center.vendor.AiVendorHelper;
import uw.ai.center.vo.ConversationData;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.common.constant.StateCommon;
import uw.common.dto.ResponseData;
import uw.dao.DaoFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * 测试接口。
 */
@RestController
@RequestMapping("/open/ai")
@Tag(name = "测试接口")
@ResponseAdviceIgnore
public class AiChatController {
    private static final Logger logger = LoggerFactory.getLogger( AiChatController.class );
    DaoFactory dao = DaoFactory.getInstance();

    public static void main(String[] args) {
        System.out.println( StringUtils.leftPad( "Hello World!", 5, "..." ) );
    }


    /**
     * ChatClient 简单调用
     */
    @GetMapping("/generate")
    public String generate(@RequestParam(defaultValue = "你是谁？") String question) {
        return AiVendorHelper.getChatClient( 1L ).prompt( question ).call().content();
    }

    /**
     * ChatClient 流式调用
     */
    @GetMapping("/chat")
    public Flux<String> chat(HttpServletResponse response, @RequestParam(defaultValue = "0") long sessionId, @RequestParam(defaultValue = "你是谁？") String question) {
        response.setCharacterEncoding( "UTF-8" );
        AiSessionInfo sessionInfo = null;
        if (sessionId > 0) {
            sessionInfo = AiSessionService.loadSession( 0L, 0L, sessionId );
            if (sessionInfo == null) {
                return Flux.just( ResponseData.errorMsg( "会话不存在" ).toString() );
            }
        } else {
            sessionInfo = AiSessionService.initSession( 0L, 0L, 0L, 0, 0L, "未知", "未知", "未知", question, "未知", 10 );
            if (sessionInfo == null) {
                return Flux.just( ResponseData.errorMsg( "会话创建失败" ).toString() );
            }
        }
        // 初始化会话消息
        AiSessionMsg sessionMsg = AiSessionService.initSessionMsg( sessionInfo.getId(), question);
        // 会话消息的会话ID和消息ID
        ConversationData conversationData = new ConversationData( sessionMsg.getSessionId(), sessionMsg.getId() );
        // 返回信息
        StringBuilder responseData = new StringBuilder();
        // 最后一个ChatResponse信息
        AtomicReference<ChatResponse> lastResponseRef = new AtomicReference<>();
        return AiVendorHelper.getChatClient( 1L ).prompt().user( question )
            .advisors( spec -> spec.param( CHAT_MEMORY_CONVERSATION_ID_KEY, conversationData.toString() ).param( CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10 ) ).stream().chatResponse()
            .doFirst( () -> {
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


