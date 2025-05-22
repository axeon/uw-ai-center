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
 * AiRagLib实体类
 * rag文档库
 *
 * @author axeon
 */
@TableMeta(tableName="ai_rag_lib",tableType="table")
@Schema(title = "rag文档库", description = "rag文档库")
public class AiRagLib implements DataEntity,Serializable{


    /**
     * ID
     */
    @ColumnMeta(columnName="id", dataType="long", dataSize=19, nullable=false, primaryKey=true)
    @Schema(title = "ID", description = "ID", maxLength=19, nullable=false )
    private long id;

    /**
     * saasId
     */
    @ColumnMeta(columnName="saas_id", dataType="long", dataSize=19, nullable=false, primaryKey=true)
    @Schema(title = "saasId", description = "saasId", maxLength=19, nullable=false )
    private long saasId;

    /**
     * 文档库类型
     */
    @ColumnMeta(columnName="lib_type", dataType="int", dataSize=10, nullable=true)
    @Schema(title = "文档库类型", description = "文档库类型", maxLength=10, nullable=true )
    private int libType;

    /**
     * 文档库名称
     */
    @ColumnMeta(columnName="lib_name", dataType="String", dataSize=200, nullable=true)
    @Schema(title = "文档库名称", description = "文档库名称", maxLength=200, nullable=true )
    private String libName;

    /**
     * 文档库描述
     */
    @ColumnMeta(columnName="lib_desc", dataType="String", dataSize=65535, nullable=true)
    @Schema(title = "文档库描述", description = "文档库描述", maxLength=65535, nullable=true )
    private String libDesc;

    /**
     * embed配置ID
     */
    @ColumnMeta(columnName="embed_config_id", dataType="long", dataSize=19, nullable=true)
    @Schema(title = "embed配置ID", description = "embed配置ID", maxLength=19, nullable=true )
    private long embedConfigId;

    /**
     * embed模型名
     */
    @ColumnMeta(columnName="embed_model_name", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "embed模型名", description = "embed模型名", maxLength=100, nullable=true )
    private String embedModelName;

    /**
     * 文档库配置
     */
    @ColumnMeta(columnName="lib_config", dataType="String", dataSize=1073741824, nullable=true)
    @Schema(title = "文档库配置", description = "文档库配置", maxLength=1073741824, nullable=true )
    @JsonRawValue(value = false)
    private String libConfig;

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
        return "ai_rag_lib";
    }

    /**
     * 获得实体的表注释。
     */
    @Override
    public String ENTITY_NAME(){
        return "rag文档库";
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
     * 获取saasId。
     */
    public long getSaasId(){
        return this.saasId;
    }

    /**
     * 获取文档库类型。
     */
    public int getLibType(){
        return this.libType;
    }

    /**
     * 获取文档库名称。
     */
    public String getLibName(){
        return this.libName;
    }

    /**
     * 获取文档库描述。
     */
    public String getLibDesc(){
        return this.libDesc;
    }

    /**
     * 获取embed配置ID。
     */
    public long getEmbedConfigId(){
        return this.embedConfigId;
    }

    /**
     * 获取embed模型名。
     */
    public String getEmbedModelName(){
        return this.embedModelName;
    }

    /**
     * 获取文档库配置。
     */
    public String getLibConfig(){
        return this.libConfig;
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
    public AiRagLib id(long id){
        setId(id);
        return this;
    }

    /**
     * 设置saasId。
     */
    public void setSaasId(long saasId){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "saasId", this.saasId, saasId, !_IS_LOADED );
        this.saasId = saasId;
    }

    /**
     *  设置saasId链式调用。
     */
    public AiRagLib saasId(long saasId){
        setSaasId(saasId);
        return this;
    }

    /**
     * 设置文档库类型。
     */
    public void setLibType(int libType){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "libType", this.libType, libType, !_IS_LOADED );
        this.libType = libType;
    }

    /**
     *  设置文档库类型链式调用。
     */
    public AiRagLib libType(int libType){
        setLibType(libType);
        return this;
    }

    /**
     * 设置文档库名称。
     */
    public void setLibName(String libName){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "libName", this.libName, libName, !_IS_LOADED );
        this.libName = libName;
    }

    /**
     *  设置文档库名称链式调用。
     */
    public AiRagLib libName(String libName){
        setLibName(libName);
        return this;
    }

    /**
     * 设置文档库描述。
     */
    public void setLibDesc(String libDesc){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "libDesc", this.libDesc, libDesc, !_IS_LOADED );
        this.libDesc = libDesc;
    }

    /**
     *  设置文档库描述链式调用。
     */
    public AiRagLib libDesc(String libDesc){
        setLibDesc(libDesc);
        return this;
    }

    /**
     * 设置embed配置ID。
     */
    public void setEmbedConfigId(long embedConfigId){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "embedConfigId", this.embedConfigId, embedConfigId, !_IS_LOADED );
        this.embedConfigId = embedConfigId;
    }

    /**
     *  设置embed配置ID链式调用。
     */
    public AiRagLib embedConfigId(long embedConfigId){
        setEmbedConfigId(embedConfigId);
        return this;
    }

    /**
     * 设置embed模型名。
     */
    public void setEmbedModelName(String embedModelName){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "embedModelName", this.embedModelName, embedModelName, !_IS_LOADED );
        this.embedModelName = embedModelName;
    }

    /**
     *  设置embed模型名链式调用。
     */
    public AiRagLib embedModelName(String embedModelName){
        setEmbedModelName(embedModelName);
        return this;
    }

    /**
     * 设置文档库配置。
     */
    public void setLibConfig(String libConfig){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "libConfig", this.libConfig, libConfig, !_IS_LOADED );
        this.libConfig = libConfig;
    }

    /**
     *  设置文档库配置链式调用。
     */
    public AiRagLib libConfig(String libConfig){
        setLibConfig(libConfig);
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
    public AiRagLib createDate(java.util.Date createDate){
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
    public AiRagLib modifyDate(java.util.Date modifyDate){
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
    public AiRagLib state(int state){
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