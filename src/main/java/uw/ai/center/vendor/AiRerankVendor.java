package uw.ai.center.vendor;

import uw.ai.center.vendor.client.RerankClient;
import uw.ai.center.vo.AiModelConfigData;

/**
 * RERANK 能力接口。
 * <p>声明 vendor 支持重排能力，{@link #buildRerankClient} 是唯一的构建入口。
 * Vendor 类按需 implements 此接口——类的 implements 列表即能力清单，
 * 无需点开源码查看哪些方法被覆写。
 */
public interface AiRerankVendor extends AiVendor {

    /**
     * 构建 RERANK 客户端实例。
     *
     * @param configData 聚合了 API 配置与模型配置的数据对象
     * @return 具体的 {@link RerankClient} 实例
     */
    RerankClient buildRerankClient(AiModelConfigData configData);
}
