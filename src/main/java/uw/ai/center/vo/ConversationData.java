package uw.ai.center.vo;

/**
 * 会话Id数据。
 * 本质上由 sessionId:sessionMsgId组成。
 */
public class ConversationData {

    /**
     * 会话ID。
     */
    private long sessionId;

    /**
     *  消息ID。
     */
    private long msgId;

    public ConversationData(String conversationId) {
        int pos  = conversationId.indexOf(":");
        if (pos > 0) {
            sessionId = Long.parseLong(conversationId.substring(0, pos));
            msgId = Long.parseLong(conversationId.substring(pos + 1));
        }
    }

    public ConversationData(long sessionId, long msgId) {
        this.sessionId = sessionId;
        this.msgId = msgId;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String toString() {
        return sessionId + ":" + msgId;
    }
}
