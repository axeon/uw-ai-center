package uw.ai.center.vo;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vendor.AiVendorHelper;
import uw.httpclient.json.JsonInterfaceHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * AiModelConfigData 大模型配置。
 */
public class AiModelConfigData {

    private static final Logger logger = LoggerFactory.getLogger( AiModelConfigData.class );
    private final AiModelConfig aiModelConfig;
    /**
     * vendor参数信息集合，所有人可见。
     */
    private Map<String, String> vendorParamMap;
    /**
     * model参数信息集合，管理员可见。
     */
    private Map<String, String> modelParamMap;
    /**
     * 嵌入参数信息集合，仅管理员可见。
     */
    private Map<String, String> embedParamMap;

    public AiModelConfigData(AiModelConfig aiModelConfig) {
        this.aiModelConfig = aiModelConfig;
        AiVendor aiVendor = AiVendorHelper.getVendor( aiModelConfig.getVendorClass() );
        if (aiVendor != null) {
            vendorParamMap = new HashMap<String, String>();
            for (AiVendor.ConfigParam configParam : aiVendor.vendorParam()) {
                vendorParamMap.put( configParam.getKey(), configParam.getValue() );
            }
            if (StringUtils.isNotBlank( aiModelConfig.getVendorData() )) {
                try {
                    vendorParamMap.putAll( JsonInterfaceHelper.JSON_CONVERTER.parse( aiModelConfig.getVendorData(), new TypeReference<Map<? extends String, ? extends String>>() {
                    } ) );
                } catch (Exception e) {
                    logger.error( e.getMessage(), e );
                }
            }

            modelParamMap = new HashMap<String, String>();
            for (AiVendor.ConfigParam configParam : aiVendor.modelParam()) {
                modelParamMap.put( configParam.getKey(), configParam.getValue() );
            }
            if (StringUtils.isNotBlank( aiModelConfig.getModelData() )) {
                try {
                    modelParamMap.putAll( JsonInterfaceHelper.JSON_CONVERTER.parse( aiModelConfig.getModelData(), new TypeReference<Map<? extends String, ? extends String>>() {
                    } ) );
                } catch (Exception e) {
                    logger.error( e.getMessage(), e );
                }
            }

            embedParamMap = new HashMap<String, String>();
            for (AiVendor.ConfigParam configParam : aiVendor.embedParam()) {
                embedParamMap.put( configParam.getKey(), configParam.getValue() );
            }
            if (StringUtils.isNotBlank( aiModelConfig.getEmbedData() )) {
                try {
                    embedParamMap.putAll( JsonInterfaceHelper.JSON_CONVERTER.parse( aiModelConfig.getEmbedData(), new TypeReference<Map<? extends String, ? extends String>>() {
                    } ) );
                } catch (Exception e) {
                    logger.error( e.getMessage(), e );
                }
            }

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
     * 获取PUB参数集合。
     *
     * @return 配置信息
     */
    public Map<String, String> getVendorParamMap() {
        return vendorParamMap;
    }


    /**
     * 获取model参数信息集合
     *
     * @return 集合
     */
    public Map<String, String> getModelParamMap() {
        return modelParamMap;
    }


    /**
     * 获取日志参数信息集合
     *
     * @return 集合
     */
    public Map<String, String> getEmbedParamMap() {
        return embedParamMap;
    }


    /**
     * 获得指定的pub参数。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public String getVendorParam(String paramName) {
        return getParam( vendorParamMap, paramName, "" );
    }

    /**
     * 获得指定的pub参数。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public String getVendorParam(String paramName, String defaultValue) {
        return getParam( vendorParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的pub参数（int类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public int getVendorIntParam(String paramName, int defaultValue) {
        return getIntParam( vendorParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的pub参数（int类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public int getVendorIntParam(String paramName) {
        return getIntParam( vendorParamMap, paramName, 0 );
    }

    /**
     * 获得指定的pub参数（long类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public Long getVendorLongParam(String paramName, long defaultValue) {
        return getLongParam( vendorParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的pub参数（long类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public Long getVendorLongParam(String paramName) {
        return getLongParam( vendorParamMap, paramName, 0 );
    }

    /**
     * 获得指定的pub参数（boolean类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public boolean getVendorBooleanParam(String paramName, boolean defaultValue) {
        return getBooleanParam( vendorParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的pub参数（boolean类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public boolean getVendorBooleanParam(String paramName) {
        return getBooleanParam( vendorParamMap, paramName, false );
    }

    /**
     * 获得指定的pub参数（double类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public double getVendorDoubleParam(String paramName, double defaultValue) {
        return getDoubleParam( vendorParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的pub参数（double类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public double getVendorDoubleParam(String paramName) {
        return getDoubleParam( vendorParamMap, paramName, 0 );
    }

    /**
     * 获得指定的pub参数（float类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public float getVendorFloatParam(String paramName, float defaultValue) {
        return getFloatParam( vendorParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的pub参数（float类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public float getVendorFloatParam(String paramName) {
        return getFloatParam( vendorParamMap, paramName, 0 );
    }

    /**
     * 获得指定的api参数。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public String getModelParam(String paramName) {
        return getParam( modelParamMap, paramName, "" );
    }

    /**
     * 获得指定的api参数。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public String getModelParam(String paramName, String defaultValue) {
        return getParam( modelParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的api参数（int类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public int getModelIntParam(String paramName, int defaultValue) {
        return getIntParam( modelParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的api参数（int类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public int getModelIntParam(String paramName) {
        return getIntParam( modelParamMap, paramName, 0 );
    }

    /**
     * 获得指定的api参数（long类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public Long getModelLongParam(String paramName, long defaultValue) {
        return getLongParam( modelParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的api参数（long类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public Long getModelLongParam(String paramName) {
        return getLongParam( modelParamMap, paramName, 0 );
    }

    /**
     * 获得指定的api参数（boolean类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public boolean getModelBooleanParam(String paramName, boolean defaultValue) {
        return getBooleanParam( modelParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的api参数（boolean类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public boolean getModelBooleanParam(String paramName) {
        return getBooleanParam( modelParamMap, paramName, false );
    }

    /**
     * 获得指定的api参数（double类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public double getModelDoubleParam(String paramName, double defaultValue) {
        return getDoubleParam( modelParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的api参数（double类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public double getModelDoubleParam(String paramName) {
        return getDoubleParam( modelParamMap, paramName, 0 );
    }

    /**
     * 获得指定的api参数（float类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public float getModelFloatParam(String paramName, float defaultValue) {
        return getFloatParam( modelParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的api参数（float类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public float getModelFloatParam(String paramName) {
        return getFloatParam( modelParamMap, paramName, 0 );
    }


    /**
     * 获得指定的api参数。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public String getEmbedParam(String paramName) {
        return getParam( modelParamMap, paramName, "" );
    }

    /**
     * 获得指定的api参数。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public String getEmbedParam(String paramName, String defaultValue) {
        return getParam( modelParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的api参数（int类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public int getEmbedIntParam(String paramName, int defaultValue) {
        return getIntParam( modelParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的api参数（int类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public int getEmbedIntParam(String paramName) {
        return getIntParam( modelParamMap, paramName, 0 );
    }

    /**
     * 获得指定的api参数（long类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public Long getEmbedLongParam(String paramName, long defaultValue) {
        return getLongParam( modelParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的api参数（long类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public Long getEmbedLongParam(String paramName) {
        return getLongParam( modelParamMap, paramName, 0 );
    }

    /**
     * 获得指定的api参数（boolean类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public boolean getEmbedBooleanParam(String paramName, boolean defaultValue) {
        return getBooleanParam( modelParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的api参数（boolean类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public boolean getEmbedBooleanParam(String paramName) {
        return getBooleanParam( modelParamMap, paramName, false );
    }

    /**
     * 获得指定的api参数（double类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public double getEmbedDoubleParam(String paramName, double defaultValue) {
        return getDoubleParam( modelParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的api参数（double类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public double getEmbedDoubleParam(String paramName) {
        return getDoubleParam( modelParamMap, paramName, 0 );
    }

    /**
     * 获得指定的api参数（float类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public float getEmbedFloatParam(String paramName, float defaultValue) {
        return getFloatParam( modelParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的api参数（float类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public float getEmbedFloatParam(String paramName) {
        return getFloatParam( modelParamMap, paramName, 0 );
    }


    /**
     * 公用的获取参数值方法
     *
     * @param params       参数集合
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数的值
     */
    private String getParam(Map<String, String> params, String paramName, String defaultValue) {
        if (params == null || params.isEmpty()) {
            return defaultValue;
        }
        String temp = params.get( paramName );
        if (temp != null && !temp.isEmpty()) {
            return temp;
        }
        return defaultValue;
    }

    /**
     * 公用的获取参数值方法(int)
     *
     * @param params       参数集合
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认的返回值
     * @return 参数的值
     */
    private int getIntParam(Map<String, String> params, String paramName, int defaultValue) {
        if (params == null || params.isEmpty()) {
            return defaultValue;
        }
        String temp = params.get( paramName );
        if (temp != null && !temp.isEmpty()) {
            try {
                return Integer.parseInt( temp );
            } catch (Exception ignored) {
            }
        }
        return defaultValue;
    }

    /**
     * 公用的获取参数值方法(long)
     *
     * @param params       参数集合
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认的返回值
     * @return 参数的值
     */
    private long getLongParam(Map<String, String> params, String paramName, long defaultValue) {
        if (params == null || params.isEmpty()) {
            return defaultValue;
        }
        String temp = params.get( paramName );
        if (temp != null && !temp.isEmpty()) {
            try {
                return Long.parseLong( temp );
            } catch (Exception ignored) {
            }
        }
        return defaultValue;
    }

    /**
     * 公用的获取参数值方法(double)
     *
     * @param params       参数集合
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认的返回值
     * @return 参数的值
     */
    private double getDoubleParam(Map<String, String> params, String paramName, double defaultValue) {
        if (params == null || params.isEmpty()) {
            return defaultValue;
        }
        String temp = params.get( paramName );
        if (temp != null && !temp.isEmpty()) {
            try {
                return Double.parseDouble( temp );
            } catch (Exception ignored) {
            }
        }
        return defaultValue;

    }

    /**
     * 公用的获取参数值方法(float)
     *
     * @param params       参数集合
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认的返回值
     * @return 参数的值
     */
    private float getFloatParam(Map<String, String> params, String paramName, float defaultValue) {
        if (params == null || params.isEmpty()) {
            return defaultValue;
        }
        String temp = params.get( paramName );
        if (temp != null && !temp.isEmpty()) {
            try {
                return Float.parseFloat( temp );
            } catch (Exception ignored) {
            }
        }
        return defaultValue;
    }

    /**
     * 公用的获取参数值方法(boolean)
     *
     * @param params       参数集合
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认的返回值
     * @return 参数的值
     */
    private boolean getBooleanParam(Map<String, String> params, String paramName, boolean defaultValue) {
        if (params == null || params.isEmpty()) {
            return defaultValue;
        }
        String temp = params.get( paramName );
        if (temp != null && !temp.isEmpty()) {
            try {
                return Boolean.parseBoolean( temp );
            } catch (Exception ignored) {
            }
        }
        return defaultValue;
    }
}
