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
 * AiVendorInfo实体类
 * AI服务商信息
 *
 * @author axeon
 */
@TableMeta(tableName="ai_vendor_info",tableType="table")
@Schema(title = "AI服务商信息", description = "AI服务商信息")
public class AiVendorInfo implements DataEntity,Serializable{


    /**
     * ID
     */
    @ColumnMeta(columnName="id", dataType="long", dataSize=19, nullable=false, primaryKey=true)
    @Schema(title = "ID", description = "ID")
    private long id;

    /**
     * 服务商代码
     */
    @ColumnMeta(columnName="vendor_code", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "服务商代码", description = "服务商代码")
    private String vendorCode;

    /**
     * 服务商名称
     */
    @ColumnMeta(columnName="vendor_name", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "服务商名称", description = "服务商名称")
    private String vendorName;

    /**
     * 服务商描述
     */
    @ColumnMeta(columnName="vendor_desc", dataType="String", dataSize=65535, nullable=true)
    @Schema(title = "服务商描述", description = "服务商描述")
    private String vendorDesc;

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
        this.UPDATED_INFO = new StringBuilder("表ai_vendor_info主键\"" + 
        this.id+ "\"更新为:\r\n");
    }


    /**
     * 获取ID。
     */
    public long getId(){
        return this.id;
    }

    /**
     * 获取服务商代码。
     */
    public String getVendorCode(){
        return this.vendorCode;
    }

    /**
     * 获取服务商名称。
     */
    public String getVendorName(){
        return this.vendorName;
    }

    /**
     * 获取服务商描述。
     */
    public String getVendorDesc(){
        return this.vendorDesc;
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
     * 设置服务商代码。
     */
    public void setVendorCode(String vendorCode){
        if (!Objects.equals(this.vendorCode, vendorCode)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("vendor_code");
            this.UPDATED_INFO.append("vendor_code:\"" + this.vendorCode+ "\"=>\"" + vendorCode + "\"\r\n");
            this.vendorCode = vendorCode;
        }
    }

    /**
     * 设置服务商名称。
     */
    public void setVendorName(String vendorName){
        if (!Objects.equals(this.vendorName, vendorName)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("vendor_name");
            this.UPDATED_INFO.append("vendor_name:\"" + this.vendorName+ "\"=>\"" + vendorName + "\"\r\n");
            this.vendorName = vendorName;
        }
    }

    /**
     * 设置服务商描述。
     */
    public void setVendorDesc(String vendorDesc){
        if (!Objects.equals(this.vendorDesc, vendorDesc)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("vendor_desc");
            this.UPDATED_INFO.append("vendor_desc:\"" + this.vendorDesc+ "\"=>\"" + vendorDesc + "\"\r\n");
            this.vendorDesc = vendorDesc;
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
        sb.append("vendor_code:\"" + this.vendorCode + "\"\r\n");
        sb.append("vendor_name:\"" + this.vendorName + "\"\r\n");
        sb.append("vendor_desc:\"" + this.vendorDesc + "\"\r\n");
        sb.append("create_date:\"" + this.createDate + "\"\r\n");
        sb.append("modify_date:\"" + this.modifyDate + "\"\r\n");
        sb.append("state:\"" + this.state + "\"\r\n");
        return sb.toString();
    }

}