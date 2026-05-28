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
     * vendor参数信息集合。
     */
    private JsonConfigBox vendorParamBox;

    /**
     * model参数信息集合。
     */
    private JsonConfigBox modelParamBox;

    public AiModelConfigData() {
    }

    public AiModelConfigData(AiModelConfig aiModelConfig, AiApiConfigData apiConfigData) {
        this.aiModelConfig = aiModelConfig;
        this.apiConfigData = apiConfigData;
        initParamBoxes();
    }

    private void initParamBoxes() {
        if (aiModelConfig == null) {
            return;
        }
        AiVendor aiVendor = AiVendorHelper.getVendor(aiModelConfig.getVendorClass());
        if (aiVendor != null) {
            String modelData = aiModelConfig.getModelData();
            vendorParamBox = JsonConfigHelper.buildParamBox(aiVendor.vendorParam(), modelData).getData();
            modelParamBox = JsonConfigHelper.buildParamBox(aiVendor.modelParam(), modelData).getData();
        }
    }

    public AiModelConfig getAiModelConfig() {
        return aiModelConfig;
    }

    public void setAiModelConfig(AiModelConfig aiModelConfig, AiApiConfigData apiConfigData) {
        this.aiModelConfig = aiModelConfig;
        this.apiConfigData = apiConfigData;
        initParamBoxes();
    }

    public AiApiConfigData getApiConfigData() {
        return apiConfigData;
    }

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

    /** @deprecated 使用 getModelName() 替代 */
    @Deprecated
    public String getModelMain() {
        return aiModelConfig.getModelName();
    }

    /** @deprecated 使用 getModelName() 替代 */
    @Deprecated
    public String getModelEmbed() {
        return aiModelConfig.getModelName();
    }

    public String getApiUrl() {
        return apiConfigData != null ? apiConfigData.getApiUrl() : null;
    }

    public String getApiKey() {
        return apiConfigData != null ? apiConfigData.getApiKey() : null;
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

    public JsonConfigBox getVendorParamBox() {
        return vendorParamBox;
    }

    public JsonConfigBox getModelParamBox() {
        return modelParamBox;
    }
}