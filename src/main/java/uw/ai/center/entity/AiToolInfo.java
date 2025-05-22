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
 * AiToolInfo实体类
 * AI工具信息
 *
 * @author axeon
 */
@TableMeta(tableName="ai_tool_info",tableType="table")
@Schema(title = "AI工具信息", description = "AI工具信息")
public class AiToolInfo implements DataEntity,Serializable{


    /**
     * ID
     */
    @ColumnMeta(columnName="id", dataType="long", dataSize=19, nullable=false, primaryKey=true)
    @Schema(title = "ID", description = "ID", maxLength=19, nullable=false )
    private long id;

    /**
     * 应用名
     */
    @ColumnMeta(columnName="app_name", dataType="String", dataSize=200, nullable=false)
    @Schema(title = "应用名", description = "应用名", maxLength=200, nullable=false )
    private String appName;

    /**
     * 工具类
     */
    @ColumnMeta(columnName="tool_class", dataType="String", dataSize=100, nullable=false)
    @Schema(title = "工具类", description = "工具类", maxLength=100, nullable=false )
    private String toolClass;

    /**
     * 工具版本
     */
    @ColumnMeta(columnName="tool_version", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "工具版本", description = "工具版本", maxLength=100, nullable=true )
    private String toolVersion;

    /**
     * 工具名称
     */
    @ColumnMeta(columnName="tool_name", dataType="String", dataSize=200, nullable=true)
    @Schema(title = "工具名称", description = "工具名称", maxLength=200, nullable=true )
    private String toolName;

    /**
     * 工具描述
     */
    @ColumnMeta(columnName="tool_desc", dataType="String", dataSize=65535, nullable=true)
    @Schema(title = "工具描述", description = "工具描述", maxLength=65535, nullable=true )
    private String toolDesc;

    /**
     * 工具参数配置
     */
    @ColumnMeta(columnName="tool_input", dataType="String", dataSize=1073741824, nullable=true)
    @Schema(title = "工具参数配置", description = "工具参数配置", maxLength=1073741824, nullable=true )
    @JsonRawValue(value = false)
    private String toolInput;

    /**
     * 工具返回配置
     */
    @ColumnMeta(columnName="tool_output", dataType="String", dataSize=1073741824, nullable=true)
    @Schema(title = "工具返回配置", description = "工具返回配置", maxLength=1073741824, nullable=true )
    @JsonRawValue(value = false)
    private String toolOutput;

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
        return "ai_tool_info";
    }

    /**
     * 获得实体的表注释。
     */
    @Override
    public String ENTITY_NAME(){
        return "AI工具信息";
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
     * 获取应用名。
     */
    public String getAppName(){
        return this.appName;
    }

    /**
     * 获取工具类。
     */
    public String getToolClass(){
        return this.toolClass;
    }

    /**
     * 获取工具版本。
     */
    public String getToolVersion(){
        return this.toolVersion;
    }

    /**
     * 获取工具名称。
     */
    public String getToolName(){
        return this.toolName;
    }

    /**
     * 获取工具描述。
     */
    public String getToolDesc(){
        return this.toolDesc;
    }

    /**
     * 获取工具参数配置。
     */
    public String getToolInput(){
        return this.toolInput;
    }

    /**
     * 获取工具返回配置。
     */
    public String getToolOutput(){
        return this.toolOutput;
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
    public AiToolInfo id(long id){
        setId(id);
        return this;
    }

    /**
     * 设置应用名。
     */
    public void setAppName(String appName){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "appName", this.appName, appName, !_IS_LOADED );
        this.appName = appName;
    }

    /**
     *  设置应用名链式调用。
     */
    public AiToolInfo appName(String appName){
        setAppName(appName);
        return this;
    }

    /**
     * 设置工具类。
     */
    public void setToolClass(String toolClass){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "toolClass", this.toolClass, toolClass, !_IS_LOADED );
        this.toolClass = toolClass;
    }

    /**
     *  设置工具类链式调用。
     */
    public AiToolInfo toolClass(String toolClass){
        setToolClass(toolClass);
        return this;
    }

    /**
     * 设置工具版本。
     */
    public void setToolVersion(String toolVersion){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "toolVersion", this.toolVersion, toolVersion, !_IS_LOADED );
        this.toolVersion = toolVersion;
    }

    /**
     *  设置工具版本链式调用。
     */
    public AiToolInfo toolVersion(String toolVersion){
        setToolVersion(toolVersion);
        return this;
    }

    /**
     * 设置工具名称。
     */
    public void setToolName(String toolName){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "toolName", this.toolName, toolName, !_IS_LOADED );
        this.toolName = toolName;
    }

    /**
     *  设置工具名称链式调用。
     */
    public AiToolInfo toolName(String toolName){
        setToolName(toolName);
        return this;
    }

    /**
     * 设置工具描述。
     */
    public void setToolDesc(String toolDesc){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "toolDesc", this.toolDesc, toolDesc, !_IS_LOADED );
        this.toolDesc = toolDesc;
    }

    /**
     *  设置工具描述链式调用。
     */
    public AiToolInfo toolDesc(String toolDesc){
        setToolDesc(toolDesc);
        return this;
    }

    /**
     * 设置工具参数配置。
     */
    public void setToolInput(String toolInput){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "toolInput", this.toolInput, toolInput, !_IS_LOADED );
        this.toolInput = toolInput;
    }

    /**
     *  设置工具参数配置链式调用。
     */
    public AiToolInfo toolInput(String toolInput){
        setToolInput(toolInput);
        return this;
    }

    /**
     * 设置工具返回配置。
     */
    public void setToolOutput(String toolOutput){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "toolOutput", this.toolOutput, toolOutput, !_IS_LOADED );
        this.toolOutput = toolOutput;
    }

    /**
     *  设置工具返回配置链式调用。
     */
    public AiToolInfo toolOutput(String toolOutput){
        setToolOutput(toolOutput);
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
    public AiToolInfo createDate(java.util.Date createDate){
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
    public AiToolInfo modifyDate(java.util.Date modifyDate){
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
    public AiToolInfo state(int state){
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