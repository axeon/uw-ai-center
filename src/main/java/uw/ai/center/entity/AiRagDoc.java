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
    @ColumnMeta(columnName="doc_type", dataType="String", dataSize=20, nullable=true)
    @Schema(title = "文档类型", description = "文档类型", maxLength=20, nullable=true )
    private String docType;

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
     * 文档主体
     */
    @ColumnMeta(columnName="doc_body", dataType="Object", dataSize=2147483646, nullable=true)
    @Schema(title = "文档主体", description = "文档主体", maxLength=2147483646, nullable=true )
    private Object docBody;

    /**
     * 文档内容
     */
    @ColumnMeta(columnName="doc_content", dataType="String", dataSize=1073741824, nullable=true)
    @Schema(title = "文档内容", description = "文档内容", maxLength=1073741824, nullable=true )
    @JsonRawValue(value = false)
    private String docContent;

    /**
     * 文档主体大小
     */
    @ColumnMeta(columnName="doc_body_size", dataType="long", dataSize=19, nullable=true)
    @Schema(title = "文档主体大小", description = "文档主体大小", maxLength=19, nullable=true )
    private long docBodySize;

    /**
     * 文档内容大小
     */
    @ColumnMeta(columnName="doc_content_size", dataType="long", dataSize=19, nullable=true)
    @Schema(title = "文档内容大小", description = "文档内容大小", maxLength=19, nullable=true )
    private long docContentSize;

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
    private transient Set<String> _UPDATED_COLUMN = null;

    /**
     * 更新的信息.
     */
    private transient StringBuilder _UPDATED_INFO = null;


    /**
     * 是否加载完成.
     */
    private transient boolean _IS_LOADED;


    /**
     * 获得实体的表名。
     */
    @Override
    public String ENTITY_TABLE(){
        return "ai_rag_doc";
    }

    /**
     * 获得实体的表注释。
     */
    @Override
    public String ENTITY_NAME(){
        return "rag文档信息";
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
        return _UPDATED_COLUMN;
    }

    /**
     * 获取文本更新信息.
     */
    @Override
    public String GET_UPDATED_INFO() {
        if (this._UPDATED_INFO == null) {
            return null;
        } else {
            return this._UPDATED_INFO.toString();
        }
    }

    /**
     * 清除更新信息.
     */
    @Override
    public void CLEAR_UPDATED_INFO() {
        _UPDATED_COLUMN = null;
        _UPDATED_INFO = null;
    }

    /**
     * 初始化set相关的信息.
     */
    private void _INIT_UPDATE_INFO() {
        this._UPDATED_COLUMN = new HashSet<String>();
        this._UPDATED_INFO = new StringBuilder("表ai_rag_doc主键\"" + 
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
    public String getDocType(){
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
     * 获取文档主体。
     */
    public Object getDocBody(){
        return this.docBody;
    }

    /**
     * 获取文档内容。
     */
    public String getDocContent(){
        return this.docContent;
    }

    /**
     * 获取文档主体大小。
     */
    public long getDocBodySize(){
        return this.docBodySize;
    }

    /**
     * 获取文档内容大小。
     */
    public long getDocContentSize(){
        return this.docContentSize;
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
        if (!_IS_LOADED||!Objects.equals(this.id, id)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("id");
            this._UPDATED_INFO.append("id:\"").append(this.id).append("\"=>\"").append(id).append("\"\n");
            this.id = id;
        }
    }

    /**
     *  设置ID链式调用。
     */
    public AiRagDoc id(long id){
        setId(id);
        return this;
        }

    /**
     * 设置saasId。
     */
    public void setSaasId(long saasId){
        if (!_IS_LOADED||!Objects.equals(this.saasId, saasId)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("saas_id");
            this._UPDATED_INFO.append("saas_id:\"").append(this.saasId).append("\"=>\"").append(saasId).append("\"\n");
            this.saasId = saasId;
        }
    }

    /**
     *  设置saasId链式调用。
     */
    public AiRagDoc saasId(long saasId){
        setSaasId(saasId);
        return this;
        }

    /**
     * 设置libId。
     */
    public void setLibId(long libId){
        if (!_IS_LOADED||!Objects.equals(this.libId, libId)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("lib_id");
            this._UPDATED_INFO.append("lib_id:\"").append(this.libId).append("\"=>\"").append(libId).append("\"\n");
            this.libId = libId;
        }
    }

    /**
     *  设置libId链式调用。
     */
    public AiRagDoc libId(long libId){
        setLibId(libId);
        return this;
        }

    /**
     * 设置文档类型。
     */
    public void setDocType(String docType){
        if (!_IS_LOADED||!Objects.equals(this.docType, docType)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("doc_type");
            this._UPDATED_INFO.append("doc_type:\"").append(this.docType).append("\"=>\"").append(docType).append("\"\n");
            this.docType = docType;
        }
    }

    /**
     *  设置文档类型链式调用。
     */
    public AiRagDoc docType(String docType){
        setDocType(docType);
        return this;
        }

    /**
     * 设置文档名称。
     */
    public void setDocName(String docName){
        if (!_IS_LOADED||!Objects.equals(this.docName, docName)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("doc_name");
            this._UPDATED_INFO.append("doc_name:\"").append(this.docName).append("\"=>\"").append(docName).append("\"\n");
            this.docName = docName;
        }
    }

    /**
     *  设置文档名称链式调用。
     */
    public AiRagDoc docName(String docName){
        setDocName(docName);
        return this;
        }

    /**
     * 设置文档描述。
     */
    public void setDocDesc(String docDesc){
        if (!_IS_LOADED||!Objects.equals(this.docDesc, docDesc)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("doc_desc");
            this._UPDATED_INFO.append("doc_desc:\"").append(this.docDesc).append("\"=>\"").append(docDesc).append("\"\n");
            this.docDesc = docDesc;
        }
    }

    /**
     *  设置文档描述链式调用。
     */
    public AiRagDoc docDesc(String docDesc){
        setDocDesc(docDesc);
        return this;
        }

    /**
     * 设置文档主体。
     */
    public void setDocBody(Object docBody){
        if (!_IS_LOADED||!Objects.equals(this.docBody, docBody)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("doc_body");
            this._UPDATED_INFO.append("doc_body:\"").append(this.docBody).append("\"=>\"").append(docBody).append("\"\n");
            this.docBody = docBody;
        }
    }

    /**
     *  设置文档主体链式调用。
     */
    public AiRagDoc docBody(Object docBody){
        setDocBody(docBody);
        return this;
        }

    /**
     * 设置文档内容。
     */
    public void setDocContent(String docContent){
        if (!_IS_LOADED||!Objects.equals(this.docContent, docContent)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("doc_content");
            this._UPDATED_INFO.append("doc_content:\"").append(this.docContent).append("\"=>\"").append(docContent).append("\"\n");
            this.docContent = docContent;
        }
    }

    /**
     *  设置文档内容链式调用。
     */
    public AiRagDoc docContent(String docContent){
        setDocContent(docContent);
        return this;
        }

    /**
     * 设置文档主体大小。
     */
    public void setDocBodySize(long docBodySize){
        if (!_IS_LOADED||!Objects.equals(this.docBodySize, docBodySize)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("doc_body_size");
            this._UPDATED_INFO.append("doc_body_size:\"").append(this.docBodySize).append("\"=>\"").append(docBodySize).append("\"\n");
            this.docBodySize = docBodySize;
        }
    }

    /**
     *  设置文档主体大小链式调用。
     */
    public AiRagDoc docBodySize(long docBodySize){
        setDocBodySize(docBodySize);
        return this;
        }

    /**
     * 设置文档内容大小。
     */
    public void setDocContentSize(long docContentSize){
        if (!_IS_LOADED||!Objects.equals(this.docContentSize, docContentSize)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("doc_content_size");
            this._UPDATED_INFO.append("doc_content_size:\"").append(this.docContentSize).append("\"=>\"").append(docContentSize).append("\"\n");
            this.docContentSize = docContentSize;
        }
    }

    /**
     *  设置文档内容大小链式调用。
     */
    public AiRagDoc docContentSize(long docContentSize){
        setDocContentSize(docContentSize);
        return this;
        }

    /**
     * 设置创建时间。
     */
    public void setCreateDate(java.util.Date createDate){
        if (!_IS_LOADED||!Objects.equals(this.createDate, createDate)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("create_date");
            this._UPDATED_INFO.append("create_date:\"").append(this.createDate).append("\"=>\"").append(createDate).append("\"\n");
            this.createDate = createDate;
        }
    }

    /**
     *  设置创建时间链式调用。
     */
    public AiRagDoc createDate(java.util.Date createDate){
        setCreateDate(createDate);
        return this;
        }

    /**
     * 设置修改时间。
     */
    public void setModifyDate(java.util.Date modifyDate){
        if (!_IS_LOADED||!Objects.equals(this.modifyDate, modifyDate)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("modify_date");
            this._UPDATED_INFO.append("modify_date:\"").append(this.modifyDate).append("\"=>\"").append(modifyDate).append("\"\n");
            this.modifyDate = modifyDate;
        }
    }

    /**
     *  设置修改时间链式调用。
     */
    public AiRagDoc modifyDate(java.util.Date modifyDate){
        setModifyDate(modifyDate);
        return this;
        }

    /**
     * 设置状态。
     */
    public void setState(int state){
        if (!_IS_LOADED||!Objects.equals(this.state, state)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("state");
            this._UPDATED_INFO.append("state:\"").append(this.state).append("\"=>\"").append(state).append("\"\n");
            this.state = state;
        }
    }

    /**
     *  设置状态链式调用。
     */
    public AiRagDoc state(int state){
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
        sb.append("saas_id:\"" + this.saasId + "\"\r\n");
        sb.append("lib_id:\"" + this.libId + "\"\r\n");
        sb.append("doc_type:\"" + this.docType + "\"\r\n");
        sb.append("doc_name:\"" + this.docName + "\"\r\n");
        sb.append("doc_desc:\"" + this.docDesc + "\"\r\n");
        sb.append("doc_body:\"" + this.docBody + "\"\r\n");
        sb.append("doc_content:\"" + this.docContent + "\"\r\n");
        sb.append("doc_body_size:\"" + this.docBodySize + "\"\r\n");
        sb.append("doc_content_size:\"" + this.docContentSize + "\"\r\n");
        sb.append("create_date:\"" + this.createDate + "\"\r\n");
        sb.append("modify_date:\"" + this.modifyDate + "\"\r\n");
        sb.append("state:\"" + this.state + "\"\r\n");
        return sb.toString();
    }

}