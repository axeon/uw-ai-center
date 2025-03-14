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
     * 用户id
     */
    @ColumnMeta(columnName="user_id", dataType="long", dataSize=19, nullable=false)
    @Schema(title = "用户id", description = "用户id", maxLength=19, nullable=false )
    private long userId;

    /**
     * 用户类型
     */
    @ColumnMeta(columnName="user_type", dataType="int", dataSize=10, nullable=true)
    @Schema(title = "用户类型", description = "用户类型", maxLength=10, nullable=true )
    private int userType;

    /**
     * 用户名
     */
    @ColumnMeta(columnName="user_info", dataType="String", dataSize=200, nullable=true)
    @Schema(title = "用户名", description = "用户名", maxLength=200, nullable=true )
    private String userInfo;

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
     * 服务商配置
     */
    @ColumnMeta(columnName="lib_config", dataType="String", dataSize=1073741824, nullable=true)
    @Schema(title = "服务商配置", description = "服务商配置", maxLength=1073741824, nullable=true )
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
        this.UPDATED_INFO = new StringBuilder("表ai_rag_lib主键\"" + 
        this.id+ "\"更新为:\r\n");
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
     * 获取用户id。
     */
    public long getUserId(){
        return this.userId;
    }

    /**
     * 获取用户类型。
     */
    public int getUserType(){
        return this.userType;
    }

    /**
     * 获取用户名。
     */
    public String getUserInfo(){
        return this.userInfo;
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
     * 获取服务商配置。
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
     * 设置saasId。
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
     * 设置用户id。
     */
    public void setUserId(long userId){
        if (!Objects.equals(this.userId, userId)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("user_id");
            this.UPDATED_INFO.append("user_id:\"" + this.userId+ "\"=>\"" + userId + "\"\r\n");
            this.userId = userId;
        }
    }

    /**
     * 设置用户类型。
     */
    public void setUserType(int userType){
        if (!Objects.equals(this.userType, userType)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("user_type");
            this.UPDATED_INFO.append("user_type:\"" + this.userType+ "\"=>\"" + userType + "\"\r\n");
            this.userType = userType;
        }
    }

    /**
     * 设置用户名。
     */
    public void setUserInfo(String userInfo){
        if (!Objects.equals(this.userInfo, userInfo)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("user_info");
            this.UPDATED_INFO.append("user_info:\"" + this.userInfo+ "\"=>\"" + userInfo + "\"\r\n");
            this.userInfo = userInfo;
        }
    }

    /**
     * 设置文档库类型。
     */
    public void setLibType(int libType){
        if (!Objects.equals(this.libType, libType)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("lib_type");
            this.UPDATED_INFO.append("lib_type:\"" + this.libType+ "\"=>\"" + libType + "\"\r\n");
            this.libType = libType;
        }
    }

    /**
     * 设置文档库名称。
     */
    public void setLibName(String libName){
        if (!Objects.equals(this.libName, libName)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("lib_name");
            this.UPDATED_INFO.append("lib_name:\"" + this.libName+ "\"=>\"" + libName + "\"\r\n");
            this.libName = libName;
        }
    }

    /**
     * 设置文档库描述。
     */
    public void setLibDesc(String libDesc){
        if (!Objects.equals(this.libDesc, libDesc)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("lib_desc");
            this.UPDATED_INFO.append("lib_desc:\"" + this.libDesc+ "\"=>\"" + libDesc + "\"\r\n");
            this.libDesc = libDesc;
        }
    }

    /**
     * 设置服务商配置。
     */
    public void setLibConfig(String libConfig){
        if (!Objects.equals(this.libConfig, libConfig)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("lib_config");
            this.UPDATED_INFO.append("lib_config:\"" + this.libConfig+ "\"=>\"" + libConfig + "\"\r\n");
            this.libConfig = libConfig;
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
        sb.append("user_id:\"" + this.userId + "\"\r\n");
        sb.append("user_type:\"" + this.userType + "\"\r\n");
        sb.append("user_info:\"" + this.userInfo + "\"\r\n");
        sb.append("lib_type:\"" + this.libType + "\"\r\n");
        sb.append("lib_name:\"" + this.libName + "\"\r\n");
        sb.append("lib_desc:\"" + this.libDesc + "\"\r\n");
        sb.append("lib_config:\"" + this.libConfig + "\"\r\n");
        sb.append("create_date:\"" + this.createDate + "\"\r\n");
        sb.append("modify_date:\"" + this.modifyDate + "\"\r\n");
        sb.append("state:\"" + this.state + "\"\r\n");
        return sb.toString();
    }

}