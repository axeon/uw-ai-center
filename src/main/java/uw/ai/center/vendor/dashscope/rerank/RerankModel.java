package uw.ai.center.vendor.dashscope.rerank;

import uw.ai.center.vendor.client.RerankClient;
import uw.ai.center.vo.RerankResult;

import java.util.List;

/**
 * DashScope 重排模型接口（qwen3-rerank）
 *
 */
public interface RerankModel extends AutoCloseable {

    /**
     * 对 documents 按与 query 的语义相关性重新排序。
     *
     * @param query     查询文本
     * @param documents 候选文档列表
     * @return 重排结果（已按 relevanceScore 降序）
     */
    RerankResult rerank(String query, List<String> documents);
}
