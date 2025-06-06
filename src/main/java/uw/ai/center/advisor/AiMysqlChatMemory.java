package uw.ai.center.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import uw.ai.center.entity.AiSessionMsg;
import uw.ai.center.vo.SessionConversationData;
import uw.common.app.constant.CommonState;
import uw.dao.DaoManager;

import java.util.ArrayList;
import java.util.List;


/**
 * 会话内存的mysql实现类，保存会话内的消息。
 */
public class AiMysqlChatMemory implements ChatMemory {

    private static final Logger logger = LoggerFactory.getLogger( AiMysqlChatMemory.class );
    /**
     * dao。
     */
    private static final DaoManager dao = DaoManager.getInstance();


    /**
     * 不实现，手动前端发起请求保存用户的消息和大模型回复的消息。
     */
    @Override
    public void add(String conversationId, List<Message> messages) {

    }

    @Override
    public List<Message> get(String conversationId) {
        SessionConversationData conversationData = new SessionConversationData( conversationId );
        if (conversationData.getSessionId() > 0) {
            List<Message> messages = new ArrayList<>( 16 );
            dao.list( AiSessionMsg.class, "select * from ai_session_msg where session_id=? order by id desc",
                    new Object[]{conversationData.getSessionId()} ).onSuccess( msgList -> {
                for (AiSessionMsg msg : msgList) {
                    messages.add( new UserMessage( msg.getUserPrompt() ) );
                    messages.add( new AssistantMessage( msg.getResponseInfo() ) );
                }
            } );
            return messages;
        }
        return List.of();
    }


    /**
     * 清除会话内的消息
     *
     * @param conversationId 会话id
     */
    @Override
    public void clear(String conversationId) {
        SessionConversationData conversationData = new SessionConversationData( conversationId );
        dao.executeCommand( "update ai_session_msg set state=? where session_id=? and state=?", new Object[]{CommonState.DELETED.getValue(), conversationData.getSessionId(),
                CommonState.ENABLED.getValue()} );
    }
}
