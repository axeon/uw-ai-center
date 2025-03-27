package uw.ai.center.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import uw.ai.center.entity.AiSessionMsg;
import uw.ai.center.vo.SessionConversationData;
import uw.common.constant.StateCommon;
import uw.dao.DaoFactory;
import uw.dao.DataList;
import uw.dao.TransactionException;

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
    private static final DaoFactory dao = DaoFactory.getInstance();

    /**
     * 不实现，手动前端发起请求保存用户的消息和大模型回复的消息。
     */
    @Override
    public void add(String conversationId, List<Message> messages) {

    }

    /**
     * 查询会话内的消息最新n条历史记录
     *
     * @param conversationId 会话id
     * @param lastN          最近n条
     * @return org.springframework.ai.chat.messages.Message格式的消息
     */
    @Override
    public List<Message> get(String conversationId, int lastN) {
        if (lastN <= 0) {
            return List.of();
        }
        SessionConversationData conversationData = new SessionConversationData( conversationId );
        List<Message> messages = new ArrayList<>( lastN );
        if (conversationData.getSessionId() > 0) {
            try {
                DataList<AiSessionMsg> msgList = dao.list( AiSessionMsg.class, "select * from ai_session_msg where session_id=? order by id desc",
                        new Object[]{conversationData.getSessionId()}, 0, (int) Math.ceil( lastN / 2.0f ), false );
                for (AiSessionMsg msg : msgList) {
                    messages.add( new UserMessage( msg.getUserPrompt() ) );
                    messages.add( new AssistantMessage( msg.getResponseInfo() ) );
                }
                return messages;
            } catch (TransactionException e) {
                logger.error( e.getMessage(), e );
            }
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
        try {
            dao.executeCommand( "update ai_session_msg set state=? where session_id=? and state=?", new Object[]{StateCommon.DELETED.getValue(), conversationData.getSessionId(),
                    StateCommon.ENABLED.getValue()} );
        } catch (TransactionException e) {
            logger.error( e.getMessage(), e );
        }
    }
}
