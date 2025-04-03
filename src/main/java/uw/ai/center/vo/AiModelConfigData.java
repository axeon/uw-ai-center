package uw.ai.center.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vendor.AiVendorHelper;
import uw.app.common.helper.ConfigParamHelper;
import uw.app.common.vo.ConfigParamBox;

import java.util.Date;

/**
 * AiModelConfigData 大模型配置。
 */
public class AiModelConfigData {

    private static final Logger logger = LoggerFactory.getLogger( AiModelConfigData.class );

    /**
     * Ai模型配置。
     */
    private final AiModelConfig aiModelConfig;
    /**
     * vendor参数信息集合，所有人可见。
     */
    private ConfigParamBox vendorParamBox;
    /**
     * model参数信息集合，管理员可见。
     */
    private ConfigParamBox modelParamBox;
    /**
     * 嵌入参数信息集合，仅管理员可见。
     */
    private ConfigParamBox embedParamBox;

    public AiModelConfigData(AiModelConfig aiModelConfig) {
        this.aiModelConfig = aiModelConfig;
        AiVendor aiVendor = AiVendorHelper.getVendor( aiModelConfig.getVendorClass() );
        if (aiVendor != null) {
            vendorParamBox = ConfigParamHelper.buildParamBox( aiVendor.vendorParam(), aiModelConfig.getVendorData() ).getData();
            modelParamBox = ConfigParamHelper.buildParamBox( aiVendor.modelParam(), aiModelConfig.getModelData() ).getData();
            embedParamBox = ConfigParamHelper.buildParamBox( aiVendor.embedParam(), aiModelConfig.getEmbedData() ).getData();
        }
    }

    /**
     * 获取ID。
     */
    public long getId() {
        return aiModelConfig.getId();
    }

    /**
     * 获取SAAS ID。
     */
    public long getSaasId() {
        return aiModelConfig.getSaasId();
    }

    /**
     * 获取商户ID。
     */
    public long getMchId() {
        return aiModelConfig.getMchId();
    }

    /**
     * 获取服务商类。
     */
    public String getVendorClass() {
        return aiModelConfig.getVendorClass();
    }

    /**
     * 获取服务商代码。
     */
    public String getConfigCode() {
        return aiModelConfig.getConfigCode();
    }

    /**
     * 获取服务商名称。
     */
    public String getConfigName() {
        return aiModelConfig.getConfigName();
    }

    /**
     * 获取服务商描述。
     */
    public String getConfigDesc() {
        return aiModelConfig.getConfigDesc();
    }

    /**
     * 获取api地址。
     */
    public String getApiUrl() {
        return aiModelConfig.getApiUrl();
    }

    /**
     * 获取api key。
     */
    public String getApiKey() {
        return aiModelConfig.getApiKey();
    }

    /**
     * 获取主模型。
     */
    public String getModelMain() {
        return aiModelConfig.getModelMain();
    }

    /**
     * 获取嵌入模型。
     */
    public String getModelEmbed() {
        return aiModelConfig.getModelEmbed();
    }

    /**
     * 获取服务商配置。
     */
    public String getVendorData() {
        return aiModelConfig.getVendorData();
    }

    /**
     * 获取模型配置。
     */
    public String getModelData() {
        return aiModelConfig.getModelData();
    }

    /**
     * 获取嵌入配置。
     */
    public String getEmbedData() {
        return aiModelConfig.getEmbedData();
    }

    /**
     * 获取创建时间。
     */
    public Date getCreateDate() {
        return aiModelConfig.getCreateDate();
    }

    /**
     * 获取修改时间。
     */
    public Date getModifyDate() {
        return aiModelConfig.getModifyDate();
    }

    /**
     * 获取状态。
     */
    public int getState() {
        return aiModelConfig.getState();
    }

    /**
     * 获取服务商参数。
     */
    public ConfigParamBox getVendorParamBox() {
        return vendorParamBox;
    }

    /**
     * 获取模型参数。
     */
    public ConfigParamBox getModelParamBox() {
        return modelParamBox;
    }

    /**
     * 获取嵌入参数。
     */
    public ConfigParamBox getEmbedParamBox() {
        return embedParamBox;
    }
}
