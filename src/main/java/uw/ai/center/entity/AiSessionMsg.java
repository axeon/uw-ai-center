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
 * AiSessionMsg实体类
 * session消息
 *
 * @author axeon
 */
@TableMeta(tableName="ai_session_msg",tableType="table")
@Schema(title = "session消息", description = "session消息")
public class AiSessionMsg implements DataEntity,Serializable{


    /**
     * ID
     */
    @ColumnMeta(columnName="id", dataType="long", dataSize=19, nullable=false, primaryKey=true)
    @Schema(title = "ID", description = "ID")
    private long id;

    /**
     * sessionId
     */
    @ColumnMeta(columnName="session_id", dataType="long", dataSize=19, nullable=false)
    @Schema(title = "sessionId", description = "sessionId")
    private long sessionId;

    /**
     * 请求信息
     */
    @ColumnMeta(columnName="request_info", dataType="String", dataSize=2147483647, nullable=true)
    @Schema(title = "请求信息", description = "请求信息")
    private String requestInfo;

    /**
     * 返回信息
     */
    @ColumnMeta(columnName="response_info", dataType="String", dataSize=2147483647, nullable=true)
    @Schema(title = "返回信息", description = "返回信息")
    private String responseInfo;

    /**
     * 创建时间
     */
    @ColumnMeta(columnName="create_date", dataType="java.util.Date", dataSize=23, nullable=true)
    @Schema(title = "创建时间", description = "创建时间")
    private java.util.Date createDate;

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
        this.UPDATED_INFO = new StringBuilder("表ai_session_msg主键\"" + 
        this.id+ "\"更新为:\r\n");
    }


    /**
     * 获取ID。
     */
    public long getId(){
        return this.id;
    }

    /**
     * 获取sessionId。
     */
    public long getSessionId(){
        return this.sessionId;
    }

    /**
     * 获取请求信息。
     */
    public String getRequestInfo(){
        return this.requestInfo;
    }

    /**
     * 获取返回信息。
     */
    public String getResponseInfo(){
        return this.responseInfo;
    }

    /**
     * 获取创建时间。
     */
    public java.util.Date getCreateDate(){
        return this.createDate;
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
     * 设置sessionId。
     */
    public void setSessionId(long sessionId){
        if (!Objects.equals(this.sessionId, sessionId)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("session_id");
            this.UPDATED_INFO.append("session_id:\"" + this.sessionId+ "\"=>\"" + sessionId + "\"\r\n");
            this.sessionId = sessionId;
        }
    }

    /**
     * 设置请求信息。
     */
    public void setRequestInfo(String requestInfo){
        if (!Objects.equals(this.requestInfo, requestInfo)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("request_info");
            this.UPDATED_INFO.append("request_info:\"" + this.requestInfo+ "\"=>\"" + requestInfo + "\"\r\n");
            this.requestInfo = requestInfo;
        }
    }

    /**
     * 设置返回信息。
     */
    public void setResponseInfo(String responseInfo){
        if (!Objects.equals(this.responseInfo, responseInfo)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("response_info");
            this.UPDATED_INFO.append("response_info:\"" + this.responseInfo+ "\"=>\"" + responseInfo + "\"\r\n");
            this.responseInfo = responseInfo;
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
        sb.append("session_id:\"" + this.sessionId + "\"\r\n");
        sb.append("request_info:\"" + this.requestInfo + "\"\r\n");
        sb.append("response_info:\"" + this.responseInfo + "\"\r\n");
        sb.append("create_date:\"" + this.createDate + "\"\r\n");
        sb.append("state:\"" + this.state + "\"\r\n");
        return sb.toString();
    }

}