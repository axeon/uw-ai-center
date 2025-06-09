package uw.ai.center.vo;

import uw.common.util.JsonUtils;

/**
 * AiChatSentEvent聊天发送事件。
 */
public class AiChatSentEvent<T> {

    /**
     * 数据。
     */
    private T data;

    public AiChatSentEvent(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JsonUtils.toString(this);
    }
}
