package uw.ai.center.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.dao.PageQueryParam;
import uw.dao.annotation.QueryMeta;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
* session消息列表查询参数。
*/
@Schema(title = "session消息列表查询参数", description = "session消息列表查询参数")
public class AiSessionMsgQueryParam extends PageQueryParam{

    /**
     * 允许的排序属性。
     * key:排序名 value:排序字段
     *
     * @return
     */
    @Override
    public Map<String, String> ALLOWED_SORT_PROPERTY() {
        return new HashMap<>() {{
            put( "id", "id" );
            put( "sessionId", "session_id" );
            put( "requestTokens", "request_tokens" );
            put( "responseTokens", "response_tokens" );
            put( "requestDate", "request_date" );
            put( "responseStartDate", "response_start_date" );
            put( "responseEndDate", "response_end_date" );
            put( "state", "state" );
        }};
    }

    /**
    * ID。
    */
    @QueryMeta(expr = "id=?")
    @Schema(title="ID", description = "ID")
    private Long id;
	
    /**
    * sessionId。
    */
    @QueryMeta(expr = "session_id=?")
    @Schema(title="sessionId", description = "sessionId")
    private Long sessionId;
	
    /**
    * 请求token数。
    */
    @QueryMeta(expr = "request_tokens=?")
    @Schema(title="请求token数", description = "请求token数")
    private Long requestTokens;

    /**
    * 请求token数范围。
    */
    @QueryMeta(expr = "request_tokens between ? and ?")
    @Schema(title="请求token数范围", description = "请求token数范围")
    private Long[] requestTokensRange;
	
    /**
    * 响应token数。
    */
    @QueryMeta(expr = "response_tokens=?")
    @Schema(title="响应token数", description = "响应token数")
    private Long responseTokens;

    /**
    * 响应token数范围。
    */
    @QueryMeta(expr = "response_tokens between ? and ?")
    @Schema(title="响应token数范围", description = "响应token数范围")
    private Long[] responseTokensRange;
	
    /**
    * 创建时间范围。
    */
    @QueryMeta(expr = "request_date between ? and ?")
    @Schema(title="创建时间范围", description = "创建时间范围")
    private Date[] requestDateRange;

    /**
    * 回应开始时间范围。
    */
    @QueryMeta(expr = "response_start_date between ? and ?")
    @Schema(title="回应开始时间范围", description = "回应开始时间范围")
    private Date[] responseStartDateRange;

    /**
    * 回应结束时间范围。
    */
    @QueryMeta(expr = "response_end_date between ? and ?")
    @Schema(title="回应结束时间范围", description = "回应结束时间范围")
    private Date[] responseEndDateRange;

    /**
    * 状态。
    */
    @QueryMeta(expr = "state=?")
    @Schema(title="状态", description = "状态")
    private Integer state;

    /**
    * 数组状态。
    */
    @QueryMeta(expr = "state in (?)")
    @Schema(title="数组状态", description = "状态数组，可同时匹配多个状态。")
    private Integer[] states;

    /**
    * 大于等于状态。
    */
    @QueryMeta(expr = "state>=?")
    @Schema(title="大于等于状态", description = "大于等于状态")
    private Integer stateGte;

    /**
    * 小于等于状态。
    */
    @QueryMeta(expr = "state<=?")
    @Schema(title="小于等于状态", description = "小于等于状态")
    private Integer stateLte;


    /**
    * 获取ID。
    */
    public Long getId(){
        return this.id;
    }

    /**
    * 设置ID。
    */
    public void setId(Long id){
        this.id = id;
    }
	
    /**
    * 设置ID链式调用。
    */
	public AiSessionMsgQueryParam id(Long id){
        setId(id);
        return this;
    }
	
    /**
    * 获取sessionId。
    */
    public Long getSessionId(){
        return this.sessionId;
    }

    /**
    * 设置sessionId。
    */
    public void setSessionId(Long sessionId){
        this.sessionId = sessionId;
    }
	
    /**
    * 设置sessionId链式调用。
    */
	public AiSessionMsgQueryParam sessionId(Long sessionId){
        setSessionId(sessionId);
        return this;
    }
	
    /**
    * 获取请求token数。
    */
    public Long getRequestTokens(){
        return this.requestTokens;
    }

    /**
    * 设置请求token数。
    */
    public void setRequestTokens(Long requestTokens){
        this.requestTokens = requestTokens;
    }
	
    /**
    * 设置请求token数链式调用。
    */
    public AiSessionMsgQueryParam requestTokens(Long requestTokens){
        setRequestTokens(requestTokens);
        return this;
    }

    /**
    * 获取请求token数范围。
    */
    public Long[] getRequestTokensRange(){
        return this.requestTokensRange;
    }

    /**
    * 设置请求token数范围。
    */
    public void setRequestTokensRange(Long[] requestTokensRange){
        this.requestTokensRange = requestTokensRange;
    }
	
    /**
    * 设置请求token数范围链式调用。
    */
    public AiSessionMsgQueryParam requestTokensRange(Long[] requestTokensRange){
        setRequestTokensRange(requestTokensRange);
        return this;
    }
	
    /**
    * 获取响应token数。
    */
    public Long getResponseTokens(){
        return this.responseTokens;
    }

    /**
    * 设置响应token数。
    */
    public void setResponseTokens(Long responseTokens){
        this.responseTokens = responseTokens;
    }
	
    /**
    * 设置响应token数链式调用。
    */
    public AiSessionMsgQueryParam responseTokens(Long responseTokens){
        setResponseTokens(responseTokens);
        return this;
    }

    /**
    * 获取响应token数范围。
    */
    public Long[] getResponseTokensRange(){
        return this.responseTokensRange;
    }

    /**
    * 设置响应token数范围。
    */
    public void setResponseTokensRange(Long[] responseTokensRange){
        this.responseTokensRange = responseTokensRange;
    }
	
    /**
    * 设置响应token数范围链式调用。
    */
    public AiSessionMsgQueryParam responseTokensRange(Long[] responseTokensRange){
        setResponseTokensRange(responseTokensRange);
        return this;
    }
	
    /**
    * 获取创建时间范围。
    */
    public Date[] getRequestDateRange(){
        return this.requestDateRange;
    }

    /**
    * 设置创建时间范围。
    */
    public void setRequestDateRange(Date[] requestDateRange){
        this.requestDateRange = requestDateRange;
    }
	
    /**
    * 设置创建时间范围链式调用。
    */
    public AiSessionMsgQueryParam requestDateRange(Date[] requestDateRange) {
        setRequestDateRange(requestDateRange);
        return this;
    }
	
    /**
    * 获取回应开始时间范围。
    */
    public Date[] getResponseStartDateRange(){
        return this.responseStartDateRange;
    }

    /**
    * 设置回应开始时间范围。
    */
    public void setResponseStartDateRange(Date[] responseStartDateRange){
        this.responseStartDateRange = responseStartDateRange;
    }
	
    /**
    * 设置回应开始时间范围链式调用。
    */
    public AiSessionMsgQueryParam responseStartDateRange(Date[] responseStartDateRange) {
        setResponseStartDateRange(responseStartDateRange);
        return this;
    }
	
    /**
    * 获取回应结束时间范围。
    */
    public Date[] getResponseEndDateRange(){
        return this.responseEndDateRange;
    }

    /**
    * 设置回应结束时间范围。
    */
    public void setResponseEndDateRange(Date[] responseEndDateRange){
        this.responseEndDateRange = responseEndDateRange;
    }
	
    /**
    * 设置回应结束时间范围链式调用。
    */
    public AiSessionMsgQueryParam responseEndDateRange(Date[] responseEndDateRange) {
        setResponseEndDateRange(responseEndDateRange);
        return this;
    }
	
    /**
    * 获取状态。
    */
    public Integer getState(){
        return this.state;
    }

    /**
    * 设置状态。
    */
    public void setState(Integer state){
        this.state = state;
    }
	
    /**
    * 设置状态链式调用。
    */
    public AiSessionMsgQueryParam state(Integer state) {
        setState(state);
        return this;
    }

    /**
    * 获取数组状态。
    */
    public Integer[] getStates(){
        return this.states;
    }

    /**
    * 设置数组状态。
    */
    public void setStates(Integer[] states){
        this.states = states;
    }
	
    /**
    * 设置数组状态链式调用。
    */
    public AiSessionMsgQueryParam states(Integer[] states) {
        setStates(states);
        return this;
    }
    
    /**
    * 获取大于等于状态。
    */
    public Integer getStateGte(){
        return this.stateGte;
    }

    /**
    * 设置大于等于状态。
    */
    public void setStateGte(Integer stateGte){
        this.stateGte = stateGte;
    }
	
    /**
    * 设置大于等于状态链式调用。
    */
    public AiSessionMsgQueryParam stateGte(Integer stateGte) {
        setStateGte(stateGte);
        return this;
    }
    
    /**
    * 获取小于等于状态。
    */
    public Integer getStateLte(){
        return this.stateLte;
    }

    /**
    * 获取小于等于状态。
    */
    public void setStateLte(Integer stateLte){
        this.stateLte = stateLte;
    }
	
    /**
    * 获取小于等于状态链式调用。
    */
    public AiSessionMsgQueryParam stateLte(Integer stateLte) {
        setStateLte(stateLte);
        return this;
    }
    

}