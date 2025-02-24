package uw.ai.center.controller.open;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import uw.dao.DaoFactory;
import uw.dao.TransactionException;

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
    public Flux<String> chat(HttpServletResponse response, long sessionId, @RequestParam(defaultValue = "你是谁？") String question) {
        response.setCharacterEncoding( "UTF-8" );
        if (sessionId > 0) {
            AiSessionInfo info = AiSessionService.loadSession( 0L, 0L, sessionId );
            if (info == null) {
                return Flux.just( "会话不存在" );
            }
        } else {
            sessionId = dao.getSequenceId( AiSessionInfo.class );

        }
        long msgId = dao.getSequenceId( AiSessionMsg.class );
        ConversationData conversationData = new ConversationData(sessionId, msgId);
        StringBuilder responseData = new StringBuilder();
        AiSessionMsg msg = new AiSessionMsg();
        msg.setId( msgId );
        msg.setSessionId( sessionId );
        msg.setUserInfo( question );
        msg.setState( StateCommon.ENABLED.getValue() );
        msg.setRequestDate( new java.util.Date() );
        return AiVendorHelper.getChatClient( 1L ).prompt().user( question )
                .advisors( spec -> spec.param( CHAT_MEMORY_CONVERSATION_ID_KEY, conversationData.toString() )
                        .param( CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10 ) ).stream().content().doFirst( () -> {msg.setResponseStartDate( new java.util.Date() );} ).doOnNext( responseData::append ).doOnComplete( () -> {
                            msg.setResponseEndDate( new java.util.Date() );
                            msg.setResponseInfo( responseData.toString() );
                    try {
                        dao.save( msg );
                    } catch (TransactionException e) {
                        logger.error( e.getMessage(), e );
                    }
                } );
    }
}
