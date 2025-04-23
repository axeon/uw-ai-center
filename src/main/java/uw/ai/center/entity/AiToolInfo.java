package uw.ai.center.entity;

import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.dao.DataEntity;
import uw.dao.annotation.ColumnMeta;
import uw.dao.annotation.TableMeta;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
     * 轻量级状态下更新列表list.
     */
    private transient Set<String> UPDATED_COLUMN = null;

    /**
     * 更新的信息.
     */
    private transient StringBuilder UPDATED_INFO = null;


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
        this.UPDATED_INFO = new StringBuilder("表ai_tool_info主键\"" + 
        this.id+ "\"更新为:\r\n");
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
        if (!Objects.equals(this.id, id)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("id");
            this.UPDATED_INFO.append("id:\"").append(this.id).append("\"=>\"").append(id).append("\"\n");
            this.id = id;
        }
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
        if (!Objects.equals(this.appName, appName)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("app_name");
            this.UPDATED_INFO.append("app_name:\"").append(this.appName).append("\"=>\"").append(appName).append("\"\n");
            this.appName = appName;
        }
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
        if (!Objects.equals(this.toolClass, toolClass)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("tool_class");
            this.UPDATED_INFO.append("tool_class:\"").append(this.toolClass).append("\"=>\"").append(toolClass).append("\"\n");
            this.toolClass = toolClass;
        }
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
        if (!Objects.equals(this.toolVersion, toolVersion)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("tool_version");
            this.UPDATED_INFO.append("tool_version:\"").append(this.toolVersion).append("\"=>\"").append(toolVersion).append("\"\n");
            this.toolVersion = toolVersion;
        }
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
        if (!Objects.equals(this.toolName, toolName)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("tool_name");
            this.UPDATED_INFO.append("tool_name:\"").append(this.toolName).append("\"=>\"").append(toolName).append("\"\n");
            this.toolName = toolName;
        }
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
        if (!Objects.equals(this.toolDesc, toolDesc)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("tool_desc");
            this.UPDATED_INFO.append("tool_desc:\"").append(this.toolDesc).append("\"=>\"").append(toolDesc).append("\"\n");
            this.toolDesc = toolDesc;
        }
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
        if (!Objects.equals(this.toolInput, toolInput)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("tool_input");
            this.UPDATED_INFO.append("tool_input:\"").append(this.toolInput).append("\"=>\"").append(toolInput).append("\"\n");
            this.toolInput = toolInput;
        }
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
        if (!Objects.equals(this.toolOutput, toolOutput)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("tool_output");
            this.UPDATED_INFO.append("tool_output:\"").append(this.toolOutput).append("\"=>\"").append(toolOutput).append("\"\n");
            this.toolOutput = toolOutput;
        }
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
        if (!Objects.equals(this.createDate, createDate)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("create_date");
            this.UPDATED_INFO.append("create_date:\"").append(this.createDate).append("\"=>\"").append(createDate).append("\"\n");
            this.createDate = createDate;
        }
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
        if (!Objects.equals(this.modifyDate, modifyDate)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("modify_date");
            this.UPDATED_INFO.append("modify_date:\"").append(this.modifyDate).append("\"=>\"").append(modifyDate).append("\"\n");
            this.modifyDate = modifyDate;
        }
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
        if (!Objects.equals(this.state, state)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("state");
            this.UPDATED_INFO.append("state:\"").append(this.state).append("\"=>\"").append(state).append("\"\n");
            this.state = state;
        }
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
        StringBuilder sb = new StringBuilder();
        sb.append("id:\"" + this.id + "\"\r\n");
        sb.append("app_name:\"" + this.appName + "\"\r\n");
        sb.append("tool_class:\"" + this.toolClass + "\"\r\n");
        sb.append("tool_version:\"" + this.toolVersion + "\"\r\n");
        sb.append("tool_name:\"" + this.toolName + "\"\r\n");
        sb.append("tool_desc:\"" + this.toolDesc + "\"\r\n");
        sb.append("tool_input:\"" + this.toolInput + "\"\r\n");
        sb.append("tool_output:\"" + this.toolOutput + "\"\r\n");
        sb.append("create_date:\"" + this.createDate + "\"\r\n");
        sb.append("modify_date:\"" + this.modifyDate + "\"\r\n");
        sb.append("state:\"" + this.state + "\"\r\n");
        return sb.toString();
    }

}