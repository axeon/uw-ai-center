package uw.ai.center.vendor.client;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.ai.center.constant.ModelType;
import uw.ai.center.vo.AiModelConfigData;

/**
 * CHAT 类型客户端：同步 + 流式聊天模型。
 */
@Schema(title = "Chat客户端", description = "Chat模型客户端，包含同步与流式两种")
public class ChatClient extends AiModelClient {

    @Schema(title = "同步聊天模型", description = "同步聊天模型")
    private final ChatModel chatModel;

    @Schema(title = "流式聊天模型", description = "流式聊天模型")
    private final StreamingChatModel streamingChatModel;

    public ChatClient(AiModelConfigData configData,
                      ChatModel chatModel,
                      StreamingChatModel streamingChatModel)
    {
        super(configData, ModelType.CHAT);
        this.chatModel = chatModel;
        this.streamingChatModel = streamingChatModel;
    }

    public ChatModel getChatModel() {
        return chatModel;
    }

    public StreamingChatModel getStreamingChatModel() {
        return streamingChatModel;
    }

    @Override
    protected void doClose() {
        closeQuietly(chatModel);
        closeQuietly(streamingChatModel);
    }
}
