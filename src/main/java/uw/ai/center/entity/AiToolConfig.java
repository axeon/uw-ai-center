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
 * AiToolConfig实体类
 * AI工具配置
 *
 * @author axeon
 */
@TableMeta(tableName="ai_tool_config",tableType="table")
@Schema(title = "AI工具配置", description = "AI工具配置")
public class AiToolConfig implements DataEntity,Serializable{


    /**
     * ID
     */
    @ColumnMeta(columnName="id", dataType="long", dataSize=19, nullable=false, primaryKey=true)
    @Schema(title = "ID", description = "ID")
    private long id;

    /**
     * 应用名
     */
    @ColumnMeta(columnName="app_name", dataType="String", dataSize=200, nullable=false)
    @Schema(title = "应用名", description = "应用名")
    private String appName;

    /**
     * 工具代码
     */
    @ColumnMeta(columnName="tool_code", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "工具代码", description = "工具代码")
    private String toolCode;

    /**
     * 工具版本
     */
    @ColumnMeta(columnName="tool_version", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "工具版本", description = "工具版本")
    private String toolVersion;

    /**
     * 工具名称
     */
    @ColumnMeta(columnName="tool_name", dataType="String", dataSize=200, nullable=true)
    @Schema(title = "工具名称", description = "工具名称")
    private String toolName;

    /**
     * 工具描述
     */
    @ColumnMeta(columnName="tool_desc", dataType="String", dataSize=65535, nullable=true)
    @Schema(title = "工具描述", description = "工具描述")
    private String toolDesc;

    /**
     * 工具参数配置
     */
    @ColumnMeta(columnName="tool_param", dataType="String", dataSize=1073741824, nullable=true)
    @Schema(title = "工具参数配置", description = "工具参数配置")
    @JsonRawValue(value = false)
    private String toolParam;

    /**
     * 工具返回配置
     */
    @ColumnMeta(columnName="tool_return", dataType="String", dataSize=1073741824, nullable=true)
    @Schema(title = "工具返回配置", description = "工具返回配置")
    @JsonRawValue(value = false)
    private String toolReturn;

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
        this.UPDATED_INFO = new StringBuilder("表ai_tool_config主键\"" + 
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
     * 获取工具代码。
     */
    public String getToolCode(){
        return this.toolCode;
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
    public String getToolParam(){
        return this.toolParam;
    }

    /**
     * 获取工具返回配置。
     */
    public String getToolReturn(){
        return this.toolReturn;
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
     * 设置应用名。
     */
    public void setAppName(String appName){
        if (!Objects.equals(this.appName, appName)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("app_name");
            this.UPDATED_INFO.append("app_name:\"" + this.appName+ "\"=>\"" + appName + "\"\r\n");
            this.appName = appName;
        }
    }

    /**
     * 设置工具代码。
     */
    public void setToolCode(String toolCode){
        if (!Objects.equals(this.toolCode, toolCode)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("tool_code");
            this.UPDATED_INFO.append("tool_code:\"" + this.toolCode+ "\"=>\"" + toolCode + "\"\r\n");
            this.toolCode = toolCode;
        }
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
            this.UPDATED_INFO.append("tool_version:\"" + this.toolVersion+ "\"=>\"" + toolVersion + "\"\r\n");
            this.toolVersion = toolVersion;
        }
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
            this.UPDATED_INFO.append("tool_name:\"" + this.toolName+ "\"=>\"" + toolName + "\"\r\n");
            this.toolName = toolName;
        }
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
            this.UPDATED_INFO.append("tool_desc:\"" + this.toolDesc+ "\"=>\"" + toolDesc + "\"\r\n");
            this.toolDesc = toolDesc;
        }
    }

    /**
     * 设置工具参数配置。
     */
    public void setToolParam(String toolParam){
        if (!Objects.equals(this.toolParam, toolParam)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("tool_param");
            this.UPDATED_INFO.append("tool_param:\"" + this.toolParam+ "\"=>\"" + toolParam + "\"\r\n");
            this.toolParam = toolParam;
        }
    }

    /**
     * 设置工具返回配置。
     */
    public void setToolReturn(String toolReturn){
        if (!Objects.equals(this.toolReturn, toolReturn)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("tool_return");
            this.UPDATED_INFO.append("tool_return:\"" + this.toolReturn+ "\"=>\"" + toolReturn + "\"\r\n");
            this.toolReturn = toolReturn;
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
        sb.append("app_name:\"" + this.appName + "\"\r\n");
        sb.append("tool_code:\"" + this.toolCode + "\"\r\n");
        sb.append("tool_version:\"" + this.toolVersion + "\"\r\n");
        sb.append("tool_name:\"" + this.toolName + "\"\r\n");
        sb.append("tool_desc:\"" + this.toolDesc + "\"\r\n");
        sb.append("tool_param:\"" + this.toolParam + "\"\r\n");
        sb.append("tool_return:\"" + this.toolReturn + "\"\r\n");
        sb.append("create_date:\"" + this.createDate + "\"\r\n");
        sb.append("modify_date:\"" + this.modifyDate + "\"\r\n");
        sb.append("state:\"" + this.state + "\"\r\n");
        return sb.toString();
    }

}