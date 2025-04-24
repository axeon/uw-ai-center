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
        this._UPDATED_INFO = new StringBuilder("表ai_session_msg主键\"" + 
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
    public AiSessionMsg id(long id){
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
    public AiSessionMsg saasId(long saasId){
        setSaasId(saasId);
        return this;
        }

    /**
     * 设置sessionId。
     */
    public void setSessionId(long sessionId){
        if (!_IS_LOADED||!Objects.equals(this.sessionId, sessionId)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("session_id");
            this._UPDATED_INFO.append("session_id:\"").append(this.sessionId).append("\"=>\"").append(sessionId).append("\"\n");
            this.sessionId = sessionId;
        }
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
        if (!_IS_LOADED||!Objects.equals(this.systemPrompt, systemPrompt)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("system_prompt");
            this._UPDATED_INFO.append("system_prompt:\"").append(this.systemPrompt).append("\"=>\"").append(systemPrompt).append("\"\n");
            this.systemPrompt = systemPrompt;
        }
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
        if (!_IS_LOADED||!Objects.equals(this.userPrompt, userPrompt)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("user_prompt");
            this._UPDATED_INFO.append("user_prompt:\"").append(this.userPrompt).append("\"=>\"").append(userPrompt).append("\"\n");
            this.userPrompt = userPrompt;
        }
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
        if (!_IS_LOADED||!Objects.equals(this.contextData, contextData)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("context_data");
            this._UPDATED_INFO.append("context_data:\"").append(this.contextData).append("\"=>\"").append(contextData).append("\"\n");
            this.contextData = contextData;
        }
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
        if (!_IS_LOADED||!Objects.equals(this.toolConfig, toolConfig)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("tool_config");
            this._UPDATED_INFO.append("tool_config:\"").append(this.toolConfig).append("\"=>\"").append(toolConfig).append("\"\n");
            this.toolConfig = toolConfig;
        }
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
        if (!_IS_LOADED||!Objects.equals(this.fileConfig, fileConfig)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("file_config");
            this._UPDATED_INFO.append("file_config:\"").append(this.fileConfig).append("\"=>\"").append(fileConfig).append("\"\n");
            this.fileConfig = fileConfig;
        }
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
        if (!_IS_LOADED||!Objects.equals(this.ragConfig, ragConfig)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("rag_config");
            this._UPDATED_INFO.append("rag_config:\"").append(this.ragConfig).append("\"=>\"").append(ragConfig).append("\"\n");
            this.ragConfig = ragConfig;
        }
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
        if (!_IS_LOADED||!Objects.equals(this.responseInfo, responseInfo)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("response_info");
            this._UPDATED_INFO.append("response_info:\"").append(this.responseInfo).append("\"=>\"").append(responseInfo).append("\"\n");
            this.responseInfo = responseInfo;
        }
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
        if (!_IS_LOADED||!Objects.equals(this.requestTokens, requestTokens)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("request_tokens");
            this._UPDATED_INFO.append("request_tokens:\"").append(this.requestTokens).append("\"=>\"").append(requestTokens).append("\"\n");
            this.requestTokens = requestTokens;
        }
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
        if (!_IS_LOADED||!Objects.equals(this.responseTokens, responseTokens)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("response_tokens");
            this._UPDATED_INFO.append("response_tokens:\"").append(this.responseTokens).append("\"=>\"").append(responseTokens).append("\"\n");
            this.responseTokens = responseTokens;
        }
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
        if (!_IS_LOADED||!Objects.equals(this.requestDate, requestDate)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("request_date");
            this._UPDATED_INFO.append("request_date:\"").append(this.requestDate).append("\"=>\"").append(requestDate).append("\"\n");
            this.requestDate = requestDate;
        }
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
        if (!_IS_LOADED||!Objects.equals(this.responseStartDate, responseStartDate)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("response_start_date");
            this._UPDATED_INFO.append("response_start_date:\"").append(this.responseStartDate).append("\"=>\"").append(responseStartDate).append("\"\n");
            this.responseStartDate = responseStartDate;
        }
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
        if (!_IS_LOADED||!Objects.equals(this.responseEndDate, responseEndDate)){
            if (this._UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this._UPDATED_COLUMN.add("response_end_date");
            this._UPDATED_INFO.append("response_end_date:\"").append(this.responseEndDate).append("\"=>\"").append(responseEndDate).append("\"\n");
            this.responseEndDate = responseEndDate;
        }
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
    public AiSessionMsg state(int state){
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
        sb.append("session_id:\"" + this.sessionId + "\"\r\n");
        sb.append("system_prompt:\"" + this.systemPrompt + "\"\r\n");
        sb.append("user_prompt:\"" + this.userPrompt + "\"\r\n");
        sb.append("context_data:\"" + this.contextData + "\"\r\n");
        sb.append("tool_config:\"" + this.toolConfig + "\"\r\n");
        sb.append("file_config:\"" + this.fileConfig + "\"\r\n");
        sb.append("rag_config:\"" + this.ragConfig + "\"\r\n");
        sb.append("response_info:\"" + this.responseInfo + "\"\r\n");
        sb.append("request_tokens:\"" + this.requestTokens + "\"\r\n");
        sb.append("response_tokens:\"" + this.responseTokens + "\"\r\n");
        sb.append("request_date:\"" + this.requestDate + "\"\r\n");
        sb.append("response_start_date:\"" + this.responseStartDate + "\"\r\n");
        sb.append("response_end_date:\"" + this.responseEndDate + "\"\r\n");
        sb.append("state:\"" + this.state + "\"\r\n");
        return sb.toString();
    }

}