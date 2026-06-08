package uw.ai.center.vo;

import uw.ai.center.entity.AiModelApi;

import java.util.Date;

/**
 * AiApiConfigData API连接配置数据对象。
 */
public class AiApiConfigData {

    /**
     * API配置实体。
     */
    private AiModelApi aiModelApi;

    public AiApiConfigData() {
    }

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
