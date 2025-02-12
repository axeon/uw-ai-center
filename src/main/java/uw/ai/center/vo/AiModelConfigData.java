package uw.ai.center.vo;

import com.fasterxml.jackson.core.type.TypeReference;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.center.service.AiVendor;
import uw.ai.center.service.AiVendorHelper;
import uw.httpclient.json.JsonInterfaceHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * AiModelConfigData 大模型配置。
 */
public class AiModelConfigData {

    private final AiModelConfig aiModelConfig;
    /**
     * public参数信息集合，所有人可见。
     */
    private Map<String, String> publicParamMap;
    /**
     * model参数信息集合，管理员可见。
     */
    private Map<String, String> modelParamMap;
    /**
     * 日志类型参数信息集合，仅管理员可见。
     */
    private Map<String, String> logParamMap;

    public AiModelConfigData(AiModelConfig aiModelConfig) {
        this.aiModelConfig = aiModelConfig;
        AiVendor aiVendor = AiVendorHelper.getVendor( aiModelConfig.getVendorClass() );
        if (aiVendor != null) {
            publicParamMap = new HashMap<String, String>();
            for (AiVendor.ConfigParam configParam : aiVendor.pubicParam()) {
                publicParamMap.put( configParam.getKey(), configParam.getValue() );
            }
            publicParamMap.putAll( JsonInterfaceHelper.JSON_CONVERTER.parse( aiModelConfig.getPublicData(), new TypeReference<Map<? extends String, ? extends String>>() {
            } ) );

            modelParamMap = new HashMap<String, String>();
            for (AiVendor.ConfigParam configParam : aiVendor.modelParam()) {
                modelParamMap.put( configParam.getKey(), configParam.getValue() );
            }
            modelParamMap.putAll( JsonInterfaceHelper.JSON_CONVERTER.parse( aiModelConfig.getModelData(), new TypeReference<Map<? extends String, ? extends String>>() {
            } ) );

            logParamMap = new HashMap<String, String>();
            for (AiVendor.ConfigParam configParam : aiVendor.logParam()) {
                logParamMap.put( configParam.getKey(), configParam.getValue() );
            }
            logParamMap.putAll( JsonInterfaceHelper.JSON_CONVERTER.parse( aiModelConfig.getLogData(), new TypeReference<Map<? extends String, ? extends String>>() {
            } ) );

        }
    }

    /**
     * 获取状态。
     */
    public int getState() {
        return aiModelConfig.getState();
    }

    /**
     * 获取日志配置。
     */
    public String getLogData() {
        return aiModelConfig.getLogData();
    }

    /**
     * 获取API配置。
     */
    public String getModelData() {
        return aiModelConfig.getModelData();
    }

    /**
     * 获取公开配置。
     */
    public String getPublicData() {
        return aiModelConfig.getPublicData();
    }

    /**
     * 获取修改时间。
     */
    public Date getModifyDate() {
        return aiModelConfig.getModifyDate();
    }

    /**
     * 获取创建时间。
     */
    public Date getCreateDate() {
        return aiModelConfig.getCreateDate();
    }

    /**
     * 获取服务商描述。
     */
    public String getModelDesc() {
        return aiModelConfig.getModelDesc();
    }

    /**
     * 获取服务商名称。
     */
    public String getModelName() {
        return aiModelConfig.getModelName();
    }

    /**
     * 获取服务商代码。
     */
    public String getModelCode() {
        return aiModelConfig.getModelCode();
    }

    /**
     * 获取服务商类。
     */
    public String getVendorClass() {
        return aiModelConfig.getVendorClass();
    }

    /**
     * 获取商户ID。
     */
    public long getMchId() {
        return aiModelConfig.getMchId();
    }

    /**
     * 获取SAAS ID。
     */
    public long getSaasId() {
        return aiModelConfig.getSaasId();
    }

    /**
     * 获取ID。
     */
    public long getId() {
        return aiModelConfig.getId();
    }

    /**
     * 获取PUB参数集合。
     *
     * @return
     */
    public Map<String, String> getPubicicParamMap() {
        return publicParamMap;
    }


    /**
     * 获取API参数信息集合
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
    public Map<String, String> getLogParamMap() {
        return logParamMap;
    }


    /**
     * 获得指定的pub参数。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public String getPubicParam(String paramName) {
        return getParam( publicParamMap, paramName, "" );
    }

    /**
     * 获得指定的pub参数。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public String getPubicParam(String paramName, String defaultValue) {
        return getParam( publicParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的pub参数（int类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public int getPubicIntParam(String paramName, int defaultValue) {
        return getIntParam( publicParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的pub参数（int类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public int getPubicIntParam(String paramName) {
        return getIntParam( publicParamMap, paramName, 0 );
    }

    /**
     * 获得指定的pub参数（long类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public Long getPubicLongParam(String paramName, long defaultValue) {
        return getLongParam( publicParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的pub参数（long类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public Long getPubicLongParam(String paramName) {
        return getLongParam( publicParamMap, paramName, 0 );
    }

    /**
     * 获得指定的pub参数（boolean类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public boolean getPubicBooleanParam(String paramName, boolean defaultValue) {
        return getBooleanParam( publicParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的pub参数（boolean类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public boolean getPubicBooleanParam(String paramName) {
        return getBooleanParam( publicParamMap, paramName, false );
    }

    /**
     * 获得指定的pub参数（double类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public double getPubicDoubleParam(String paramName, double defaultValue) {
        return getDoubleParam( publicParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的pub参数（double类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public double getPubicDoubleParam(String paramName) {
        return getDoubleParam( publicParamMap, paramName, 0 );
    }

    /**
     * 获得指定的pub参数（float类型）。
     *
     * @param paramName    参数名字
     * @param defaultValue 集合没值时或者报异常，默认返回值
     * @return 参数值
     */
    public float getPubicFloatParam(String paramName, float defaultValue) {
        return getFloatParam( publicParamMap, paramName, defaultValue );
    }

    /**
     * 获得指定的pub参数（float类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public float getPubicFloatParam(String paramName) {
        return getFloatParam( publicParamMap, paramName, 0 );
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
     * 获得指定的LogLevel参数（int类型）。
     *
     * @param paramName 参数名字
     * @return 参数值
     */
    public int getLogLevel(String paramName) {
        return getIntParam( logParamMap, paramName, -1 );
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
