package uw.ai.center.vendor;

import uw.ai.center.vendor.client.ChatClient;
import uw.ai.center.vo.AiModelConfigData;

/**
 * CHAT 能力接口。
 * <p>声明 vendor 支持对话能力，{@link #buildChatClient} 是唯一的构建入口。
 * Vendor 类按需 implements 此接口——类的 implements 列表即能力清单，
 * 无需点开源码查看哪些方法被覆写。
 */
public interface AiChatVendor extends AiVendor {

    /**
     * 构建 CHAT 客户端实例。
     *
     * @param configData 聚合了 API 配置与模型配置的数据对象
     * @return 具体的 {@link ChatClient} 实例
     */
    ChatClient buildChatClient(AiModelConfigData configData);
}
