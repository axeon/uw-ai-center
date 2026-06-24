package uw.ai.center.entity;

import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.util.JsonUtils;
import uw.dao.DataEntity;
import uw.dao.DataUpdateInfo;
import uw.dao.annotation.ColumnMeta;
import uw.dao.annotation.TableMeta;

import java.io.Serializable;
import java.util.Date;


/**
 * AiModelConfig实体类
 * AI模型配置
 *
 * @author axeon
 */
@TableMeta(tableName="ai_model_config",tableType="table")
@Schema(title = "AI模型配置", description = "AI模型配置")
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
     * API配置ID
     */
    @ColumnMeta(columnName="api_id", dataType="long", dataSize=19, nullable=false)
    @Schema(title = "API配置ID", description = "API配置ID", maxLength=19, nullable=false )
    private long apiId;

    /**
     * 供应商类
     */
    @ColumnMeta(columnName="vendor_class", dataType="String", dataSize=200, nullable=false)
    @Schema(title = "供应商类", description = "供应商类", maxLength=200, nullable=false )
    private String vendorClass;

    /**
     * 模型类型: CHAT/EMBEDDING/RERANK/TTS/OCR/IMAGE_GENERATION/AUDIO_TRANSCRIPTION
     */
    @ColumnMeta(columnName="model_type", dataType="String", dataSize=50, nullable=false)
    @Schema(title = "模型类型: CHAT/EMBEDDING/RERANK/TTS/OCR/IMAGE_GENERATION/AUDIO_TRANSCRIPTION", description = "模型类型: CHAT/EMBEDDING/RERANK/TTS/OCR/IMAGE_GENERATION/AUDIO_TRANSCRIPTION", maxLength=50, nullable=false )
    private String modelType;

    /**
     * 模型能力标签
     */
    @ColumnMeta(columnName="model_tag", dataType="String", dataSize=200, nullable=true)
    @Schema(title = "模型能力标签", description = "模型能力标签", maxLength=200, nullable=true )
    private String modelTag;

    /**
     * 配置代码
     */
    @ColumnMeta(columnName="config_code", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "配置代码", description = "配置代码", maxLength=100, nullable=true )
    private String configCode;

    /**
     * 配置名称
     */
    @ColumnMeta(columnName="config_name", dataType="String", dataSize=200, nullable=true)
    @Schema(title = "配置名称", description = "配置名称", maxLength=200, nullable=true )
    private String configName;

    /**
     * 配置描述
     */
    @ColumnMeta(columnName="config_desc", dataType="String", dataSize=65535, nullable=true)
    @Schema(title = "配置描述", description = "配置描述", maxLength=65535, nullable=true )
    private String configDesc;

    /**
     * 模型名
     */
    @ColumnMeta(columnName="model_name", dataType="String", dataSize=100, nullable=false)
    @Schema(title = "模型名", description = "模型名", maxLength=100, nullable=false )
    private String modelName;

    /**
     * 模型参数JSON(平铺: temperature/max_tokens等)
     */
    @ColumnMeta(columnName="model_data", dataType="String", dataSize=1073741824, nullable=true)
    @Schema(title = "模型参数JSON(平铺: temperature/max_tokens等)", description = "模型参数JSON(平铺: temperature/max_tokens等)", maxLength=1073741824, nullable=true )
    @JsonRawValue(value = false)
    private String modelData;

    /**
     * 状态
     */
    @ColumnMeta(columnName="state", dataType="int", dataSize=10, nullable=true)
    @Schema(title = "状态", description = "状态", maxLength=10, nullable=true )
    private int state;

    /**
     * 创建时间
     */
    @ColumnMeta(columnName="create_date", dataType="java.util.Date", dataSize=23, nullable=true)
    @Schema(title = "创建时间", description = "创建时间", maxLength=23, nullable=true )
    private Date createDate;

    /**
     * 修改时间
     */
    @ColumnMeta(columnName="modify_date", dataType="java.util.Date", dataSize=23, nullable=true)
    @Schema(title = "修改时间", description = "修改时间", maxLength=23, nullable=true )
    private java.util.Date modifyDate;

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
        return "AI模型配置";
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
     * 获取API配置ID。
     */
    public long getApiId(){
        return this.apiId;
    }

    /**
     * 获取供应商类。
     */
    public String getVendorClass(){
        return this.vendorClass;
    }

    /**
     * 获取模型类型: CHAT/EMBEDDING/RERANK/TTS/OCR/IMAGE_GENERATION/AUDIO_TRANSCRIPTION。
     */
    public String getModelType(){
        return this.modelType;
    }

    /**
     * 获取模型能力标签。
     */
    public String getModelTag(){
        return this.modelTag;
    }

    /**
     * 获取配置代码。
     */
    public String getConfigCode(){
        return this.configCode;
    }

    /**
     * 获取配置名称。
     */
    public String getConfigName(){
        return this.configName;
    }

    /**
     * 获取配置描述。
     */
    public String getConfigDesc(){
        return this.configDesc;
    }

    /**
     * 获取模型名。
     */
    public String getModelName(){
        return this.modelName;
    }

    /**
     * 获取模型参数JSON(平铺: temperature/max_tokens等)。
     */
    public String getModelData(){
        return this.modelData;
    }

    /**
     * 获取状态。
     */
    public int getState(){
        return this.state;
    }

    /**
     * 获取创建时间。
     */
    public Date getCreateDate(){
        return this.createDate;
    }

    /**
     * 获取修改时间。
     */
    public Date getModifyDate(){
        return this.modifyDate;
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
     * 设置API配置ID。
     */
    public void setApiId(long apiId){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "apiId", this.apiId, apiId, !_IS_LOADED );
        this.apiId = apiId;
    }

    /**
     *  设置API配置ID链式调用。
     */
    public AiModelConfig apiId(long apiId){
        setApiId(apiId);
        return this;
    }

    /**
     * 设置供应商类。
     */
    public void setVendorClass(String vendorClass){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "vendorClass", this.vendorClass, vendorClass, !_IS_LOADED );
        this.vendorClass = vendorClass;
    }

    /**
     *  设置供应商类链式调用。
     */
    public AiModelConfig vendorClass(String vendorClass){
        setVendorClass(vendorClass);
        return this;
    }

    /**
     * 设置模型类型: CHAT/EMBEDDING/RERANK/TTS/OCR/IMAGE_GENERATION/AUDIO_TRANSCRIPTION。
     */
    public void setModelType(String modelType){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "modelType", this.modelType, modelType, !_IS_LOADED );
        this.modelType = modelType;
    }

    /**
     *  设置模型类型: CHAT/EMBEDDING/RERANK/TTS/OCR/IMAGE_GENERATION/AUDIO_TRANSCRIPTION链式调用。
     */
    public AiModelConfig modelType(String modelType){
        setModelType(modelType);
        return this;
    }

    /**
     * 设置模型能力标签。
     */
    public void setModelTag(String modelTag){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "modelTag", this.modelTag, modelTag, !_IS_LOADED );
        this.modelTag = modelTag;
    }

    /**
     *  设置模型能力标签链式调用。
     */
    public AiModelConfig modelTag(String modelTag){
        setModelTag(modelTag);
        return this;
    }

    /**
     * 设置配置代码。
     */
    public void setConfigCode(String configCode){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "configCode", this.configCode, configCode, !_IS_LOADED );
        this.configCode = configCode;
    }

    /**
     *  设置配置代码链式调用。
     */
    public AiModelConfig configCode(String configCode){
        setConfigCode(configCode);
        return this;
    }

    /**
     * 设置配置名称。
     */
    public void setConfigName(String configName){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "configName", this.configName, configName, !_IS_LOADED );
        this.configName = configName;
    }

    /**
     *  设置配置名称链式调用。
     */
    public AiModelConfig configName(String configName){
        setConfigName(configName);
        return this;
    }

    /**
     * 设置配置描述。
     */
    public void setConfigDesc(String configDesc){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "configDesc", this.configDesc, configDesc, !_IS_LOADED );
        this.configDesc = configDesc;
    }

    /**
     *  设置配置描述链式调用。
     */
    public AiModelConfig configDesc(String configDesc){
        setConfigDesc(configDesc);
        return this;
    }

    /**
     * 设置模型名。
     */
    public void setModelName(String modelName){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "modelName", this.modelName, modelName, !_IS_LOADED );
        this.modelName = modelName;
    }

    /**
     *  设置模型名链式调用。
     */
    public AiModelConfig modelName(String modelName){
        setModelName(modelName);
        return this;
    }

    /**
     * 设置模型参数JSON(平铺: temperature/max_tokens等)。
     */
    public void setModelData(String modelData){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "modelData", this.modelData, modelData, !_IS_LOADED );
        this.modelData = modelData;
    }

    /**
     *  设置模型参数JSON(平铺: temperature/max_tokens等)链式调用。
     */
    public AiModelConfig modelData(String modelData){
        setModelData(modelData);
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
     * 设置创建时间。
     */
    public void setCreateDate(Date createDate){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "createDate", this.createDate, createDate, !_IS_LOADED );
        this.createDate = createDate;
    }

    /**
     *  设置创建时间链式调用。
     */
    public AiModelConfig createDate(Date createDate){
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
     * 重载toString方法.
     */
    @Override
    public String toString() {
        return JsonUtils.toString(this);
    }

}