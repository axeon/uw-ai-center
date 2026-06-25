package uw.ai.center.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 重排结果。
 * <p>封装 qwen3-rerank 等重排模型的返回值。items 已按 relevanceScore 降序排列。
 */
@Schema(title = "重排结果", description = "重排模型的返回结果，已按相关性得分降序排列")
public class RerankResult {

    /**
     * 重排后的文档列表（已按 relevanceScore 降序）。
     */
    @Schema(title = "重排后的文档列表", description = "已按相关性得分降序排列")
    private List<RerankItem> items;

    /**
     * 本次请求消耗的总 Token 数。
     */
    @Schema(title = "Token使用量", description = "本次请求消耗的总 Token 数")
    private int totalTokens;

    public RerankResult() {
    }

    public RerankResult(List<RerankItem> items, int totalTokens) {
        this.items = items;
        this.totalTokens = totalTokens;
    }

    public List<RerankItem> getItems() {
        return items;
    }

    public void setItems(List<RerankItem> items) {
        this.items = items;
    }

    public int getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(int totalTokens) {
        this.totalTokens = totalTokens;
    }

    /**
     * 单条文档的重排结果。
     */
    @Schema(title = "重排结果项", description = "单条文档的重排结果")
    public static class RerankItem {

        /**
         * 该结果对应原始 documents 列表中的下标。
         */
        @Schema(title = "原始文档下标", description = "该结果对应原始 documents 列表中的下标")
        private int index;

        /**
         * 与查询的语义相关性得分，取值 0.0~1.0，分数越高越相关。
         */
        @Schema(title = "相关性得分", description = "与查询的语义相关性得分，取值 0.0~1.0")
        private double relevanceScore;

        /**
         * 文档原文。仅当调用方 returnDocuments=true 时回带，否则为 null。
         */
        @Schema(title = "文档原文", description = "文档原文，仅当 returnDocuments=true 时回带")
        private String document;

        public RerankItem() {
        }

        public RerankItem(int index, double relevanceScore, String document) {
            this.index = index;
            this.relevanceScore = relevanceScore;
            this.document = document;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public double getRelevanceScore() {
            return relevanceScore;
        }

        public void setRelevanceScore(double relevanceScore) {
            this.relevanceScore = relevanceScore;
        }

        public String getDocument() {
            return document;
        }

        public void setDocument(String document) {
            this.document = document;
        }
    }
}
