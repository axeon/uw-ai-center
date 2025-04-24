package uw.ai.center.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.dao.DataEntity;
import uw.dao.annotation.ColumnMeta;
import uw.dao.annotation.TableMeta;

/**
 * AiModelConfig实体类
 * AI服务模型
 *
 * @author axeon
 */
@TableMeta(tableName="ai_model_config",tableType="table")
@Schema(title = "AI服务模型", description = "AI服务模型")
public class AiModelConfig implements DataEntity,Serializable{


    /**
     * ID
     */
    @ColumnMeta(columnName="id", dataType="long", dataSize=19, nullable=false, primaryKey=true)
    @Schema(title = "ID", description = "ID", maxLength=19, nullable=false )
    private long id;

    /**
     * SAAS ID
     */
    @ColumnMeta(columnName="saas_id", dataType="long", dataSize=19, nullable=false, primaryKey=true)
    @Schema(title = "SAAS ID", description = "SAAS ID", maxLength=19, nullable=false )
    private long saasId;

    /**
     * 商户ID
     */
    @ColumnMeta(columnName="mch_id", dataType="long", dataSize=19, nullable=false)
    @Schema(title = "商户ID", description = "商户ID", maxLength=19, nullable=false )
    private long mchId;

    /**
     * 服务商类
     */
    @ColumnMeta(columnName="vendor_class", dataType="String", dataSize=200, nullable=false)
    @Schema(title = "服务商类", description = "服务商类", maxLength=200, nullable=false )
    private String vendorClass;

    /**
     * 服务商代码
     */
    @ColumnMeta(columnName="config_code", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "服务商代码", description = "服务商代码", maxLength=100, nullable=true )
    private String configCode;

    /**
     * 服务商名称
     */
    @ColumnMeta(columnName="config_name", dataType="String", dataSize=200, nullable=true)
    @Schema(title = "服务商名称", description = "服务商名称", maxLength=200, nullable=true )
    private String configName;

    /**
     * 服务商描述
     */
    @ColumnMeta(columnName="config_desc", dataType="String", dataSize=65535, nullable=true)
    @Schema(title = "服务商描述", description = "服务商描述", maxLength=65535, nullable=true )
    private String configDesc;

    /**
     * api地址
     */
    @ColumnMeta(columnName="api_url", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "api地址", description = "api地址", maxLength=100, nullable=true )
    private String apiUrl;

    /**
     * api key
     */
    @ColumnMeta(columnName="api_key", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "api key", description = "api key", maxLength=100, nullable=true )
    private String apiKey;

    /**
     * 主模型
     */
    @ColumnMeta(columnName="model_main", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "主模型", description = "主模型", maxLength=100, nullable=true )
    private String modelMain;

    /**
     * 嵌入模型
     */
    @ColumnMeta(columnName="model_embed", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "嵌入模型", description = "嵌入模型", maxLength=100, nullable=true )
    private String modelEmbed;

    /**
     * 服务商配置
     */
    @ColumnMeta(columnName="vendor_data", dataType="String", dataSize=1073741824, nullable=true)
    @Schema(title = "服务商配置", description = "服务商配置", maxLength=1073741824, nullable=true )
    @JsonRawValue(value = false)
    private String vendorData;

    /**
     * 模型配置
     */
    @ColumnMeta(columnName="model_data", dataType="String", dataSize=1073741824, nullable=true)
    @Schema(title = "模型配置", description = "模型配置", maxLength=1073741824, nullable=true )
    @JsonRawValue(value = false)
    private String modelData;

    /**
     * 嵌入配置
     */
    @ColumnMeta(columnName="embed_data", dataType="String", dataSize=1073741824, nullable=true)
    @Schema(title = "嵌入配置", description = "嵌入配置", maxLength=1073741824, nullable=true )
    @JsonRawValue(value = false)
    private String embedData;

    /**
     * 创建时间
     */
    @ColumnMeta(columnName="create_date", dataType="java.util.Date", dataSize=23, nullable=true)
    @Schema(title = "创建时间", description = "创建时间", maxLength=23, nullable=true )
    private java.util.Date createDate;

    /**
     * 修改时间
     */
    @ColumnMeta(columnName="modify_date", dataType="java.util.Date", dataSize=23, nullable=true)
    @Schema(title = "修改时间", description = "修改时间", maxLength=23, nullable=true )
    private java.util.Date modifyDate;

    /**
     * 状态
     */
    @ColumnMeta(columnName="state", dataType="int", dataSize=10, nullable=true)
    @Schema(title = "状态", description = "状态", maxLength=10, nullable=true )
    private int state;

    /**
     * 轻量级状态下更新列表list.
     */
    private transient Set<String> _UPDATED_COLUMN = null;

    /**
     * 更新的信息.
     */
    private transient StringBuilder _UPDATED_INFO = null;


    /**
     * 是否加载完成.
     */
    private transient boolean _IS_LOADED;


    /**
     * 获得实体的表名。
     */
    @Override
    public String ENTITY_TABLE(){
        return "ai_model_config";
    }

    /**
     * 获得实体的表注释。
     */
    @Override
    public String ENTITY_NAME(){
        return "AI服务模型";
    }

    /**
     * 获得主键
     */
    @Override
    public Serializable ENTITY_ID(){
        return getId();
    }


    /**
     * 获取更改的字段列表.
     */
    @Override
    public Set<String> GET_UPDATED_COLUMN() {
        return _UPDATED_COLUMN;
    }

    /**
     * 获取文本更新信息.
     */
    @Override
    public String GET_UPDATED_INFO() {
        if (this._UPDATED_INFO == null) {
            return null;
        } else {
            return this._UPDATED_INFO.toString();
        }
    }

    /**
     * 清除更新信息.
     */
    @Override
    public void CLEAR_UPDATED_INFO() {
        _UPDATED_COLUMN = null;
        _UPDATED_INFO = null;
    }

    /**
     * 初始化set相关的信息.
     */
    private void _INIT_UPDATE_INFO() {
        this._UPDATED_COLUMN = new HashSet<String>();
        this._UPDATED_INFO = new StringBuilder("表ai_model_config主键\"" + 
        this.id+ "\"更新为:\r\n");
    }


    /**
     * 获取ID。
     */
    public long getId(){
        return this.id;
    }

    /**
     * 获取SAAS ID。
     */
    public long getSaasId(){
        return this.saasId;
    }

    /**
     * 获取商户ID。
     */
    public long getMchId(){
        return this.mchId;
    }

    /**
     * 获取服务商类。
     */
    public String getVendorClass(){
        return this.vendorClass;
    }

    /**
     * 获取服务商代码。
     */
    public String getConfigCode(){
        return this.configCode;
    }

    /**
     * 获取服务商名称。
     */
    public String getConfigName(){
        return this.configName;
    }

    /**
     * 获取服务商描述。
     */
    public String getConfigDesc(){
        return this.configDesc;
    }

    /**
     * 获取api地址。
     */
    public String getApiUrl(){
        return this.apiUrl;
    }

    /**
     * 获取api key。
     */
    public String getApiKey(){
        return this.apiKey;
    }

    /**
     * 获取主模型。
     */
    public String getModelMain(){
        return this.modelMain;
    }

    /**
     * 获取嵌入模型。
     */
    public String getModelEmbed(){
        return this.modelEmbed;
    }

    /**
     * 获取服务商配置。
     */
    public String getVendorData(){
        return this.vendorData;
    }

    /**
     * 获取模型配置。
     */
    public String getModelData(){
        return this.modelData;
    }

    /**
     * 获取嵌入配置。
     */
    public String getEmbedData(){
        return this.embedData;
    }

    /**
     * 获取创建时间。
     */
    public java.util.Date getCreateDate(){
        return this.createDate;
    }

    /**
     * 获取修改时间。
     */
    public java.util.Date getModifyDate(){
        return this.modifyDate;
    }

    /**
     * 获取状态。
     */
    public int getState(){
        return this.state;
    }


    /**
     * 设置ID。
     */
    public void setId(long id){
        if (!_IS_LOADED||!Objects.equals(this.id, id)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("id");
            this._UPDATED_INFO.append("id:\"").append(this.id).append("\"=>\"").append(id).append("\"\n");
            this.id = id;
        }
    }

    /**
     *  设置ID链式调用。
     */
    public AiModelConfig id(long id){
        setId(id);
        return this;
        }

    /**
     * 设置SAAS ID。
     */
    public void setSaasId(long saasId){
        if (!_IS_LOADED||!Objects.equals(this.saasId, saasId)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("saas_id");
            this._UPDATED_INFO.append("saas_id:\"").append(this.saasId).append("\"=>\"").append(saasId).append("\"\n");
            this.saasId = saasId;
        }
    }

    /**
     *  设置SAAS ID链式调用。
     */
    public AiModelConfig saasId(long saasId){
        setSaasId(saasId);
        return this;
        }

    /**
     * 设置商户ID。
     */
    public void setMchId(long mchId){
        if (!_IS_LOADED||!Objects.equals(this.mchId, mchId)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("mch_id");
            this._UPDATED_INFO.append("mch_id:\"").append(this.mchId).append("\"=>\"").append(mchId).append("\"\n");
            this.mchId = mchId;
        }
    }

    /**
     *  设置商户ID链式调用。
     */
    public AiModelConfig mchId(long mchId){
        setMchId(mchId);
        return this;
        }

    /**
     * 设置服务商类。
     */
    public void setVendorClass(String vendorClass){
        if (!_IS_LOADED||!Objects.equals(this.vendorClass, vendorClass)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("vendor_class");
            this._UPDATED_INFO.append("vendor_class:\"").append(this.vendorClass).append("\"=>\"").append(vendorClass).append("\"\n");
            this.vendorClass = vendorClass;
        }
    }

    /**
     *  设置服务商类链式调用。
     */
    public AiModelConfig vendorClass(String vendorClass){
        setVendorClass(vendorClass);
        return this;
        }

    /**
     * 设置服务商代码。
     */
    public void setConfigCode(String configCode){
        if (!_IS_LOADED||!Objects.equals(this.configCode, configCode)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("config_code");
            this._UPDATED_INFO.append("config_code:\"").append(this.configCode).append("\"=>\"").append(configCode).append("\"\n");
            this.configCode = configCode;
        }
    }

    /**
     *  设置服务商代码链式调用。
     */
    public AiModelConfig configCode(String configCode){
        setConfigCode(configCode);
        return this;
        }

    /**
     * 设置服务商名称。
     */
    public void setConfigName(String configName){
        if (!_IS_LOADED||!Objects.equals(this.configName, configName)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("config_name");
            this._UPDATED_INFO.append("config_name:\"").append(this.configName).append("\"=>\"").append(configName).append("\"\n");
            this.configName = configName;
        }
    }

    /**
     *  设置服务商名称链式调用。
     */
    public AiModelConfig configName(String configName){
        setConfigName(configName);
        return this;
        }

    /**
     * 设置服务商描述。
     */
    public void setConfigDesc(String configDesc){
        if (!_IS_LOADED||!Objects.equals(this.configDesc, configDesc)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("config_desc");
            this._UPDATED_INFO.append("config_desc:\"").append(this.configDesc).append("\"=>\"").append(configDesc).append("\"\n");
            this.configDesc = configDesc;
        }
    }

    /**
     *  设置服务商描述链式调用。
     */
    public AiModelConfig configDesc(String configDesc){
        setConfigDesc(configDesc);
        return this;
        }

    /**
     * 设置api地址。
     */
    public void setApiUrl(String apiUrl){
        if (!_IS_LOADED||!Objects.equals(this.apiUrl, apiUrl)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("api_url");
            this._UPDATED_INFO.append("api_url:\"").append(this.apiUrl).append("\"=>\"").append(apiUrl).append("\"\n");
            this.apiUrl = apiUrl;
        }
    }

    /**
     *  设置api地址链式调用。
     */
    public AiModelConfig apiUrl(String apiUrl){
        setApiUrl(apiUrl);
        return this;
        }

    /**
     * 设置api key。
     */
    public void setApiKey(String apiKey){
        if (!_IS_LOADED||!Objects.equals(this.apiKey, apiKey)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("api_key");
            this._UPDATED_INFO.append("api_key:\"").append(this.apiKey).append("\"=>\"").append(apiKey).append("\"\n");
            this.apiKey = apiKey;
        }
    }

    /**
     *  设置api key链式调用。
     */
    public AiModelConfig apiKey(String apiKey){
        setApiKey(apiKey);
        return this;
        }

    /**
     * 设置主模型。
     */
    public void setModelMain(String modelMain){
        if (!_IS_LOADED||!Objects.equals(this.modelMain, modelMain)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("model_main");
            this._UPDATED_INFO.append("model_main:\"").append(this.modelMain).append("\"=>\"").append(modelMain).append("\"\n");
            this.modelMain = modelMain;
        }
    }

    /**
     *  设置主模型链式调用。
     */
    public AiModelConfig modelMain(String modelMain){
        setModelMain(modelMain);
        return this;
        }

    /**
     * 设置嵌入模型。
     */
    public void setModelEmbed(String modelEmbed){
        if (!_IS_LOADED||!Objects.equals(this.modelEmbed, modelEmbed)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("model_embed");
            this._UPDATED_INFO.append("model_embed:\"").append(this.modelEmbed).append("\"=>\"").append(modelEmbed).append("\"\n");
            this.modelEmbed = modelEmbed;
        }
    }

    /**
     *  设置嵌入模型链式调用。
     */
    public AiModelConfig modelEmbed(String modelEmbed){
        setModelEmbed(modelEmbed);
        return this;
        }

    /**
     * 设置服务商配置。
     */
    public void setVendorData(String vendorData){
        if (!_IS_LOADED||!Objects.equals(this.vendorData, vendorData)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("vendor_data");
            this._UPDATED_INFO.append("vendor_data:\"").append(this.vendorData).append("\"=>\"").append(vendorData).append("\"\n");
            this.vendorData = vendorData;
        }
    }

    /**
     *  设置服务商配置链式调用。
     */
    public AiModelConfig vendorData(String vendorData){
        setVendorData(vendorData);
        return this;
        }

    /**
     * 设置模型配置。
     */
    public void setModelData(String modelData){
        if (!_IS_LOADED||!Objects.equals(this.modelData, modelData)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("model_data");
            this._UPDATED_INFO.append("model_data:\"").append(this.modelData).append("\"=>\"").append(modelData).append("\"\n");
            this.modelData = modelData;
        }
    }

    /**
     *  设置模型配置链式调用。
     */
    public AiModelConfig modelData(String modelData){
        setModelData(modelData);
        return this;
        }

    /**
     * 设置嵌入配置。
     */
    public void setEmbedData(String embedData){
        if (!_IS_LOADED||!Objects.equals(this.embedData, embedData)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("embed_data");
            this._UPDATED_INFO.append("embed_data:\"").append(this.embedData).append("\"=>\"").append(embedData).append("\"\n");
            this.embedData = embedData;
        }
    }

    /**
     *  设置嵌入配置链式调用。
     */
    public AiModelConfig embedData(String embedData){
        setEmbedData(embedData);
        return this;
        }

    /**
     * 设置创建时间。
     */
    public void setCreateDate(java.util.Date createDate){
        if (!_IS_LOADED||!Objects.equals(this.createDate, createDate)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("create_date");
            this._UPDATED_INFO.append("create_date:\"").append(this.createDate).append("\"=>\"").append(createDate).append("\"\n");
            this.createDate = createDate;
        }
    }

    /**
     *  设置创建时间链式调用。
     */
    public AiModelConfig createDate(java.util.Date createDate){
        setCreateDate(createDate);
        return this;
        }

    /**
     * 设置修改时间。
     */
    public void setModifyDate(java.util.Date modifyDate){
        if (!_IS_LOADED||!Objects.equals(this.modifyDate, modifyDate)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("modify_date");
            this._UPDATED_INFO.append("modify_date:\"").append(this.modifyDate).append("\"=>\"").append(modifyDate).append("\"\n");
            this.modifyDate = modifyDate;
        }
    }

    /**
     *  设置修改时间链式调用。
     */
    public AiModelConfig modifyDate(java.util.Date modifyDate){
        setModifyDate(modifyDate);
        return this;
        }

    /**
     * 设置状态。
     */
    public void setState(int state){
        if (!_IS_LOADED||!Objects.equals(this.state, state)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("state");
            this._UPDATED_INFO.append("state:\"").append(this.state).append("\"=>\"").append(state).append("\"\n");
            this.state = state;
        }
    }

    /**
     *  设置状态链式调用。
     */
    public AiModelConfig state(int state){
        setState(state);
        return this;
        }

    /**
     * 重载toString方法.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id:\"" + this.id + "\"\r\n");
        sb.append("saas_id:\"" + this.saasId + "\"\r\n");
        sb.append("mch_id:\"" + this.mchId + "\"\r\n");
        sb.append("vendor_class:\"" + this.vendorClass + "\"\r\n");
        sb.append("config_code:\"" + this.configCode + "\"\r\n");
        sb.append("config_name:\"" + this.configName + "\"\r\n");
        sb.append("config_desc:\"" + this.configDesc + "\"\r\n");
        sb.append("api_url:\"" + this.apiUrl + "\"\r\n");
        sb.append("api_key:\"" + this.apiKey + "\"\r\n");
        sb.append("model_main:\"" + this.modelMain + "\"\r\n");
        sb.append("model_embed:\"" + this.modelEmbed + "\"\r\n");
        sb.append("vendor_data:\"" + this.vendorData + "\"\r\n");
        sb.append("model_data:\"" + this.modelData + "\"\r\n");
        sb.append("embed_data:\"" + this.embedData + "\"\r\n");
        sb.append("create_date:\"" + this.createDate + "\"\r\n");
        sb.append("modify_date:\"" + this.modifyDate + "\"\r\n");
        sb.append("state:\"" + this.state + "\"\r\n");
        return sb.toString();
    }

}