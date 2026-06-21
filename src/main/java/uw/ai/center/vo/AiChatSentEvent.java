package uw.ai.center.vo;

import uw.common.util.JsonUtils;

/**
 * AiChatSentEvent聊天发送事件。
 * <p>流式聊天（SSE）场景下，每个 token 或最终文本都包装成本事件再序列化为 JSON 字符串下发。
 *
 * @param <T> 携带的数据类型（通常为 String）
 */
public class AiChatSentEvent<T> {

    /**
     * 数据。
     */
    private T data;

    /**
     * 构造聊天发送事件。
     *
     * @param data 携带的数据
     */
    public AiChatSentEvent(T data) {
        this.data = data;
    }

    /**
     * 获取数据。
     *
     * @return 携带的数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置数据。
     *
     * @param data 携带的数据
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * 将本事件序列化为 JSON 字符串，用于 SSE 流式下发。
     *
     * @return JSON 字符串
     */
    @Override
    public String toString() {
        return JsonUtils.toString(this);
    }
}
