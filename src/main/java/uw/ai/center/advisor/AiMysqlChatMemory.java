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
     * 默认加载最近 50 条会话消息（每条含一问一答，即 50 轮对话），
     * 在 windowSize 未显式设置或非正值时作为兜底，避免一次性加载全部历史。
     */
    private static final int DEFAULT_HISTORY_LIMIT = 50;

    /**
     * 单次加载的硬上限：防止 windowSize 被人为调大导致 token 爆炸。
     */
    private static final int MAX_HISTORY_LIMIT = 500;

    /**
     * 从 MySQL 加载会话历史消息。
     * 先按 id 降序取最近 N 条（N 由 windowSize 控制），再反转为升序，
     * 保证历史消息时间顺序正确（最旧在前、最新在后）。
     *
     * @param sessionId  会话ID
     * @param windowSize 每轮对话消息对数量上限（&lt;=0 时使用默认值 50，&gt;MAX_HISTORY_LIMIT 时截断）
     */
    public static List<ChatMessage> load(long sessionId, int windowSize) {
        int limit = resolveLimit(windowSize);
        List<ChatMessage> messages = new ArrayList<>(limit * 2);
        try {
            dao.list(AiSessionMsg.class,
                    "select * from ai_session_msg where session_id=? and state=? order by id desc limit ?",
                    new Object[]{sessionId, CommonState.ENABLED.getValue(), limit}).onSuccess(msgList -> {
                // 数据库按 id desc 返回（最新在前），反向遍历后追加（最旧在前）
                List<AiSessionMsg> raw = msgList.list();
                for (int i = raw.size() - 1; i >= 0; i--) {
                    AiSessionMsg msg = raw.get(i);
                    messages.add(new UserMessage(msg.getUserPrompt()));
                    messages.add(new AiMessage(msg.getResponseInfo()));
                }
            });
        } catch (Exception e) {
            logger.error("加载会话历史消息失败, sessionId={}, windowSize={}", sessionId, windowSize, e);
        }
        return messages;
    }

    /**
     * 计算实际拉取的消息条数：windowSize 非正则使用默认值，超过硬上限则截断。
     */
    private static int resolveLimit(int windowSize) {
        if (windowSize <= 0) {
            return DEFAULT_HISTORY_LIMIT;
        }
        return Math.min(windowSize, MAX_HISTORY_LIMIT);
    }

    /**
     * 软删除会话消息（将启用状态的消息置为删除状态）。
     * <p>清除历史后，load() 不再返回这些消息，相当于重置多轮对话上下文。
     *
     * @param sessionId 会话ID
     */
    public static void clear(long sessionId) {
        dao.execute("update ai_session_msg set state=? where session_id=? and state=?",
                new Object[]{CommonState.DELETED.getValue(), sessionId,
                        CommonState.ENABLED.getValue()});
    }
}
