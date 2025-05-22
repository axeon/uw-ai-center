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
     * 数据更新信息.
     */
    private transient DataUpdateInfo _UPDATED_INFO = null;

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
     * 获取更新信息.
     */
    @Override
    public DataUpdateInfo GET_UPDATED_INFO() {
        return this._UPDATED_INFO;
    }

    /**
     * 清除更新信息.
     */
    @Override
    public void CLEAR_UPDATED_INFO() {
        _UPDATED_INFO = null;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "id", this.id, id, !_IS_LOADED );
        this.id = id;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "saasId", this.saasId, saasId, !_IS_LOADED );
        this.saasId = saasId;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "mchId", this.mchId, mchId, !_IS_LOADED );
        this.mchId = mchId;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "vendorClass", this.vendorClass, vendorClass, !_IS_LOADED );
        this.vendorClass = vendorClass;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "configCode", this.configCode, configCode, !_IS_LOADED );
        this.configCode = configCode;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "configName", this.configName, configName, !_IS_LOADED );
        this.configName = configName;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "configDesc", this.configDesc, configDesc, !_IS_LOADED );
        this.configDesc = configDesc;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "apiUrl", this.apiUrl, apiUrl, !_IS_LOADED );
        this.apiUrl = apiUrl;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "apiKey", this.apiKey, apiKey, !_IS_LOADED );
        this.apiKey = apiKey;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "modelMain", this.modelMain, modelMain, !_IS_LOADED );
        this.modelMain = modelMain;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "modelEmbed", this.modelEmbed, modelEmbed, !_IS_LOADED );
        this.modelEmbed = modelEmbed;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "vendorData", this.vendorData, vendorData, !_IS_LOADED );
        this.vendorData = vendorData;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "modelData", this.modelData, modelData, !_IS_LOADED );
        this.modelData = modelData;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "embedData", this.embedData, embedData, !_IS_LOADED );
        this.embedData = embedData;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "createDate", this.createDate, createDate, !_IS_LOADED );
        this.createDate = createDate;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "modifyDate", this.modifyDate, modifyDate, !_IS_LOADED );
        this.modifyDate = modifyDate;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "state", this.state, state, !_IS_LOADED );
        this.state = state;
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
        return JsonUtils.toString(this);
    }

}