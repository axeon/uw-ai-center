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
 * AiSessionInfo实体类
 * session信息
 *
 * @author axeon
 */
@TableMeta(tableName="ai_session_info",tableType="table")
@Schema(title = "session信息", description = "session信息")
public class AiSessionInfo implements DataEntity,Serializable{


    /**
     * ID
     */
    @ColumnMeta(columnName="id", dataType="long", dataSize=19, nullable=false, primaryKey=true)
    @Schema(title = "ID", description = "ID")
    private long id;

    /**
     * saasId
     */
    @ColumnMeta(columnName="saas_id", dataType="long", dataSize=19, nullable=false, primaryKey=true)
    @Schema(title = "saasId", description = "saasId")
    private long saasId;

    /**
     * 商户ID
     */
    @ColumnMeta(columnName="mch_id", dataType="long", dataSize=19, nullable=true)
    @Schema(title = "商户ID", description = "商户ID")
    private long mchId;

    /**
     * 用户id
     */
    @ColumnMeta(columnName="user_id", dataType="long", dataSize=19, nullable=false)
    @Schema(title = "用户id", description = "用户id")
    private long userId;

    /**
     * 用户类型
     */
    @ColumnMeta(columnName="user_type", dataType="int", dataSize=10, nullable=true)
    @Schema(title = "用户类型", description = "用户类型")
    private int userType;

    /**
     * 用户组ID
     */
    @ColumnMeta(columnName="group_id", dataType="long", dataSize=19, nullable=true)
    @Schema(title = "用户组ID", description = "用户组ID")
    private long groupId;

    /**
     * 用户名
     */
    @ColumnMeta(columnName="user_name", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "用户名", description = "用户名")
    private String userName;

    /**
     * 用户昵称
     */
    @ColumnMeta(columnName="nick_name", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "用户昵称", description = "用户昵称")
    private String nickName;

    /**
     * 真实名称
     */
    @ColumnMeta(columnName="real_name", dataType="String", dataSize=100, nullable=true)
    @Schema(title = "真实名称", description = "真实名称")
    private String realName;

    /**
     * session名称
     */
    @ColumnMeta(columnName="session_name", dataType="String", dataSize=200, nullable=true)
    @Schema(title = "session名称", description = "session名称")
    private String sessionName;

    /**
     * session大小
     */
    @ColumnMeta(columnName="msg_num", dataType="int", dataSize=10, nullable=true)
    @Schema(title = "session大小", description = "session大小")
    private int msgNum;

    /**
     * 历史长度
     */
    @ColumnMeta(columnName="window_size", dataType="int", dataSize=10, nullable=true)
    @Schema(title = "历史长度", description = "历史长度")
    private int windowSize;

    /**
     * 请求token数
     */
    @ColumnMeta(columnName="request_tokens", dataType="long", dataSize=19, nullable=true)
    @Schema(title = "请求token数", description = "请求token数")
    private long requestTokens;

    /**
     * 响应token数
     */
    @ColumnMeta(columnName="response_tokens", dataType="long", dataSize=19, nullable=true)
    @Schema(title = "响应token数", description = "响应token数")
    private long responseTokens;

    /**
     * 系统信息
     */
    @ColumnMeta(columnName="system_info", dataType="String", dataSize=2000, nullable=true)
    @Schema(title = "系统信息", description = "系统信息")
    private String systemInfo;

    /**
     * 创建时间
     */
    @ColumnMeta(columnName="create_date", dataType="java.util.Date", dataSize=23, nullable=true)
    @Schema(title = "创建时间", description = "创建时间")
    private java.util.Date createDate;

    /**
     * 最后更新时间
     */
    @ColumnMeta(columnName="last_update", dataType="java.util.Date", dataSize=23, nullable=true)
    @Schema(title = "最后更新时间", description = "最后更新时间")
    private java.util.Date lastUpdate;

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
        this.UPDATED_INFO = new StringBuilder("表ai_session_info主键\"" + 
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
     * 获取商户ID。
     */
    public long getMchId(){
        return this.mchId;
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
     * 获取用户组ID。
     */
    public long getGroupId(){
        return this.groupId;
    }

    /**
     * 获取用户名。
     */
    public String getUserName(){
        return this.userName;
    }

    /**
     * 获取用户昵称。
     */
    public String getNickName(){
        return this.nickName;
    }

    /**
     * 获取真实名称。
     */
    public String getRealName(){
        return this.realName;
    }

    /**
     * 获取session名称。
     */
    public String getSessionName(){
        return this.sessionName;
    }

    /**
     * 获取session大小。
     */
    public int getMsgNum(){
        return this.msgNum;
    }

    /**
     * 获取历史长度。
     */
    public int getWindowSize(){
        return this.windowSize;
    }

    /**
     * 获取请求token数。
     */
    public long getRequestTokens(){
        return this.requestTokens;
    }

    /**
     * 获取响应token数。
     */
    public long getResponseTokens(){
        return this.responseTokens;
    }

    /**
     * 获取系统信息。
     */
    public String getSystemInfo(){
        return this.systemInfo;
    }

    /**
     * 获取创建时间。
     */
    public java.util.Date getCreateDate(){
        return this.createDate;
    }

    /**
     * 获取最后更新时间。
     */
    public java.util.Date getLastUpdate(){
        return this.lastUpdate;
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
     * 设置商户ID。
     */
    public void setMchId(long mchId){
        if (!Objects.equals(this.mchId, mchId)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("mch_id");
            this.UPDATED_INFO.append("mch_id:\"" + this.mchId+ "\"=>\"" + mchId + "\"\r\n");
            this.mchId = mchId;
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
     * 设置用户组ID。
     */
    public void setGroupId(long groupId){
        if (!Objects.equals(this.groupId, groupId)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("group_id");
            this.UPDATED_INFO.append("group_id:\"" + this.groupId+ "\"=>\"" + groupId + "\"\r\n");
            this.groupId = groupId;
        }
    }

    /**
     * 设置用户名。
     */
    public void setUserName(String userName){
        if (!Objects.equals(this.userName, userName)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("user_name");
            this.UPDATED_INFO.append("user_name:\"" + this.userName+ "\"=>\"" + userName + "\"\r\n");
            this.userName = userName;
        }
    }

    /**
     * 设置用户昵称。
     */
    public void setNickName(String nickName){
        if (!Objects.equals(this.nickName, nickName)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("nick_name");
            this.UPDATED_INFO.append("nick_name:\"" + this.nickName+ "\"=>\"" + nickName + "\"\r\n");
            this.nickName = nickName;
        }
    }

    /**
     * 设置真实名称。
     */
    public void setRealName(String realName){
        if (!Objects.equals(this.realName, realName)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("real_name");
            this.UPDATED_INFO.append("real_name:\"" + this.realName+ "\"=>\"" + realName + "\"\r\n");
            this.realName = realName;
        }
    }

    /**
     * 设置session名称。
     */
    public void setSessionName(String sessionName){
        if (!Objects.equals(this.sessionName, sessionName)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("session_name");
            this.UPDATED_INFO.append("session_name:\"" + this.sessionName+ "\"=>\"" + sessionName + "\"\r\n");
            this.sessionName = sessionName;
        }
    }

    /**
     * 设置session大小。
     */
    public void setMsgNum(int msgNum){
        if (!Objects.equals(this.msgNum, msgNum)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("msg_num");
            this.UPDATED_INFO.append("msg_num:\"" + this.msgNum+ "\"=>\"" + msgNum + "\"\r\n");
            this.msgNum = msgNum;
        }
    }

    /**
     * 设置历史长度。
     */
    public void setWindowSize(int windowSize){
        if (!Objects.equals(this.windowSize, windowSize)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("window_size");
            this.UPDATED_INFO.append("window_size:\"" + this.windowSize+ "\"=>\"" + windowSize + "\"\r\n");
            this.windowSize = windowSize;
        }
    }

    /**
     * 设置请求token数。
     */
    public void setRequestTokens(long requestTokens){
        if (!Objects.equals(this.requestTokens, requestTokens)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("request_tokens");
            this.UPDATED_INFO.append("request_tokens:\"" + this.requestTokens+ "\"=>\"" + requestTokens + "\"\r\n");
            this.requestTokens = requestTokens;
        }
    }

    /**
     * 设置响应token数。
     */
    public void setResponseTokens(long responseTokens){
        if (!Objects.equals(this.responseTokens, responseTokens)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("response_tokens");
            this.UPDATED_INFO.append("response_tokens:\"" + this.responseTokens+ "\"=>\"" + responseTokens + "\"\r\n");
            this.responseTokens = responseTokens;
        }
    }

    /**
     * 设置系统信息。
     */
    public void setSystemInfo(String systemInfo){
        if (!Objects.equals(this.systemInfo, systemInfo)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("system_info");
            this.UPDATED_INFO.append("system_info:\"" + this.systemInfo+ "\"=>\"" + systemInfo + "\"\r\n");
            this.systemInfo = systemInfo;
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
     * 设置最后更新时间。
     */
    public void setLastUpdate(java.util.Date lastUpdate){
        if (!Objects.equals(this.lastUpdate, lastUpdate)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("last_update");
            this.UPDATED_INFO.append("last_update:\"" + this.lastUpdate+ "\"=>\"" + lastUpdate + "\"\r\n");
            this.lastUpdate = lastUpdate;
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
        sb.append("mch_id:\"" + this.mchId + "\"\r\n");
        sb.append("user_id:\"" + this.userId + "\"\r\n");
        sb.append("user_type:\"" + this.userType + "\"\r\n");
        sb.append("group_id:\"" + this.groupId + "\"\r\n");
        sb.append("user_name:\"" + this.userName + "\"\r\n");
        sb.append("nick_name:\"" + this.nickName + "\"\r\n");
        sb.append("real_name:\"" + this.realName + "\"\r\n");
        sb.append("session_name:\"" + this.sessionName + "\"\r\n");
        sb.append("msg_num:\"" + this.msgNum + "\"\r\n");
        sb.append("window_size:\"" + this.windowSize + "\"\r\n");
        sb.append("request_tokens:\"" + this.requestTokens + "\"\r\n");
        sb.append("response_tokens:\"" + this.responseTokens + "\"\r\n");
        sb.append("system_info:\"" + this.systemInfo + "\"\r\n");
        sb.append("create_date:\"" + this.createDate + "\"\r\n");
        sb.append("last_update:\"" + this.lastUpdate + "\"\r\n");
        sb.append("state:\"" + this.state + "\"\r\n");
        return sb.toString();
    }

}