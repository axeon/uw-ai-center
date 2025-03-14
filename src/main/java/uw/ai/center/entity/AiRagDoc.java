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
 * AiRagDoc实体类
 * rag文档信息
 *
 * @author axeon
 */
@TableMeta(tableName="ai_rag_doc",tableType="table")
@Schema(title = "rag文档信息", description = "rag文档信息")
public class AiRagDoc implements DataEntity,Serializable{


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
     * libId
     */
    @ColumnMeta(columnName="lib_id", dataType="long", dataSize=19, nullable=false)
    @Schema(title = "libId", description = "libId", maxLength=19, nullable=false )
    private long libId;

    /**
     * 文档类型
     */
    @ColumnMeta(columnName="doc_type", dataType="int", dataSize=10, nullable=true)
    @Schema(title = "文档类型", description = "文档类型", maxLength=10, nullable=true )
    private int docType;

    /**
     * 文档名称
     */
    @ColumnMeta(columnName="doc_name", dataType="String", dataSize=200, nullable=true)
    @Schema(title = "文档名称", description = "文档名称", maxLength=200, nullable=true )
    private String docName;

    /**
     * 文档描述
     */
    @ColumnMeta(columnName="doc_desc", dataType="String", dataSize=65535, nullable=true)
    @Schema(title = "文档描述", description = "文档描述", maxLength=65535, nullable=true )
    private String docDesc;

    /**
     * 文档大小
     */
    @ColumnMeta(columnName="doc_size", dataType="long", dataSize=19, nullable=false)
    @Schema(title = "文档大小", description = "文档大小", maxLength=19, nullable=false )
    private long docSize;

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
        this.UPDATED_INFO = new StringBuilder("表ai_rag_doc主键\"" + 
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
     * 获取libId。
     */
    public long getLibId(){
        return this.libId;
    }

    /**
     * 获取文档类型。
     */
    public int getDocType(){
        return this.docType;
    }

    /**
     * 获取文档名称。
     */
    public String getDocName(){
        return this.docName;
    }

    /**
     * 获取文档描述。
     */
    public String getDocDesc(){
        return this.docDesc;
    }

    /**
     * 获取文档大小。
     */
    public long getDocSize(){
        return this.docSize;
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
     * 设置libId。
     */
    public void setLibId(long libId){
        if (!Objects.equals(this.libId, libId)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("lib_id");
            this.UPDATED_INFO.append("lib_id:\"" + this.libId+ "\"=>\"" + libId + "\"\r\n");
            this.libId = libId;
        }
    }

    /**
     * 设置文档类型。
     */
    public void setDocType(int docType){
        if (!Objects.equals(this.docType, docType)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("doc_type");
            this.UPDATED_INFO.append("doc_type:\"" + this.docType+ "\"=>\"" + docType + "\"\r\n");
            this.docType = docType;
        }
    }

    /**
     * 设置文档名称。
     */
    public void setDocName(String docName){
        if (!Objects.equals(this.docName, docName)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("doc_name");
            this.UPDATED_INFO.append("doc_name:\"" + this.docName+ "\"=>\"" + docName + "\"\r\n");
            this.docName = docName;
        }
    }

    /**
     * 设置文档描述。
     */
    public void setDocDesc(String docDesc){
        if (!Objects.equals(this.docDesc, docDesc)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("doc_desc");
            this.UPDATED_INFO.append("doc_desc:\"" + this.docDesc+ "\"=>\"" + docDesc + "\"\r\n");
            this.docDesc = docDesc;
        }
    }

    /**
     * 设置文档大小。
     */
    public void setDocSize(long docSize){
        if (!Objects.equals(this.docSize, docSize)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("doc_size");
            this.UPDATED_INFO.append("doc_size:\"" + this.docSize+ "\"=>\"" + docSize + "\"\r\n");
            this.docSize = docSize;
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
        sb.append("lib_id:\"" + this.libId + "\"\r\n");
        sb.append("doc_type:\"" + this.docType + "\"\r\n");
        sb.append("doc_name:\"" + this.docName + "\"\r\n");
        sb.append("doc_desc:\"" + this.docDesc + "\"\r\n");
        sb.append("doc_size:\"" + this.docSize + "\"\r\n");
        sb.append("create_date:\"" + this.createDate + "\"\r\n");
        sb.append("modify_date:\"" + this.modifyDate + "\"\r\n");
        sb.append("state:\"" + this.state + "\"\r\n");
        return sb.toString();
    }

}