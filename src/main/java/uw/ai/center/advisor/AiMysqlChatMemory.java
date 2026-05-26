package uw.ai.center.advisor;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.ai.center.entity.AiSessionMsg;
import uw.common.app.constant.CommonState;
import uw.dao.DaoManager;

import java.util.ArrayList;
import java.util.List;

/**
 * MySQL 会话记忆（LangChain4j）。
 */
public class AiMysqlChatMemory {

    private static final Logger logger = LoggerFactory.getLogger(AiMysqlChatMemory.class);
    private static final DaoManager dao = DaoManager.getInstance();

    /**
     * 从 MySQL 加载会话历史消息。
     */
    public static List<ChatMessage> load(long sessionId) {
        List<ChatMessage> messages = new ArrayList<>(16);
        try {
            dao.list(AiSessionMsg.class,
                    "select * from ai_session_msg where session_id=? and state=? order by id desc",
                    new Object[]{sessionId, CommonState.ENABLED.getValue()}).onSuccess(msgList -> {
                for (AiSessionMsg msg : msgList) {
                    messages.add(new UserMessage(msg.getUserPrompt()));
                    messages.add(new AiMessage(msg.getResponseInfo()));
                }
            });
        } catch (Exception e) {
            logger.error("加载会话历史消息失败, sessionId={}", sessionId, e);
        }
        return messages;
    }

    /**
     * 软删除会话消息。
     */
    public static void clear(long sessionId) {
        dao.executeCommand("update ai_session_msg set state=? where session_id=? and state=?",
                new Object[]{CommonState.DELETED.getValue(), sessionId,
                        CommonState.ENABLED.getValue()});
    }
}
