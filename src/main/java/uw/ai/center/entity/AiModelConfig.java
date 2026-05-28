package uw.ai.center.entity;

import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.util.JsonUtils;
import uw.dao.DataEntity;
import uw.dao.DataUpdateInfo;
import uw.dao.annotation.ColumnMeta;
import uw.dao.annotation.TableMeta;

import java.io.Serializable;

/**
 * AiModelConfig实体类
 * AI模型配置
 *
 * @author axeon
 */
@TableMeta(tableName = "ai_model_config", tableType = "table")
@Schema(title = "AI模型配置", description = "AI模型配置")
public class AiModelConfig implements DataEntity, Serializable {

    /**
     * ID
     */
    @ColumnMeta(columnName = "id", dataType = "long", dataSize = 19, nullable = false, primaryKey = true)
    @Schema(title = "ID", description = "ID")
    private long id;

    /**
     * SAAS ID
     */
    @ColumnMeta(columnName = "saas_id", dataType = "long", dataSize = 19, nullable = false, primaryKey = true)
    @Schema(title = "SAAS ID", description = "SAAS ID")
    private long saasId;

    /**
     * 商户ID
     */
    @ColumnMeta(columnName = "mch_id", dataType = "long", dataSize = 19, nullable = false)
    @Schema(title = "商户ID", description = "商户ID")
    private long mchId;

    /**
     * API配置ID
     */
    @ColumnMeta(columnName = "api_id", dataType = "long", dataSize = 19, nullable = false)
    @Schema(title = "API配置ID", description = "FK → ai_model_api.id")
    private long apiId;

    /**
     * 供应商类
     */
    @ColumnMeta(columnName = "vendor_class", dataType = "String", dataSize = 200, nullable = false)
    @Schema(title = "供应商类", description = "供应商类")
    private String vendorClass;

    /**
     * 模型类型: CHAT/EMBEDDING/RERANK/TTS/OCR
     */
    @ColumnMeta(columnName = "model_type", dataType = "String", dataSize = 50, nullable = false)
    @Schema(title = "模型类型", description = "CHAT/EMBEDDING/RERANK/TTS/OCR")
    private String modelType;

    /**
     * 配置代码
     */
    @ColumnMeta(columnName = "config_code", dataType = "String", dataSize = 100, nullable = true)
    @Schema(title = "配置代码", description = "配置代码")
    private String configCode;

    /**
     * 配置名称
     */
    @ColumnMeta(columnName = "config_name", dataType = "String", dataSize = 200, nullable = true)
    @Schema(title = "配置名称", description = "配置名称")
    private String configName;

    /**
     * 配置描述
     */
    @ColumnMeta(columnName = "config_desc", dataType = "String", dataSize = 65535, nullable = true)
    @Schema(title = "配置描述", description = "配置描述")
    private String configDesc;

    /**
     * 模型名
     */
    @ColumnMeta(columnName = "model_name", dataType = "String", dataSize = 100, nullable = false)
    @Schema(title = "模型名", description = "模型名")
    private String modelName;

    /**
     * 模型参数JSON（平铺: temperature/max_tokens等）
     */
    @ColumnMeta(columnName = "model_data", dataType = "String", dataSize = 1073741824, nullable = true)
    @Schema(title = "模型参数", description = "模型参数JSON")
    @JsonRawValue(value = false)
    private String modelData;

    /**
     * 状态
     */
    @ColumnMeta(columnName = "state", dataType = "int", dataSize = 10, nullable = true)
    @Schema(title = "状态", description = "状态")
    private int state;

    /**
     * 创建时间
     */
    @ColumnMeta(columnName = "create_date", dataType = "java.util.Date", dataSize = 23, nullable = true)
    @Schema(title = "创建时间", description = "创建时间")
    private java.util.Date createDate;

    /**
     * 修改时间
     */
    @ColumnMeta(columnName = "modify_date", dataType = "java.util.Date", dataSize = 23, nullable = true)
    @Schema(title = "修改时间", description = "修改时间")
    private java.util.Date modifyDate;

    /**
     * 数据更新信息.
     */
    private transient DataUpdateInfo _UPDATED_INFO = null;

    /**
     * 是否加载完成.
     */
    private transient boolean _IS_LOADED;

    @Override
    public String ENTITY_TABLE() {
        return "ai_model_config";
    }

    @Override
    public String ENTITY_NAME() {
        return "AI模型配置";
    }

    @Override
    public Serializable ENTITY_ID() {
        return getId();
    }

    @Override
    public DataUpdateInfo GET_UPDATED_INFO() {
        return this._UPDATED_INFO;
    }

    @Override
    public void CLEAR_UPDATED_INFO() {
        _UPDATED_INFO = null;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "id", this.id, id, !_IS_LOADED);
        this.id = id;
    }

    public AiModelConfig id(long id) {
        setId(id);
        return this;
    }

    public long getSaasId() {
        return this.saasId;
    }

    public void setSaasId(long saasId) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "saasId", this.saasId, saasId, !_IS_LOADED);
        this.saasId = saasId;
    }

    public AiModelConfig saasId(long saasId) {
        setSaasId(saasId);
        return this;
    }

    public long getMchId() {
        return this.mchId;
    }

    public void setMchId(long mchId) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "mchId", this.mchId, mchId, !_IS_LOADED);
        this.mchId = mchId;
    }

    public AiModelConfig mchId(long mchId) {
        setMchId(mchId);
        return this;
    }

    public long getApiId() {
        return this.apiId;
    }

    public void setApiId(long apiId) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "apiId", this.apiId, apiId, !_IS_LOADED);
        this.apiId = apiId;
    }

    public AiModelConfig apiId(long apiId) {
        setApiId(apiId);
        return this;
    }

    public String getVendorClass() {
        return this.vendorClass;
    }

    public void setVendorClass(String vendorClass) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "vendorClass", this.vendorClass, vendorClass, !_IS_LOADED);
        this.vendorClass = vendorClass;
    }

    public AiModelConfig vendorClass(String vendorClass) {
        setVendorClass(vendorClass);
        return this;
    }

    public String getModelType() {
        return this.modelType;
    }

    public void setModelType(String modelType) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "modelType", this.modelType, modelType, !_IS_LOADED);
        this.modelType = modelType;
    }

    public AiModelConfig modelType(String modelType) {
        setModelType(modelType);
        return this;
    }

    public String getConfigCode() {
        return this.configCode;
    }

    public void setConfigCode(String configCode) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "configCode", this.configCode, configCode, !_IS_LOADED);
        this.configCode = configCode;
    }

    public AiModelConfig configCode(String configCode) {
        setConfigCode(configCode);
        return this;
    }

    public String getConfigName() {
        return this.configName;
    }

    public void setConfigName(String configName) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "configName", this.configName, configName, !_IS_LOADED);
        this.configName = configName;
    }

    public AiModelConfig configName(String configName) {
        setConfigName(configName);
        return this;
    }

    public String getConfigDesc() {
        return this.configDesc;
    }

    public void setConfigDesc(String configDesc) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "configDesc", this.configDesc, configDesc, !_IS_LOADED);
        this.configDesc = configDesc;
    }

    public AiModelConfig configDesc(String configDesc) {
        setConfigDesc(configDesc);
        return this;
    }

    public String getModelName() {
        return this.modelName;
    }

    public void setModelName(String modelName) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "modelName", this.modelName, modelName, !_IS_LOADED);
        this.modelName = modelName;
    }

    public AiModelConfig modelName(String modelName) {
        setModelName(modelName);
        return this;
    }

    public String getModelData() {
        return this.modelData;
    }

    public void setModelData(String modelData) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "modelData", this.modelData, modelData, !_IS_LOADED);
        this.modelData = modelData;
    }

    public AiModelConfig modelData(String modelData) {
        setModelData(modelData);
        return this;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "state", this.state, state, !_IS_LOADED);
        this.state = state;
    }

    public AiModelConfig state(int state) {
        setState(state);
        return this;
    }

    public java.util.Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(java.util.Date createDate) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "createDate", this.createDate, createDate, !_IS_LOADED);
        this.createDate = createDate;
    }

    public AiModelConfig createDate(java.util.Date createDate) {
        setCreateDate(createDate);
        return this;
    }

    public java.util.Date getModifyDate() {
        return this.modifyDate;
    }

    public void setModifyDate(java.util.Date modifyDate) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "modifyDate", this.modifyDate, modifyDate, !_IS_LOADED);
        this.modifyDate = modifyDate;
    }

    public AiModelConfig modifyDate(java.util.Date modifyDate) {
        setModifyDate(modifyDate);
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.toString(this);
    }
}