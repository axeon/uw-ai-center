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
 * AiModelApi实体类
 * AI模型API连接配置
 *
 * @author axeon
 */
@TableMeta(tableName = "ai_model_api", tableType = "table")
@Schema(title = "AI模型API连接配置", description = "AI模型API连接配置")
public class AiModelApi implements DataEntity, Serializable {

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
     * 配置代码
     */
    @ColumnMeta(columnName = "api_code", dataType = "String", dataSize = 100, nullable = true)
    @Schema(title = "配置代码", description = "配置代码")
    private String apiCode;

    /**
     * 配置名称
     */
    @ColumnMeta(columnName = "api_name", dataType = "String", dataSize = 200, nullable = true)
    @Schema(title = "配置名称", description = "配置名称")
    private String apiName;

    /**
     * 配置描述
     */
    @ColumnMeta(columnName = "api_desc", dataType = "String", dataSize = 65535, nullable = true)
    @Schema(title = "配置描述", description = "配置描述")
    private String apiDesc;

    /**
     * API地址
     */
    @ColumnMeta(columnName = "api_url", dataType = "String", dataSize = 200, nullable = true)
    @Schema(title = "API地址", description = "API地址")
    private String apiUrl;

    /**
     * API密钥
     */
    @ColumnMeta(columnName = "api_key", dataType = "String", dataSize = 200, nullable = true)
    @Schema(title = "API密钥", description = "API密钥")
    private String apiKey;

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
        return "ai_model_api";
    }

    @Override
    public String ENTITY_NAME() {
        return "AI模型API连接配置";
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

    public AiModelApi id(long id) {
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

    public AiModelApi saasId(long saasId) {
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

    public AiModelApi mchId(long mchId) {
        setMchId(mchId);
        return this;
    }

    public String getApiCode() {
        return this.apiCode;
    }

    public void setApiCode(String apiCode) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "apiCode", this.apiCode, apiCode, !_IS_LOADED);
        this.apiCode = apiCode;
    }

    public AiModelApi apiCode(String apiCode) {
        setApiCode(apiCode);
        return this;
    }

    public String getApiName() {
        return this.apiName;
    }

    public void setApiName(String apiName) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "apiName", this.apiName, apiName, !_IS_LOADED);
        this.apiName = apiName;
    }

    public AiModelApi apiName(String apiName) {
        setApiName(apiName);
        return this;
    }

    public String getApiDesc() {
        return this.apiDesc;
    }

    public void setApiDesc(String apiDesc) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "apiDesc", this.apiDesc, apiDesc, !_IS_LOADED);
        this.apiDesc = apiDesc;
    }

    public AiModelApi apiDesc(String apiDesc) {
        setApiDesc(apiDesc);
        return this;
    }

    public String getApiUrl() {
        return this.apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "apiUrl", this.apiUrl, apiUrl, !_IS_LOADED);
        this.apiUrl = apiUrl;
    }

    public AiModelApi apiUrl(String apiUrl) {
        setApiUrl(apiUrl);
        return this;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "apiKey", this.apiKey, apiKey, !_IS_LOADED);
        this.apiKey = apiKey;
    }

    public AiModelApi apiKey(String apiKey) {
        setApiKey(apiKey);
        return this;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "state", this.state, state, !_IS_LOADED);
        this.state = state;
    }

    public AiModelApi state(int state) {
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

    public AiModelApi createDate(java.util.Date createDate) {
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

    public AiModelApi modifyDate(java.util.Date modifyDate) {
        setModifyDate(modifyDate);
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.toString(this);
    }
}