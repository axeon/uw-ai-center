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
 * AiVendorModel实体类
 * AI服务模型
 *
 * @author axeon
 */
@TableMeta(tableName="ai_vendor_model",tableType="table")
@Schema(title = "AI服务模型", description = "AI服务模型")
public class AiVendorModel implements DataEntity,Serializable{


    /**
     * ID
     */
    @ColumnMeta(columnName="id", dataType="long", dataSize=19, nullable=false, primaryKey=true)
    @Schema(title = "ID", description = "ID")
    private long id;

    /**
     * 服务商ID
     */
    @ColumnMeta(columnName="vendor_id", dataType="long", dataSize=19, nullable=false)
    @Schema(title = "服务商ID", description = "服务商ID")
    private long vendorId;

    /**
     * 服务商代码
     */
    @ColumnMeta(columnName="model_code", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "服务商代码", description = "服务商代码")
    private String modelCode;

    /**
     * 服务商名称
     */
    @ColumnMeta(columnName="model_name", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "服务商名称", description = "服务商名称")
    private String modelName;

    /**
     * 服务商描述
     */
    @ColumnMeta(columnName="model_desc", dataType="String", dataSize=65535, nullable=true)
    @Schema(title = "服务商描述", description = "服务商描述")
    private String modelDesc;

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
        this.UPDATED_INFO = new StringBuilder("表ai_vendor_model主键\"" + 
        this.id+ "\"更新为:\r\n");
    }


    /**
     * 获取ID。
     */
    public long getId(){
        return this.id;
    }

    /**
     * 获取服务商ID。
     */
    public long getVendorId(){
        return this.vendorId;
    }

    /**
     * 获取服务商代码。
     */
    public String getModelCode(){
        return this.modelCode;
    }

    /**
     * 获取服务商名称。
     */
    public String getModelName(){
        return this.modelName;
    }

    /**
     * 获取服务商描述。
     */
    public String getModelDesc(){
        return this.modelDesc;
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
     * 设置服务商ID。
     */
    public void setVendorId(long vendorId){
        if (!Objects.equals(this.vendorId, vendorId)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("vendor_id");
            this.UPDATED_INFO.append("vendor_id:\"" + this.vendorId+ "\"=>\"" + vendorId + "\"\r\n");
            this.vendorId = vendorId;
        }
    }

    /**
     * 设置服务商代码。
     */
    public void setModelCode(String modelCode){
        if (!Objects.equals(this.modelCode, modelCode)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("model_code");
            this.UPDATED_INFO.append("model_code:\"" + this.modelCode+ "\"=>\"" + modelCode + "\"\r\n");
            this.modelCode = modelCode;
        }
    }

    /**
     * 设置服务商名称。
     */
    public void setModelName(String modelName){
        if (!Objects.equals(this.modelName, modelName)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("model_name");
            this.UPDATED_INFO.append("model_name:\"" + this.modelName+ "\"=>\"" + modelName + "\"\r\n");
            this.modelName = modelName;
        }
    }

    /**
     * 设置服务商描述。
     */
    public void setModelDesc(String modelDesc){
        if (!Objects.equals(this.modelDesc, modelDesc)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("model_desc");
            this.UPDATED_INFO.append("model_desc:\"" + this.modelDesc+ "\"=>\"" + modelDesc + "\"\r\n");
            this.modelDesc = modelDesc;
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
        sb.append("vendor_id:\"" + this.vendorId + "\"\r\n");
        sb.append("model_code:\"" + this.modelCode + "\"\r\n");
        sb.append("model_name:\"" + this.modelName + "\"\r\n");
        sb.append("model_desc:\"" + this.modelDesc + "\"\r\n");
        sb.append("create_date:\"" + this.createDate + "\"\r\n");
        sb.append("modify_date:\"" + this.modifyDate + "\"\r\n");
        sb.append("state:\"" + this.state + "\"\r\n");
        return sb.toString();
    }

}