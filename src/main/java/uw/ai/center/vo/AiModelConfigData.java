package uw.ai.center.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vendor.AiVendorHelper;
import uw.common.app.helper.JsonConfigHelper;
import uw.common.app.vo.JsonConfigBox;

import java.util.Date;

/**
 * AiModelConfigData 模型配置数据对象（聚合 ApiConfig + ModelConfig）。
 * <p>由 FusionCache 缓存，将 ai_model_config（模型配置）与 ai_model_api（API 连接配置）聚合为一个对象，
 * 并按 vendor 的 configParam 模板解析出参数盒 {@link JsonConfigBox}，供构建 Vendor 客户端时读取。
 */
public class AiModelConfigData {

    private static final Logger logger = LoggerFactory.getLogger(AiModelConfigData.class);

    /**
     * 模型配置实体。
     */
    private AiModelConfig aiModelConfig;

    /**
     * API连接配置。
     */
    private AiApiConfigData apiConfigData;

    /**
     * 配置参数信息集合。
     */
    private JsonConfigBox configParamBox;

    /**
     * 默认构造（反序列化用）。
     */
    public AiModelConfigData() {
    }

    /**
     * 构造并初始化参数盒。
     *
     * @param aiModelConfig 模型配置实体
     * @param apiConfigData API 连接配置
     */
    public AiModelConfigData(AiModelConfig aiModelConfig, AiApiConfigData apiConfigData) {
        this.aiModelConfig = aiModelConfig;
        this.apiConfigData = apiConfigData;
        initParamBox();
    }

    /**
     * 按 vendor 配置参数模板解析 modelData 为参数盒。
     */
    private void initParamBox() {
        if (aiModelConfig == null) {
            return;
        }
        AiVendor aiVendor = AiVendorHelper.getVendor(aiModelConfig.getVendorClass());
        if (aiVendor != null) {
            String modelData = aiModelConfig.getModelData();
            configParamBox = JsonConfigHelper.buildParamBox(aiVendor.configParam(), modelData).getData();
        }
    }

    public AiModelConfig getAiModelConfig() {
        return aiModelConfig;
    }

    /**
     * 设置模型配置实体与 API 配置，并重新初始化参数盒（配置变更后重建 Vendor 时使用）。
     *
     * @param aiModelConfig 模型配置实体
     * @param apiConfigData API 连接配置
     */
    public void setAiModelConfig(AiModelConfig aiModelConfig, AiApiConfigData apiConfigData) {
        this.aiModelConfig = aiModelConfig;
        this.apiConfigData = apiConfigData;
        initParamBox();
    }

    /**
     * 获取 API 连接配置。
     *
     * @return API 连接配置
     */
    public AiApiConfigData getApiConfigData() {
        return apiConfigData;
    }

    /**
     * 获取配置ID。
     *
     * @return ai_model_config.id
     */
    public long getId() {
        return aiModelConfig.getId();
    }

    public long getSaasId() {
        return aiModelConfig.getSaasId();
    }

    public long getMchId() {
        return aiModelConfig.getMchId();
    }

    public long getApiId() {
        return aiModelConfig.getApiId();
    }

    public String getVendorClass() {
        return aiModelConfig.getVendorClass();
    }

    public String getModelType() {
        return aiModelConfig.getModelType();
    }

    public String getModelTag() {
        return aiModelConfig.getModelTag();
    }

    public String getConfigCode() {
        return aiModelConfig.getConfigCode();
    }

    public String getConfigName() {
        return aiModelConfig.getConfigName();
    }

    public String getConfigDesc() {
        return aiModelConfig.getConfigDesc();
    }

    public String getModelName() {
        return aiModelConfig.getModelName();
    }

    public String getApiUrl() {
        return apiConfigData != null ? apiConfigData.getApiUrl() : null;
    }

    public String getApiKey() {
        return apiConfigData != null ? apiConfigData.getApiKey() : null;
    }

    /**
     * 获取API密钥明文（仅供服务端内部使用，如构建Vendor客户端，不序列化到前端）。
     */
    public String getApiKeyRaw() {
        return apiConfigData != null ? apiConfigData.getApiKeyRaw() : null;
    }

    public String getModelData() {
        return aiModelConfig.getModelData();
    }

    public Date getCreateDate() {
        return aiModelConfig.getCreateDate();
    }

    public Date getModifyDate() {
        return aiModelConfig.getModifyDate();
    }

    public int getState() {
        return aiModelConfig.getState();
    }

    public JsonConfigBox getConfigParamBox() {
        return configParamBox;
    }
}
