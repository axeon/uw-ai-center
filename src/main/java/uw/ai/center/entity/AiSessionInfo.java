package uw.ai.center.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.util.JsonUtils;
import uw.dao.DataEntity;
import uw.dao.DataUpdateInfo;
import uw.dao.annotation.ColumnMeta;
import uw.dao.annotation.TableMeta;

import java.io.Serializable;


/**
 * AiSessionInfo实体类
 * session会话
 *
 * @author axeon
 */
@TableMeta(tableName = "ai_session_info", tableType = "table")
@Schema(title = "session会话", description = "session会话")
public class AiSessionInfo implements DataEntity, Serializable {


    /**
     * ID
     */
    @ColumnMeta(columnName = "id", dataType = "long", dataSize = 19, nullable = false, primaryKey = true)
    @Schema(title = "ID", description = "ID", maxLength = 19, nullable = false)
    private long id;

    /**
     * saasId
     */
    @ColumnMeta(columnName = "saas_id", dataType = "long", dataSize = 19, nullable = false, primaryKey = true)
    @Schema(title = "saasId", description = "saasId", maxLength = 19, nullable = false)
    private long saasId;

    /**
     * 用户id
     */
    @ColumnMeta(columnName = "user_id", dataType = "long", dataSize = 19, nullable = false)
    @Schema(title = "用户id", description = "用户id", maxLength = 19, nullable = false)
    private long userId;

    /**
     * 用户类型
     */
    @ColumnMeta(columnName = "user_type", dataType = "int", dataSize = 10, nullable = true)
    @Schema(title = "用户类型", description = "用户类型", maxLength = 10, nullable = true)
    private int userType;

    /**
     * 用户名
     */
    @ColumnMeta(columnName = "user_info", dataType = "String", dataSize = 200, nullable = true)
    @Schema(title = "用户名", description = "用户名", maxLength = 200, nullable = true)
    private String userInfo;

    /**
     * 配置ID
     */
    @ColumnMeta(columnName = "config_id", dataType = "long", dataSize = 19, nullable = true)
    @Schema(title = "配置ID", description = "配置ID", maxLength = 19, nullable = true)
    private long configId;

    /**
     * session类型
     */
    @ColumnMeta(columnName = "session_type", dataType = "int", dataSize = 10, nullable = true)
    @Schema(title = "session类型", description = "session类型", maxLength = 10, nullable = true)
    private int sessionType;

    /**
     * session名称
     */
    @ColumnMeta(columnName = "session_name", dataType = "String", dataSize = 200, nullable = true)
    @Schema(title = "session名称", description = "session名称", maxLength = 200, nullable = true)
    private String sessionName;

    /**
     * session大小
     */
    @ColumnMeta(columnName = "msg_num", dataType = "int", dataSize = 10, nullable = true)
    @Schema(title = "session大小", description = "session大小", maxLength = 10, nullable = true)
    private int msgNum;

    /**
     * 历史长度
     */
    @ColumnMeta(columnName = "window_size", dataType = "int", dataSize = 10, nullable = true)
    @Schema(title = "历史长度", description = "历史长度", maxLength = 10, nullable = true)
    private int windowSize;

    /**
     * 请求token数
     */
    @ColumnMeta(columnName = "request_tokens", dataType = "long", dataSize = 19, nullable = true)
    @Schema(title = "请求token数", description = "请求token数", maxLength = 19, nullable = true)
    private long requestTokens;

    /**
     * 响应token数
     */
    @ColumnMeta(columnName = "response_tokens", dataType = "long", dataSize = 19, nullable = true)
    @Schema(title = "响应token数", description = "响应token数", maxLength = 19, nullable = true)
    private long responseTokens;

    /**
     * 系统信息
     */
    @ColumnMeta(columnName = "system_prompt", dataType = "String", dataSize = 2147483646, nullable = true)
    @Schema(title = "系统信息", description = "系统信息", maxLength = 2147483646, nullable = true)
    private String systemPrompt;

    /**
     * 工具信息
     */
    @ColumnMeta(columnName = "tool_config", dataType = "String", dataSize = 65535, nullable = true)
    @Schema(title = "工具信息", description = "工具信息", maxLength = 65535, nullable = true)
    private String toolConfig;

    /**
     * rag信息
     */
    @ColumnMeta(columnName = "rag_config", dataType = "String", dataSize = 65535, nullable = true)
    @Schema(title = "rag信息", description = "rag信息", maxLength = 65535, nullable = true)
    private String ragConfig;

    /**
     * 创建时间
     */
    @ColumnMeta(columnName = "create_date", dataType = "java.util.Date", dataSize = 23, nullable = true)
    @Schema(title = "创建时间", description = "创建时间", maxLength = 23, nullable = true)
    private java.util.Date createDate;

    /**
     * 修改时间
     */
    @ColumnMeta(columnName = "modify_date", dataType = "java.util.Date", dataSize = 23, nullable = true)
    @Schema(title = "修改时间", description = "修改时间", maxLength = 23, nullable = true)
    private java.util.Date modifyDate;

    /**
     * 最后更新时间
     */
    @ColumnMeta(columnName = "last_update", dataType = "java.util.Date", dataSize = 23, nullable = true)
    @Schema(title = "最后更新时间", description = "最后更新时间", maxLength = 23, nullable = true)
    private java.util.Date lastUpdate;

    /**
     * 状态
     */
    @ColumnMeta(columnName = "state", dataType = "int", dataSize = 10, nullable = true)
    @Schema(title = "状态", description = "状态", maxLength = 10, nullable = true)
    private int state;

    /**
     * 数据更新信息.
     */
    private transient DataUpdateInfo _UPDATED_INFO = null;

    /**
     * 是否加载完成.
     */
    private transient boolean _IS_LOADED;

    /**
     * 获得实体的表名。
     */
    @Override
    public String ENTITY_TABLE() {
        return "ai_session_info";
    }

    /**
     * 获得实体的表注释。
     */
    @Override
    public String ENTITY_NAME() {
        return "session会话";
    }

    /**
     * 获得主键
     */
    @Override
    public Serializable ENTITY_ID() {
        return getId();
    }

    /**
     * 获取更新信息.
     */
    @Override
    public DataUpdateInfo GET_UPDATED_INFO() {
        return this._UPDATED_INFO;
    }

    /**
     * 清除更新信息.
     */
    @Override
    public void CLEAR_UPDATED_INFO() {
        _UPDATED_INFO = null;
    }


    /**
     * 获取ID。
     */
    public long getId() {
        return this.id;
    }

    /**
     * 设置ID。
     */
    public void setId(long id) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "id", this.id, id, !_IS_LOADED);
        this.id = id;
    }

    /**
     * 获取saasId。
     */
    public long getSaasId() {
        return this.saasId;
    }

    /**
     * 设置saasId。
     */
    public void setSaasId(long saasId) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "saasId", this.saasId, saasId, !_IS_LOADED);
        this.saasId = saasId;
    }

    /**
     * 获取用户id。
     */
    public long getUserId() {
        return this.userId;
    }

    /**
     * 设置用户id。
     */
    public void setUserId(long userId) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "userId", this.userId, userId, !_IS_LOADED);
        this.userId = userId;
    }

    /**
     * 获取用户类型。
     */
    public int getUserType() {
        return this.userType;
    }

    /**
     * 设置用户类型。
     */
    public void setUserType(int userType) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "userType", this.userType, userType, !_IS_LOADED);
        this.userType = userType;
    }

    /**
     * 获取用户名。
     */
    public String getUserInfo() {
        return this.userInfo;
    }

    /**
     * 设置用户名。
     */
    public void setUserInfo(String userInfo) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "userInfo", this.userInfo, userInfo, !_IS_LOADED);
        this.userInfo = userInfo;
    }

    /**
     * 获取配置ID。
     */
    public long getConfigId() {
        return this.configId;
    }

    /**
     * 设置配置ID。
     */
    public void setConfigId(long configId) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "configId", this.configId, configId, !_IS_LOADED);
        this.configId = configId;
    }

    /**
     * 获取session类型。
     */
    public int getSessionType() {
        return this.sessionType;
    }

    /**
     * 设置session类型。
     */
    public void setSessionType(int sessionType) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "sessionType", this.sessionType, sessionType, !_IS_LOADED);
        this.sessionType = sessionType;
    }

    /**
     * 获取session名称。
     */
    public String getSessionName() {
        return this.sessionName;
    }

    /**
     * 设置session名称。
     */
    public void setSessionName(String sessionName) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "sessionName", this.sessionName, sessionName, !_IS_LOADED);
        this.sessionName = sessionName;
    }

    /**
     * 获取session大小。
     */
    public int getMsgNum() {
        return this.msgNum;
    }

    /**
     * 设置session大小。
     */
    public void setMsgNum(int msgNum) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "msgNum", this.msgNum, msgNum, !_IS_LOADED);
        this.msgNum = msgNum;
    }

    /**
     * 获取历史长度。
     */
    public int getWindowSize() {
        return this.windowSize;
    }

    /**
     * 设置历史长度。
     */
    public void setWindowSize(int windowSize) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "windowSize", this.windowSize, windowSize, !_IS_LOADED);
        this.windowSize = windowSize;
    }

    /**
     * 获取请求token数。
     */
    public long getRequestTokens() {
        return this.requestTokens;
    }

    /**
     * 设置请求token数。
     */
    public void setRequestTokens(long requestTokens) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "requestTokens", this.requestTokens, requestTokens, !_IS_LOADED);
        this.requestTokens = requestTokens;
    }

    /**
     * 获取响应token数。
     */
    public long getResponseTokens() {
        return this.responseTokens;
    }

    /**
     * 设置响应token数。
     */
    public void setResponseTokens(long responseTokens) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "responseTokens", this.responseTokens, responseTokens, !_IS_LOADED);
        this.responseTokens = responseTokens;
    }

    /**
     * 获取系统信息。
     */
    public String getSystemPrompt() {
        return this.systemPrompt;
    }

    /**
     * 设置系统信息。
     */
    public void setSystemPrompt(String systemPrompt) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "systemPrompt", this.systemPrompt, systemPrompt, !_IS_LOADED);
        this.systemPrompt = systemPrompt;
    }

    /**
     * 获取工具信息。
     */
    public String getToolConfig() {
        return this.toolConfig;
    }

    /**
     * 设置工具信息。
     */
    public void setToolConfig(String toolConfig) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "toolConfig", this.toolConfig, toolConfig, !_IS_LOADED);
        this.toolConfig = toolConfig;
    }

    /**
     * 获取rag信息。
     */
    public String getRagConfig() {
        return this.ragConfig;
    }

    /**
     * 设置rag信息。
     */
    public void setRagConfig(String ragConfig) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "ragConfig", this.ragConfig, ragConfig, !_IS_LOADED);
        this.ragConfig = ragConfig;
    }

    /**
     * 获取创建时间。
     */
    public java.util.Date getCreateDate() {
        return this.createDate;
    }

    /**
     * 设置创建时间。
     */
    public void setCreateDate(java.util.Date createDate) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "createDate", this.createDate, createDate, !_IS_LOADED);
        this.createDate = createDate;
    }

    /**
     * 获取修改时间。
     */
    public java.util.Date getModifyDate() {
        return this.modifyDate;
    }

    /**
     * 设置修改时间。
     */
    public void setModifyDate(java.util.Date modifyDate) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "modifyDate", this.modifyDate, modifyDate, !_IS_LOADED);
        this.modifyDate = modifyDate;
    }

    /**
     * 获取最后更新时间。
     */
    public java.util.Date getLastUpdate() {
        return this.lastUpdate;
    }

    /**
     * 设置最后更新时间。
     */
    public void setLastUpdate(java.util.Date lastUpdate) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "lastUpdate", this.lastUpdate, lastUpdate, !_IS_LOADED);
        this.lastUpdate = lastUpdate;
    }

    /**
     * 获取状态。
     */
    public int getState() {
        return this.state;
    }

    /**
     * 设置状态。
     */
    public void setState(int state) {
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "state", this.state, state, !_IS_LOADED);
        this.state = state;
    }

    /**
     * 设置ID链式调用。
     */
    public AiSessionInfo id(long id) {
        setId(id);
        return this;
    }

    /**
     * 设置saasId链式调用。
     */
    public AiSessionInfo saasId(long saasId) {
        setSaasId(saasId);
        return this;
    }

    /**
     * 设置用户id链式调用。
     */
    public AiSessionInfo userId(long userId) {
        setUserId(userId);
        return this;
    }

    /**
     * 设置用户类型链式调用。
     */
    public AiSessionInfo userType(int userType) {
        setUserType(userType);
        return this;
    }

    /**
     * 设置用户名链式调用。
     */
    public AiSessionInfo userInfo(String userInfo) {
        setUserInfo(userInfo);
        return this;
    }

    /**
     * 设置配置ID链式调用。
     */
    public AiSessionInfo configId(long configId) {
        setConfigId(configId);
        return this;
    }

    /**
     * 设置session类型链式调用。
     */
    public AiSessionInfo sessionType(int sessionType) {
        setSessionType(sessionType);
        return this;
    }

    /**
     * 设置session名称链式调用。
     */
    public AiSessionInfo sessionName(String sessionName) {
        setSessionName(sessionName);
        return this;
    }

    /**
     * 设置session大小链式调用。
     */
    public AiSessionInfo msgNum(int msgNum) {
        setMsgNum(msgNum);
        return this;
    }

    /**
     * 设置历史长度链式调用。
     */
    public AiSessionInfo windowSize(int windowSize) {
        setWindowSize(windowSize);
        return this;
    }

    /**
     * 设置请求token数链式调用。
     */
    public AiSessionInfo requestTokens(long requestTokens) {
        setRequestTokens(requestTokens);
        return this;
    }

    /**
     * 设置响应token数链式调用。
     */
    public AiSessionInfo responseTokens(long responseTokens) {
        setResponseTokens(responseTokens);
        return this;
    }

    /**
     * 设置系统信息链式调用。
     */
    public AiSessionInfo systemPrompt(String systemPrompt) {
        setSystemPrompt(systemPrompt);
        return this;
    }

    /**
     * 设置工具信息链式调用。
     */
    public AiSessionInfo toolConfig(String toolConfig) {
        setToolConfig(toolConfig);
        return this;
    }

    /**
     * 设置rag信息链式调用。
     */
    public AiSessionInfo ragConfig(String ragConfig) {
        setRagConfig(ragConfig);
        return this;
    }

    /**
     * 设置创建时间链式调用。
     */
    public AiSessionInfo createDate(java.util.Date createDate) {
        setCreateDate(createDate);
        return this;
    }

    /**
     * 设置修改时间链式调用。
     */
    public AiSessionInfo modifyDate(java.util.Date modifyDate) {
        setModifyDate(modifyDate);
        return this;
    }

    /**
     * 设置最后更新时间链式调用。
     */
    public AiSessionInfo lastUpdate(java.util.Date lastUpdate) {
        setLastUpdate(lastUpdate);
        return this;
    }

    /**
     * 设置状态链式调用。
     */
    public AiSessionInfo state(int state) {
        setState(state);
        return this;
    }

    /**
     * 重载toString方法.
     */
    @Override
    public String toString() {
        return JsonUtils.toString(this);
    }

}