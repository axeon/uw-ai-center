package uw.ai.center.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.dao.DataEntity;
import uw.dao.annotation.ColumnMeta;
import uw.dao.annotation.TableMeta;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * AiSessionInfo实体类
 * session会话
 *
 * @author axeon
 */
@TableMeta(tableName="ai_session_info",tableType="table")
@Schema(title = "session会话", description = "session会话")
public class AiSessionInfo implements DataEntity,Serializable{


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
     * 配置ID
     */
    @ColumnMeta(columnName="config_id", dataType="long", dataSize=19, nullable=true)
    @Schema(title = "配置ID", description = "配置ID", maxLength=19, nullable=true )
    private long configId;

    /**
     * session类型
     */
    @ColumnMeta(columnName="session_type", dataType="int", dataSize=10, nullable=true)
    @Schema(title = "session类型", description = "session类型", maxLength=10, nullable=true )
    private int sessionType;

    /**
     * session名称
     */
    @ColumnMeta(columnName="session_name", dataType="String", dataSize=200, nullable=true)
    @Schema(title = "session名称", description = "session名称", maxLength=200, nullable=true )
    private String sessionName;

    /**
     * session大小
     */
    @ColumnMeta(columnName="msg_num", dataType="int", dataSize=10, nullable=true)
    @Schema(title = "session大小", description = "session大小", maxLength=10, nullable=true )
    private int msgNum;

    /**
     * 历史长度
     */
    @ColumnMeta(columnName="window_size", dataType="int", dataSize=10, nullable=true)
    @Schema(title = "历史长度", description = "历史长度", maxLength=10, nullable=true )
    private int windowSize;

    /**
     * 请求token数
     */
    @ColumnMeta(columnName="request_tokens", dataType="long", dataSize=19, nullable=true)
    @Schema(title = "请求token数", description = "请求token数", maxLength=19, nullable=true )
    private long requestTokens;

    /**
     * 响应token数
     */
    @ColumnMeta(columnName="response_tokens", dataType="long", dataSize=19, nullable=true)
    @Schema(title = "响应token数", description = "响应token数", maxLength=19, nullable=true )
    private long responseTokens;

    /**
     * 系统信息
     */
    @ColumnMeta(columnName="system_prompt", dataType="String", dataSize=2147483646, nullable=true)
    @Schema(title = "系统信息", description = "系统信息", maxLength=2147483646, nullable=true )
    private String systemPrompt;

    /**
     * 工具信息
     */
    @ColumnMeta(columnName="tool_config", dataType="String", dataSize=65535, nullable=true)
    @Schema(title = "工具信息", description = "工具信息", maxLength=65535, nullable=true )
    private String toolConfig;

    /**
     * rag信息
     */
    @ColumnMeta(columnName="rag_config", dataType="String", dataSize=65535, nullable=true)
    @Schema(title = "rag信息", description = "rag信息", maxLength=65535, nullable=true )
    private String ragConfig;

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
     * 最后更新时间
     */
    @ColumnMeta(columnName="last_update", dataType="java.util.Date", dataSize=23, nullable=true)
    @Schema(title = "最后更新时间", description = "最后更新时间", maxLength=23, nullable=true )
    private java.util.Date lastUpdate;

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
         return "ai_session_info";
       }

    /**
     * 获得实体的表注释。
     */
    @Override
    public String ENTITY_NAME(){
          return "session会话";
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
     * 获取配置ID。
     */
    public long getConfigId(){
        return this.configId;
    }

    /**
     * 获取session类型。
     */
    public int getSessionType(){
        return this.sessionType;
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
    public String getSystemPrompt(){
        return this.systemPrompt;
    }

    /**
     * 获取工具信息。
     */
    public String getToolConfig(){
        return this.toolConfig;
    }

    /**
     * 获取rag信息。
     */
    public String getRagConfig(){
        return this.ragConfig;
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
            this.UPDATED_INFO.append("id:\"").append(this.id).append("\"=>\"").append(id).append("\"\n");
            this.id = id;
        }
    }

    /**
     *  设置ID链式调用。
     */
    public AiSessionInfo id(long id){
        setId(id);
        return this;
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
            this.UPDATED_INFO.append("saas_id:\"").append(this.saasId).append("\"=>\"").append(saasId).append("\"\n");
            this.saasId = saasId;
        }
    }

    /**
     *  设置saasId链式调用。
     */
    public AiSessionInfo saasId(long saasId){
        setSaasId(saasId);
        return this;
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
            this.UPDATED_INFO.append("user_id:\"").append(this.userId).append("\"=>\"").append(userId).append("\"\n");
            this.userId = userId;
        }
    }

    /**
     *  设置用户id链式调用。
     */
    public AiSessionInfo userId(long userId){
        setUserId(userId);
        return this;
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
            this.UPDATED_INFO.append("user_type:\"").append(this.userType).append("\"=>\"").append(userType).append("\"\n");
            this.userType = userType;
        }
    }

    /**
     *  设置用户类型链式调用。
     */
    public AiSessionInfo userType(int userType){
        setUserType(userType);
        return this;
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
            this.UPDATED_INFO.append("user_info:\"").append(this.userInfo).append("\"=>\"").append(userInfo).append("\"\n");
            this.userInfo = userInfo;
        }
    }

    /**
     *  设置用户名链式调用。
     */
    public AiSessionInfo userInfo(String userInfo){
        setUserInfo(userInfo);
        return this;
        }

    /**
     * 设置配置ID。
     */
    public void setConfigId(long configId){
        if (!Objects.equals(this.configId, configId)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("config_id");
            this.UPDATED_INFO.append("config_id:\"").append(this.configId).append("\"=>\"").append(configId).append("\"\n");
            this.configId = configId;
        }
    }

    /**
     *  设置配置ID链式调用。
     */
    public AiSessionInfo configId(long configId){
        setConfigId(configId);
        return this;
        }

    /**
     * 设置session类型。
     */
    public void setSessionType(int sessionType){
        if (!Objects.equals(this.sessionType, sessionType)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("session_type");
            this.UPDATED_INFO.append("session_type:\"").append(this.sessionType).append("\"=>\"").append(sessionType).append("\"\n");
            this.sessionType = sessionType;
        }
    }

    /**
     *  设置session类型链式调用。
     */
    public AiSessionInfo sessionType(int sessionType){
        setSessionType(sessionType);
        return this;
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
            this.UPDATED_INFO.append("session_name:\"").append(this.sessionName).append("\"=>\"").append(sessionName).append("\"\n");
            this.sessionName = sessionName;
        }
    }

    /**
     *  设置session名称链式调用。
     */
    public AiSessionInfo sessionName(String sessionName){
        setSessionName(sessionName);
        return this;
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
            this.UPDATED_INFO.append("msg_num:\"").append(this.msgNum).append("\"=>\"").append(msgNum).append("\"\n");
            this.msgNum = msgNum;
        }
    }

    /**
     *  设置session大小链式调用。
     */
    public AiSessionInfo msgNum(int msgNum){
        setMsgNum(msgNum);
        return this;
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
            this.UPDATED_INFO.append("window_size:\"").append(this.windowSize).append("\"=>\"").append(windowSize).append("\"\n");
            this.windowSize = windowSize;
        }
    }

    /**
     *  设置历史长度链式调用。
     */
    public AiSessionInfo windowSize(int windowSize){
        setWindowSize(windowSize);
        return this;
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
            this.UPDATED_INFO.append("request_tokens:\"").append(this.requestTokens).append("\"=>\"").append(requestTokens).append("\"\n");
            this.requestTokens = requestTokens;
        }
    }

    /**
     *  设置请求token数链式调用。
     */
    public AiSessionInfo requestTokens(long requestTokens){
        setRequestTokens(requestTokens);
        return this;
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
            this.UPDATED_INFO.append("response_tokens:\"").append(this.responseTokens).append("\"=>\"").append(responseTokens).append("\"\n");
            this.responseTokens = responseTokens;
        }
    }

    /**
     *  设置响应token数链式调用。
     */
    public AiSessionInfo responseTokens(long responseTokens){
        setResponseTokens(responseTokens);
        return this;
        }

    /**
     * 设置系统信息。
     */
    public void setSystemPrompt(String systemPrompt){
        if (!Objects.equals(this.systemPrompt, systemPrompt)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("system_prompt");
            this.UPDATED_INFO.append("system_prompt:\"").append(this.systemPrompt).append("\"=>\"").append(systemPrompt).append("\"\n");
            this.systemPrompt = systemPrompt;
        }
    }

    /**
     *  设置系统信息链式调用。
     */
    public AiSessionInfo systemPrompt(String systemPrompt){
        setSystemPrompt(systemPrompt);
        return this;
        }

    /**
     * 设置工具信息。
     */
    public void setToolConfig(String toolConfig){
        if (!Objects.equals(this.toolConfig, toolConfig)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("tool_config");
            this.UPDATED_INFO.append("tool_config:\"").append(this.toolConfig).append("\"=>\"").append(toolConfig).append("\"\n");
            this.toolConfig = toolConfig;
        }
    }

    /**
     *  设置工具信息链式调用。
     */
    public AiSessionInfo toolConfig(String toolConfig){
        setToolConfig(toolConfig);
        return this;
        }

    /**
     * 设置rag信息。
     */
    public void setRagConfig(String ragConfig){
        if (!Objects.equals(this.ragConfig, ragConfig)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("rag_config");
            this.UPDATED_INFO.append("rag_config:\"").append(this.ragConfig).append("\"=>\"").append(ragConfig).append("\"\n");
            this.ragConfig = ragConfig;
        }
    }

    /**
     *  设置rag信息链式调用。
     */
    public AiSessionInfo ragConfig(String ragConfig){
        setRagConfig(ragConfig);
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
    public AiSessionInfo createDate(java.util.Date createDate){
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
    public AiSessionInfo modifyDate(java.util.Date modifyDate){
        setModifyDate(modifyDate);
        return this;
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
            this.UPDATED_INFO.append("last_update:\"").append(this.lastUpdate).append("\"=>\"").append(lastUpdate).append("\"\n");
            this.lastUpdate = lastUpdate;
        }
    }

    /**
     *  设置最后更新时间链式调用。
     */
    public AiSessionInfo lastUpdate(java.util.Date lastUpdate){
        setLastUpdate(lastUpdate);
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
    public AiSessionInfo state(int state){
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
        sb.append("user_id:\"" + this.userId + "\"\r\n");
        sb.append("user_type:\"" + this.userType + "\"\r\n");
        sb.append("user_info:\"" + this.userInfo + "\"\r\n");
        sb.append("config_id:\"" + this.configId + "\"\r\n");
        sb.append("session_type:\"" + this.sessionType + "\"\r\n");
        sb.append("session_name:\"" + this.sessionName + "\"\r\n");
        sb.append("msg_num:\"" + this.msgNum + "\"\r\n");
        sb.append("window_size:\"" + this.windowSize + "\"\r\n");
        sb.append("request_tokens:\"" + this.requestTokens + "\"\r\n");
        sb.append("response_tokens:\"" + this.responseTokens + "\"\r\n");
        sb.append("system_prompt:\"" + this.systemPrompt + "\"\r\n");
        sb.append("tool_config:\"" + this.toolConfig + "\"\r\n");
        sb.append("rag_config:\"" + this.ragConfig + "\"\r\n");
        sb.append("create_date:\"" + this.createDate + "\"\r\n");
        sb.append("modify_date:\"" + this.modifyDate + "\"\r\n");
        sb.append("last_update:\"" + this.lastUpdate + "\"\r\n");
        sb.append("state:\"" + this.state + "\"\r\n");
        return sb.toString();
    }

}