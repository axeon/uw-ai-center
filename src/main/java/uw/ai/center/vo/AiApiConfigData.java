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

    public AiModelApi getAiModelApi() {
        return aiModelApi;
    }

    public void setAiModelApi(AiModelApi aiModelApi) {
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

    public String getApiKey() {
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