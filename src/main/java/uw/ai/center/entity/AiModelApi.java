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
 * AI模型API配置
 *
 * @author axeon
 */
@TableMeta(tableName="ai_model_api",tableType="table")
@Schema(title = "AI模型API配置", description = "AI模型API配置")
public class AiModelApi implements DataEntity,Serializable{


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
     * 配置代码
     */
    @ColumnMeta(columnName="api_code", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "配置代码", description = "配置代码", maxLength=100, nullable=true )
    private String apiCode;

    /**
     * 配置名称
     */
    @ColumnMeta(columnName="api_name", dataType="String", dataSize=200, nullable=true)
    @Schema(title = "配置名称", description = "配置名称", maxLength=200, nullable=true )
    private String apiName;

    /**
     * 配置描述
     */
    @ColumnMeta(columnName="api_desc", dataType="String", dataSize=65535, nullable=true)
    @Schema(title = "配置描述", description = "配置描述", maxLength=65535, nullable=true )
    private String apiDesc;

    /**
     * API地址
     */
    @ColumnMeta(columnName="api_url", dataType="String", dataSize=200, nullable=true)
    @Schema(title = "API地址", description = "API地址", maxLength=200, nullable=true )
    private String apiUrl;

    /**
     * API密钥
     */
    @ColumnMeta(columnName="api_key", dataType="String", dataSize=200, nullable=true)
    @Schema(title = "API密钥", description = "API密钥", maxLength=200, nullable=true )
    private String apiKey;

    /**
     * 状态
     */
    @ColumnMeta(columnName="state", dataType="int", dataSize=10, nullable=false)
    @Schema(title = "状态", description = "状态", maxLength=10, nullable=false )
    private int state;

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
        return "ai_model_api";
    }

    /**
     * 获得实体的表注释。
     */
    @Override
    public String ENTITY_NAME(){
        return "AI模型API配置";
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
     * 获取配置代码。
     */
    public String getApiCode(){
        return this.apiCode;
    }

    /**
     * 获取配置名称。
     */
    public String getApiName(){
        return this.apiName;
    }

    /**
     * 获取配置描述。
     */
    public String getApiDesc(){
        return this.apiDesc;
    }

    /**
     * 获取API地址。
     */
    public String getApiUrl(){
        return this.apiUrl;
    }

    /**
     * 获取API密钥。
     */
    public String getApiKey(){
        return this.apiKey;
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
     * 设置ID。
     */
    public void setId(long id){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "id", this.id, id, !_IS_LOADED );
        this.id = id;
    }

    /**
     *  设置ID链式调用。
     */
    public AiModelApi id(long id){
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
    public AiModelApi saasId(long saasId){
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
    public AiModelApi mchId(long mchId){
        setMchId(mchId);
        return this;
    }

    /**
     * 设置配置代码。
     */
    public void setApiCode(String apiCode){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "apiCode", this.apiCode, apiCode, !_IS_LOADED );
        this.apiCode = apiCode;
    }

    /**
     *  设置配置代码链式调用。
     */
    public AiModelApi apiCode(String apiCode){
        setApiCode(apiCode);
        return this;
    }

    /**
     * 设置配置名称。
     */
    public void setApiName(String apiName){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "apiName", this.apiName, apiName, !_IS_LOADED );
        this.apiName = apiName;
    }

    /**
     *  设置配置名称链式调用。
     */
    public AiModelApi apiName(String apiName){
        setApiName(apiName);
        return this;
    }

    /**
     * 设置配置描述。
     */
    public void setApiDesc(String apiDesc){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "apiDesc", this.apiDesc, apiDesc, !_IS_LOADED );
        this.apiDesc = apiDesc;
    }

    /**
     *  设置配置描述链式调用。
     */
    public AiModelApi apiDesc(String apiDesc){
        setApiDesc(apiDesc);
        return this;
    }

    /**
     * 设置API地址。
     */
    public void setApiUrl(String apiUrl){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "apiUrl", this.apiUrl, apiUrl, !_IS_LOADED );
        this.apiUrl = apiUrl;
    }

    /**
     *  设置API地址链式调用。
     */
    public AiModelApi apiUrl(String apiUrl){
        setApiUrl(apiUrl);
        return this;
    }

    /**
     * 设置API密钥。
     */
    public void setApiKey(String apiKey){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "apiKey", this.apiKey, apiKey, !_IS_LOADED );
        this.apiKey = apiKey;
    }

    /**
     *  设置API密钥链式调用。
     */
    public AiModelApi apiKey(String apiKey){
        setApiKey(apiKey);
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
    public AiModelApi state(int state){
        setState(state);
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
    public AiModelApi createDate(java.util.Date createDate){
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
    public AiModelApi modifyDate(java.util.Date modifyDate){
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