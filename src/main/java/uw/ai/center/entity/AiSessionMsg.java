package uw.ai.center.entity;

import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.util.JsonUtils;
import uw.dao.DataEntity;
import uw.dao.DataUpdateInfo;
import uw.dao.annotation.ColumnMeta;
import uw.dao.annotation.TableMeta;

import java.io.Serializable;


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
    @Schema(title = "ID", description = "ID", maxLength=19, nullable=false )
    private long id;

    /**
     * saasId
     */
    @ColumnMeta(columnName="saas_id", dataType="long", dataSize=19, nullable=false, primaryKey=true)
    @Schema(title = "saasId", description = "saasId", maxLength=19, nullable=false )
    private long saasId;

    /**
     * sessionId
     */
    @ColumnMeta(columnName="session_id", dataType="long", dataSize=19, nullable=false)
    @Schema(title = "sessionId", description = "sessionId", maxLength=19, nullable=false )
    private long sessionId;

    /**
     * 系统提问
     */
    @ColumnMeta(columnName="system_prompt", dataType="String", dataSize=2147483646, nullable=true)
    @Schema(title = "系统提问", description = "系统提问", maxLength=2147483646, nullable=true )
    private String systemPrompt;

    /**
     * 用户提问
     */
    @ColumnMeta(columnName="user_prompt", dataType="String", dataSize=2147483646, nullable=true)
    @Schema(title = "用户提问", description = "用户提问", maxLength=2147483646, nullable=true )
    private String userPrompt;

    /**
     * 上下文数据
     */
    @ColumnMeta(columnName="context_data", dataType="String", dataSize=2147483646, nullable=true)
    @Schema(title = "上下文数据", description = "上下文数据", maxLength=2147483646, nullable=true )
    private String contextData;

    /**
     * 工具信息
     */
    @ColumnMeta(columnName="tool_config", dataType="String", dataSize=65535, nullable=true)
    @Schema(title = "工具信息", description = "工具信息", maxLength=65535, nullable=true )
    private String toolConfig;

    /**
     * 文件信息
     */
    @ColumnMeta(columnName="file_config", dataType="String", dataSize=65535, nullable=true)
    @Schema(title = "文件信息", description = "文件信息", maxLength=65535, nullable=true )
    private String fileConfig;

    /**
     * rag信息
     */
    @ColumnMeta(columnName="rag_config", dataType="String", dataSize=65535, nullable=true)
    @Schema(title = "rag信息", description = "rag信息", maxLength=65535, nullable=true )
    private String ragConfig;

    /**
     * 返回信息
     */
    @ColumnMeta(columnName="response_info", dataType="String", dataSize=2147483646, nullable=true)
    @Schema(title = "返回信息", description = "返回信息", maxLength=2147483646, nullable=true )
    private String responseInfo;

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
     * 创建时间
     */
    @ColumnMeta(columnName="request_date", dataType="java.util.Date", dataSize=23, nullable=true)
    @Schema(title = "创建时间", description = "创建时间", maxLength=23, nullable=true )
    private java.util.Date requestDate;

    /**
     * 回应开始时间
     */
    @ColumnMeta(columnName="response_start_date", dataType="java.util.Date", dataSize=23, nullable=true)
    @Schema(title = "回应开始时间", description = "回应开始时间", maxLength=23, nullable=true )
    private java.util.Date responseStartDate;

    /**
     * 回应结束时间
     */
    @ColumnMeta(columnName="response_end_date", dataType="java.util.Date", dataSize=23, nullable=true)
    @Schema(title = "回应结束时间", description = "回应结束时间", maxLength=23, nullable=true )
    private java.util.Date responseEndDate;

    /**
     * 状态
     */
    @ColumnMeta(columnName="state", dataType="int", dataSize=10, nullable=true)
    @Schema(title = "状态", description = "状态", maxLength=10, nullable=true )
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
    public String ENTITY_TABLE(){
        return "ai_session_msg";
    }

    /**
     * 获得实体的表注释。
     */
    @Override
    public String ENTITY_NAME(){
        return "session消息";
    }

    /**
     * 获得主键
     */
    @Override
    public Serializable ENTITY_ID(){
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
     * 获取sessionId。
     */
    public long getSessionId(){
        return this.sessionId;
    }

    /**
     * 获取系统提问。
     */
    public String getSystemPrompt(){
        return this.systemPrompt;
    }

    /**
     * 获取用户提问。
     */
    public String getUserPrompt(){
        return this.userPrompt;
    }

    /**
     * 获取上下文数据。
     */
    public String getContextData(){
        return this.contextData;
    }

    /**
     * 获取工具信息。
     */
    public String getToolConfig(){
        return this.toolConfig;
    }

    /**
     * 获取文件信息。
     */
    public String getFileConfig(){
        return this.fileConfig;
    }

    /**
     * 获取rag信息。
     */
    public String getRagConfig(){
        return this.ragConfig;
    }

    /**
     * 获取返回信息。
     */
    public String getResponseInfo(){
        return this.responseInfo;
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
     * 获取创建时间。
     */
    public java.util.Date getRequestDate(){
        return this.requestDate;
    }

    /**
     * 获取回应开始时间。
     */
    public java.util.Date getResponseStartDate(){
        return this.responseStartDate;
    }

    /**
     * 获取回应结束时间。
     */
    public java.util.Date getResponseEndDate(){
        return this.responseEndDate;
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
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "id", this.id, id, !_IS_LOADED );
        this.id = id;
    }

    /**
     *  设置ID链式调用。
     */
    public AiSessionMsg id(long id){
        setId(id);
        return this;
    }

    /**
     * 设置saasId。
     */
    public void setSaasId(long saasId){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "saasId", this.saasId, saasId, !_IS_LOADED );
        this.saasId = saasId;
    }

    /**
     *  设置saasId链式调用。
     */
    public AiSessionMsg saasId(long saasId){
        setSaasId(saasId);
        return this;
    }

    /**
     * 设置sessionId。
     */
    public void setSessionId(long sessionId){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "sessionId", this.sessionId, sessionId, !_IS_LOADED );
        this.sessionId = sessionId;
    }

    /**
     *  设置sessionId链式调用。
     */
    public AiSessionMsg sessionId(long sessionId){
        setSessionId(sessionId);
        return this;
    }

    /**
     * 设置系统提问。
     */
    public void setSystemPrompt(String systemPrompt){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "systemPrompt", this.systemPrompt, systemPrompt, !_IS_LOADED );
        this.systemPrompt = systemPrompt;
    }

    /**
     *  设置系统提问链式调用。
     */
    public AiSessionMsg systemPrompt(String systemPrompt){
        setSystemPrompt(systemPrompt);
        return this;
    }

    /**
     * 设置用户提问。
     */
    public void setUserPrompt(String userPrompt){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "userPrompt", this.userPrompt, userPrompt, !_IS_LOADED );
        this.userPrompt = userPrompt;
    }

    /**
     *  设置用户提问链式调用。
     */
    public AiSessionMsg userPrompt(String userPrompt){
        setUserPrompt(userPrompt);
        return this;
    }

    /**
     * 设置上下文数据。
     */
    public void setContextData(String contextData){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "contextData", this.contextData, contextData, !_IS_LOADED );
        this.contextData = contextData;
    }

    /**
     *  设置上下文数据链式调用。
     */
    public AiSessionMsg contextData(String contextData){
        setContextData(contextData);
        return this;
    }

    /**
     * 设置工具信息。
     */
    public void setToolConfig(String toolConfig){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "toolConfig", this.toolConfig, toolConfig, !_IS_LOADED );
        this.toolConfig = toolConfig;
    }

    /**
     *  设置工具信息链式调用。
     */
    public AiSessionMsg toolConfig(String toolConfig){
        setToolConfig(toolConfig);
        return this;
    }

    /**
     * 设置文件信息。
     */
    public void setFileConfig(String fileConfig){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "fileConfig", this.fileConfig, fileConfig, !_IS_LOADED );
        this.fileConfig = fileConfig;
    }

    /**
     *  设置文件信息链式调用。
     */
    public AiSessionMsg fileConfig(String fileConfig){
        setFileConfig(fileConfig);
        return this;
    }

    /**
     * 设置rag信息。
     */
    public void setRagConfig(String ragConfig){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "ragConfig", this.ragConfig, ragConfig, !_IS_LOADED );
        this.ragConfig = ragConfig;
    }

    /**
     *  设置rag信息链式调用。
     */
    public AiSessionMsg ragConfig(String ragConfig){
        setRagConfig(ragConfig);
        return this;
    }

    /**
     * 设置返回信息。
     */
    public void setResponseInfo(String responseInfo){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "responseInfo", this.responseInfo, responseInfo, !_IS_LOADED );
        this.responseInfo = responseInfo;
    }

    /**
     *  设置返回信息链式调用。
     */
    public AiSessionMsg responseInfo(String responseInfo){
        setResponseInfo(responseInfo);
        return this;
    }

    /**
     * 设置请求token数。
     */
    public void setRequestTokens(long requestTokens){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "requestTokens", this.requestTokens, requestTokens, !_IS_LOADED );
        this.requestTokens = requestTokens;
    }

    /**
     *  设置请求token数链式调用。
     */
    public AiSessionMsg requestTokens(long requestTokens){
        setRequestTokens(requestTokens);
        return this;
    }

    /**
     * 设置响应token数。
     */
    public void setResponseTokens(long responseTokens){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "responseTokens", this.responseTokens, responseTokens, !_IS_LOADED );
        this.responseTokens = responseTokens;
    }

    /**
     *  设置响应token数链式调用。
     */
    public AiSessionMsg responseTokens(long responseTokens){
        setResponseTokens(responseTokens);
        return this;
    }

    /**
     * 设置创建时间。
     */
    public void setRequestDate(java.util.Date requestDate){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "requestDate", this.requestDate, requestDate, !_IS_LOADED );
        this.requestDate = requestDate;
    }

    /**
     *  设置创建时间链式调用。
     */
    public AiSessionMsg requestDate(java.util.Date requestDate){
        setRequestDate(requestDate);
        return this;
    }

    /**
     * 设置回应开始时间。
     */
    public void setResponseStartDate(java.util.Date responseStartDate){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "responseStartDate", this.responseStartDate, responseStartDate, !_IS_LOADED );
        this.responseStartDate = responseStartDate;
    }

    /**
     *  设置回应开始时间链式调用。
     */
    public AiSessionMsg responseStartDate(java.util.Date responseStartDate){
        setResponseStartDate(responseStartDate);
        return this;
    }

    /**
     * 设置回应结束时间。
     */
    public void setResponseEndDate(java.util.Date responseEndDate){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "responseEndDate", this.responseEndDate, responseEndDate, !_IS_LOADED );
        this.responseEndDate = responseEndDate;
    }

    /**
     *  设置回应结束时间链式调用。
     */
    public AiSessionMsg responseEndDate(java.util.Date responseEndDate){
        setResponseEndDate(responseEndDate);
        return this;
    }

    /**
     * 设置状态。
     */
    public void setState(int state){
        _UPDATED_INFO = DataUpdateInfo.addUpdateInfo(_UPDATED_INFO, "state", this.state, state, !_IS_LOADED );
        this.state = state;
    }

    /**
     *  设置状态链式调用。
     */
    public AiSessionMsg state(int state){
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