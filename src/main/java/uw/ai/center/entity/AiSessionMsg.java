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
     * 设置系统提问。
     */
    public void setSystemPrompt(String systemPrompt){
        if (!Objects.equals(this.systemPrompt, systemPrompt)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("system_prompt");
            this.UPDATED_INFO.append("system_prompt:\"" + this.systemPrompt+ "\"=>\"" + systemPrompt + "\"\r\n");
            this.systemPrompt = systemPrompt;
        }
    }

    /**
     * 设置用户提问。
     */
    public void setUserPrompt(String userPrompt){
        if (!Objects.equals(this.userPrompt, userPrompt)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("user_prompt");
            this.UPDATED_INFO.append("user_prompt:\"" + this.userPrompt+ "\"=>\"" + userPrompt + "\"\r\n");
            this.userPrompt = userPrompt;
        }
    }

    /**
     * 设置上下文数据。
     */
    public void setContextData(String contextData){
        if (!Objects.equals(this.contextData, contextData)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("context_data");
            this.UPDATED_INFO.append("context_data:\"" + this.contextData+ "\"=>\"" + contextData + "\"\r\n");
            this.contextData = contextData;
        }
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
            this.UPDATED_INFO.append("tool_config:\"" + this.toolConfig+ "\"=>\"" + toolConfig + "\"\r\n");
            this.toolConfig = toolConfig;
        }
    }

    /**
     * 设置文件信息。
     */
    public void setFileConfig(String fileConfig){
        if (!Objects.equals(this.fileConfig, fileConfig)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("file_config");
            this.UPDATED_INFO.append("file_config:\"" + this.fileConfig+ "\"=>\"" + fileConfig + "\"\r\n");
            this.fileConfig = fileConfig;
        }
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
            this.UPDATED_INFO.append("rag_config:\"" + this.ragConfig+ "\"=>\"" + ragConfig + "\"\r\n");
            this.ragConfig = ragConfig;
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
     * 设置创建时间。
     */
    public void setRequestDate(java.util.Date requestDate){
        if (!Objects.equals(this.requestDate, requestDate)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("request_date");
            this.UPDATED_INFO.append("request_date:\"" + this.requestDate+ "\"=>\"" + requestDate + "\"\r\n");
            this.requestDate = requestDate;
        }
    }

    /**
     * 设置回应开始时间。
     */
    public void setResponseStartDate(java.util.Date responseStartDate){
        if (!Objects.equals(this.responseStartDate, responseStartDate)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("response_start_date");
            this.UPDATED_INFO.append("response_start_date:\"" + this.responseStartDate+ "\"=>\"" + responseStartDate + "\"\r\n");
            this.responseStartDate = responseStartDate;
        }
    }

    /**
     * 设置回应结束时间。
     */
    public void setResponseEndDate(java.util.Date responseEndDate){
        if (!Objects.equals(this.responseEndDate, responseEndDate)){
            if (this.UPDATED_COLUMN == null) {
                _INIT_UPDATE_INFO();
            }
            this.UPDATED_COLUMN.add("response_end_date");
            this.UPDATED_INFO.append("response_end_date:\"" + this.responseEndDate+ "\"=>\"" + responseEndDate + "\"\r\n");
            this.responseEndDate = responseEndDate;
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