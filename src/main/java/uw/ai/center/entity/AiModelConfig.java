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
    @Schema(title = "ID", description = "ID")
    private long id;

    /**
     * SAAS ID
     */
    @ColumnMeta(columnName="saas_id", dataType="long", dataSize=19, nullable=false, primaryKey=true)
    @Schema(title = "SAAS ID", description = "SAAS ID")
    private long saasId;

    /**
     * 商户ID
     */
    @ColumnMeta(columnName="mch_id", dataType="long", dataSize=19, nullable=false)
    @Schema(title = "商户ID", description = "商户ID")
    private long mchId;

    /**
     * 服务商类
     */
    @ColumnMeta(columnName="vendor_class", dataType="String", dataSize=200, nullable=false)
    @Schema(title = "服务商类", description = "服务商类")
    private String vendorClass;

    /**
     * 服务商代码
     */
    @ColumnMeta(columnName="config_code", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "服务商代码", description = "服务商代码")
    private String configCode;

    /**
     * 服务商名称
     */
    @ColumnMeta(columnName="config_name", dataType="String", dataSize=200, nullable=true)
    @Schema(title = "服务商名称", description = "服务商名称")
    private String configName;

    /**
     * 服务商描述
     */
    @ColumnMeta(columnName="config_desc", dataType="String", dataSize=65535, nullable=true)
    @Schema(title = "服务商描述", description = "服务商描述")
    private String configDesc;

    /**
     * api地址
     */
    @ColumnMeta(columnName="api_url", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "api地址", description = "api地址")
    private String apiUrl;

    /**
     * api key
     */
    @ColumnMeta(columnName="api_key", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "api key", description = "api key")
    private String apiKey;

    /**
     * 主模型
     */
    @ColumnMeta(columnName="model_main", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "主模型", description = "主模型")
    private String modelMain;

    /**
     * 嵌入模型
     */
    @ColumnMeta(columnName="model_embed", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "嵌入模型", description = "嵌入模型")
    private String modelEmbed;

    /**
     * 服务商配置
     */
    @ColumnMeta(columnName="vendor_data", dataType="String", dataSize=1073741824, nullable=true)
    @Schema(title = "服务商配置", description = "服务商配置")
    @JsonRawValue(value = false)
    private String vendorData;

    /**
     * 模型配置
     */
    @ColumnMeta(columnName="model_data", dataType="String", dataSize=1073741824, nullable=true)
    @Schema(title = "模型配置", description = "模型配置")
    @JsonRawValue(value = false)
    private String modelData;

    /**
     * 嵌入配置
     */
    @ColumnMeta(columnName="embed_data", dataType="String", dataSize=1073741824, nullable=true)
    @Schema(title = "嵌入配置", description = "嵌入配置")
    @JsonRawValue(value = false)
    private String embedData;

    /**
     * 创建时间
     */
    @ColumnMeta(columnName="create_date", dataType="java.util.Date", dataSize=23, nullable=true)
    @Schema(title = "创建时间", description = "创建时间")
    private java.util.Date createDate;

    /**
     * 修改时间
     */
    @ColumnMeta(columnName="modify_date", dataType="java.util.Date", dataSize=23, nullable=true)
    @Schema(title = "修改时间", description = "修改时间")
    private java.util.Date modifyDate;

    /**
     * 状态
     */
    @ColumnMeta(columnName="state", dataType="int", dataSize=10, nullable=true)
    @Schema(title = "状态", description = "状态")
    private int state;

    /**
     * 轻量级状态下更新列表list.
     */
    private transient Set<String> UPDATED_COLUMN = null;

    /**
     * 更新的信息.
     */
    private transient StringBuilder UPDATED_INFO = null;

    /**
     * 获取更改的字段列表.
     */
    @Override
    public Set<String> GET_UPDATED_COLUMN() {
        return UPDATED_COLUMN;
    }

    /**
     * 获取文本更新信息.
     */
    @Override
    public String GET_UPDATED_INFO() {
        if (this.UPDATED_INFO == null) {
            return null;
        } else {
            return this.UPDATED_INFO.toString();
        }
    }

    /**
     * 清除更新信息.
     */
    @Override
    public void CLEAR_UPDATED_INFO() {
        UPDATED_COLUMN = null;
        UPDATED_INFO = null;
    }

    /**
     * 初始化set相关的信息.
     */
    private void _INIT_UPDATE_INFO() {
        this.UPDATED_COLUMN = new HashSet<String>();
        this.UPDATED_INFO = new StringBuilder("表ai_model_config主键\"" + 
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
        if (!Objects.equals(this.id, id)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("id");
            this.UPDATED_INFO.append("id:\"" + this.id+ "\"=>\"" + id + "\"\r\n");
            this.id = id;
        }
    }

    /**
     * 设置SAAS ID。
     */
    public void setSaasId(long saasId){
        if (!Objects.equals(this.saasId, saasId)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("saas_id");
            this.UPDATED_INFO.append("saas_id:\"" + this.saasId+ "\"=>\"" + saasId + "\"\r\n");
            this.saasId = saasId;
        }
    }

    /**
     * 设置商户ID。
     */
    public void setMchId(long mchId){
        if (!Objects.equals(this.mchId, mchId)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("mch_id");
            this.UPDATED_INFO.append("mch_id:\"" + this.mchId+ "\"=>\"" + mchId + "\"\r\n");
            this.mchId = mchId;
        }
    }

    /**
     * 设置服务商类。
     */
    public void setVendorClass(String vendorClass){
        if (!Objects.equals(this.vendorClass, vendorClass)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("vendor_class");
            this.UPDATED_INFO.append("vendor_class:\"" + this.vendorClass+ "\"=>\"" + vendorClass + "\"\r\n");
            this.vendorClass = vendorClass;
        }
    }

    /**
     * 设置服务商代码。
     */
    public void setConfigCode(String configCode){
        if (!Objects.equals(this.configCode, configCode)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("config_code");
            this.UPDATED_INFO.append("config_code:\"" + this.configCode+ "\"=>\"" + configCode + "\"\r\n");
            this.configCode = configCode;
        }
    }

    /**
     * 设置服务商名称。
     */
    public void setConfigName(String configName){
        if (!Objects.equals(this.configName, configName)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("config_name");
            this.UPDATED_INFO.append("config_name:\"" + this.configName+ "\"=>\"" + configName + "\"\r\n");
            this.configName = configName;
        }
    }

    /**
     * 设置服务商描述。
     */
    public void setConfigDesc(String configDesc){
        if (!Objects.equals(this.configDesc, configDesc)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("config_desc");
            this.UPDATED_INFO.append("config_desc:\"" + this.configDesc+ "\"=>\"" + configDesc + "\"\r\n");
            this.configDesc = configDesc;
        }
    }

    /**
     * 设置api地址。
     */
    public void setApiUrl(String apiUrl){
        if (!Objects.equals(this.apiUrl, apiUrl)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("api_url");
            this.UPDATED_INFO.append("api_url:\"" + this.apiUrl+ "\"=>\"" + apiUrl + "\"\r\n");
            this.apiUrl = apiUrl;
        }
    }

    /**
     * 设置api key。
     */
    public void setApiKey(String apiKey){
        if (!Objects.equals(this.apiKey, apiKey)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("api_key");
            this.UPDATED_INFO.append("api_key:\"" + this.apiKey+ "\"=>\"" + apiKey + "\"\r\n");
            this.apiKey = apiKey;
        }
    }

    /**
     * 设置主模型。
     */
    public void setModelMain(String modelMain){
        if (!Objects.equals(this.modelMain, modelMain)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("model_main");
            this.UPDATED_INFO.append("model_main:\"" + this.modelMain+ "\"=>\"" + modelMain + "\"\r\n");
            this.modelMain = modelMain;
        }
    }

    /**
     * 设置嵌入模型。
     */
    public void setModelEmbed(String modelEmbed){
        if (!Objects.equals(this.modelEmbed, modelEmbed)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("model_embed");
            this.UPDATED_INFO.append("model_embed:\"" + this.modelEmbed+ "\"=>\"" + modelEmbed + "\"\r\n");
            this.modelEmbed = modelEmbed;
        }
    }

    /**
     * 设置服务商配置。
     */
    public void setVendorData(String vendorData){
        if (!Objects.equals(this.vendorData, vendorData)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("vendor_data");
            this.UPDATED_INFO.append("vendor_data:\"" + this.vendorData+ "\"=>\"" + vendorData + "\"\r\n");
            this.vendorData = vendorData;
        }
    }

    /**
     * 设置模型配置。
     */
    public void setModelData(String modelData){
        if (!Objects.equals(this.modelData, modelData)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("model_data");
            this.UPDATED_INFO.append("model_data:\"" + this.modelData+ "\"=>\"" + modelData + "\"\r\n");
            this.modelData = modelData;
        }
    }

    /**
     * 设置嵌入配置。
     */
    public void setEmbedData(String embedData){
        if (!Objects.equals(this.embedData, embedData)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("embed_data");
            this.UPDATED_INFO.append("embed_data:\"" + this.embedData+ "\"=>\"" + embedData + "\"\r\n");
            this.embedData = embedData;
        }
    }

    /**
     * 设置创建时间。
     */
    public void setCreateDate(java.util.Date createDate){
        if (!Objects.equals(this.createDate, createDate)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("create_date");
            this.UPDATED_INFO.append("create_date:\"" + this.createDate+ "\"=>\"" + createDate + "\"\r\n");
            this.createDate = createDate;
        }
    }

    /**
     * 设置修改时间。
     */
    public void setModifyDate(java.util.Date modifyDate){
        if (!Objects.equals(this.modifyDate, modifyDate)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("modify_date");
            this.UPDATED_INFO.append("modify_date:\"" + this.modifyDate+ "\"=>\"" + modifyDate + "\"\r\n");
            this.modifyDate = modifyDate;
        }
    }

    /**
     * 设置状态。
     */
    public void setState(int state){
        if (!Objects.equals(this.state, state)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("state");
            this.UPDATED_INFO.append("state:\"" + this.state+ "\"=>\"" + state + "\"\r\n");
            this.state = state;
        }
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