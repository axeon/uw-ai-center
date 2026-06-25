package uw.ai.center.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uw.ai.center.entity.AiModelApi;

import java.util.Date;

/**
 * AiApiConfigData API连接配置数据对象。
 * <p>包装 {@link AiModelApi} 实体，提供 getUrl/getApiKeyRaw 等便捷访问；
 * {@link #getApiKey()} 返回掩码后的密钥用于前端展示，{@link #getApiKeyRaw()} 返回明文仅供服务端构建 Vendor 客户端。
 */
public class AiApiConfigData {

    /**
     * API配置实体。
     */
    private AiModelApi aiModelApi;

    /**
     * 默认构造（反序列化用）。
     */
    public AiApiConfigData() {
    }

    /**
     * 构造 API 连接配置包装对象。
     *
     * @param aiModelApi API 配置实体
     */
    public AiApiConfigData(AiModelApi aiModelApi) {
        this.aiModelApi = aiModelApi;
    }

    public long getId() {
        return aiModelApi.getId();
    }

    public long getSaasId() {
        return aiModelApi.getSaasId();
    }

    public long getMchId() {
        return aiModelApi.getMchId();
    }

    public String getApiCode() {
        return aiModelApi.getApiCode();
    }

    public String getApiName() {
        return aiModelApi.getApiName();
    }

    public String getApiDesc() {
        return aiModelApi.getApiDesc();
    }

    public String getApiUrl() {
        return aiModelApi.getApiUrl();
    }

    /**
     * 获取API密钥（掩码处理，用于前端展示）。
     * 只显示前半部分，后半部分用****代替，方便辨识同时保护密钥安全。
     */
    public String getApiKey() {
        return AiModelApi.maskApiKey(aiModelApi.getApiKey());
    }

    /**
     * 获取API密钥明文（仅供服务端内部使用，如构建Vendor客户端，不序列化到前端）。
     */
    @JsonIgnore
    public String getApiKeyRaw() {
        return aiModelApi.getApiKey();
    }

    public int getState() {
        return aiModelApi.getState();
    }

    public Date getCreateDate() {
        return aiModelApi.getCreateDate();
    }

    public Date getModifyDate() {
        return aiModelApi.getModifyDate();
    }
}
