package uw.ai.center.vo;

/**
 * 会话Id数据。
 * 本质上由 sessionId:sessionMsgId组成。
 */
public class SessionConversationData {

    /**
     * 会话ID。
     */
    private long sessionId;

    /**
     *  消息ID。
     */
    private long msgId;

    /**
     * 从 "sessionId:msgId" 格式的字符串解析构造。
     * <p>格式不合法时保持字段为默认值 0，不抛异常。
     *
     * @param conversationId 会话标识字符串
     */
    public SessionConversationData(String conversationId) {
        try {
            if (conversationId == null) {
                return;
            }
            int pos = conversationId.indexOf(":");
            if (pos > 0) {
                sessionId = Long.parseLong(conversationId.substring(0, pos));
                msgId = Long.parseLong(conversationId.substring(pos + 1));
            }
        } catch (NumberFormatException e) {
            // 格式错误时不赋值，保持默认 0
        }
    }

    /**
     * 直接以数值构造。
     *
     * @param sessionId 会话ID
     * @param msgId    消息ID
     */
    public SessionConversationData(long sessionId, long msgId) {
        this.sessionId = sessionId;
        this.msgId = msgId;
    }

    /**
     * 获取会话ID。
     *
     * @return 会话ID
     */
    public long getSessionId() {
        return sessionId;
    }

    /**
     * 设置会话ID。
     *
     * @param sessionId 会话ID
     */
    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * 获取消息ID。
     *
     * @return 消息ID
     */
    public long getMsgId() {
        return msgId;
    }

    /**
     * 设置消息ID。
     *
     * @param msgId 消息ID
     */
    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    /**
     * 序列化为 "sessionId:msgId" 格式字符串。
     *
     * @return 会话标识字符串
     */
    public String toString() {
        return sessionId + ":" + msgId;
    }
}
