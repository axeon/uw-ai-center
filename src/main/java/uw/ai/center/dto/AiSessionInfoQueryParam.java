package uw.ai.center.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.app.common.dto.AuthPageQueryParam;
import uw.dao.annotation.QueryMeta;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
* session会话列表查询参数。
*/
@Schema(title = "session会话列表查询参数", description = "session会话列表查询参数")
public class AiSessionInfoQueryParam extends AuthPageQueryParam{

    public AiSessionInfoQueryParam() {
        super();
    }

    public AiSessionInfoQueryParam(Long saasId) {
        super(saasId);
    }
	
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
            put( "saasId", "saas_id" );
            put( "userId", "user_id" );
            put( "userType", "user_type" );
            put( "userInfo", "user_info" );
            put( "configId", "config_id" );
            put( "sessionType", "session_type" );
            put( "sessionName", "session_name" );
            put( "msgNum", "msg_num" );
            put( "windowSize", "window_size" );
            put( "requestTokens", "request_tokens" );
            put( "responseTokens", "response_tokens" );
            put( "createDate", "create_date" );
            put( "modifyDate", "modify_date" );
            put( "lastUpdate", "last_update" );
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
    * 数组ID。
    */
    @QueryMeta(expr = "id in (?)")
    @Schema(title="数组ID", description = "ID数组，可同时匹配多个。")
    private Long[] ids;

    /**
    * 用户id。
    */
    @QueryMeta(expr = "user_id=?")
    @Schema(title="用户id", description = "用户id")
    private Long userId;
	
    /**
    * 用户类型。
    */
    @QueryMeta(expr = "user_type=?")
    @Schema(title="用户类型", description = "用户类型")
    private Integer userType;
	
    /**
    * 用户名。
    */
    @QueryMeta(expr = "user_info like ?")
    @Schema(title="用户名", description = "用户名")
    private String userInfo;
	
    /**
    * 配置ID。
    */
    @QueryMeta(expr = "config_id=?")
    @Schema(title="配置ID", description = "配置ID")
    private Long configId;
	
    /**
    * session类型。
    */
    @QueryMeta(expr = "session_type=?")
    @Schema(title="session类型", description = "session类型")
    private Integer sessionType;
	
    /**
    * session名称。
    */
    @QueryMeta(expr = "session_name like ?")
    @Schema(title="session名称", description = "session名称")
    private String sessionName;
	
    /**
    * session大小。
    */
    @QueryMeta(expr = "msg_num=?")
    @Schema(title="session大小", description = "session大小")
    private Integer msgNum;

    /**
    * session大小范围。
    */
    @QueryMeta(expr = "msg_num between ? and ?")
    @Schema(title="session大小范围", description = "session大小范围")
    private Integer[] msgNumRange;
	
    /**
    * 历史长度。
    */
    @QueryMeta(expr = "window_size=?")
    @Schema(title="历史长度", description = "历史长度")
    private Integer windowSize;

    /**
    * 历史长度范围。
    */
    @QueryMeta(expr = "window_size between ? and ?")
    @Schema(title="历史长度范围", description = "历史长度范围")
    private Integer[] windowSizeRange;
	
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
    @QueryMeta(expr = "create_date between ? and ?")
    @Schema(title="创建时间范围", description = "创建时间范围")
    private Date[] createDateRange;

    /**
    * 修改时间范围。
    */
    @QueryMeta(expr = "modify_date between ? and ?")
    @Schema(title="修改时间范围", description = "修改时间范围")
    private Date[] modifyDateRange;

    /**
    * 最后更新时间范围。
    */
    @QueryMeta(expr = "last_update between ? and ?")
    @Schema(title="最后更新时间范围", description = "最后更新时间范围")
    private Date[] lastUpdateRange;

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
    public Long getId() {
        return this.id;
    }

    /**
    * 设置ID。
    */
    public void setId(Long id) {
        this.id = id;
    }

    /**
    * 设置ID链式调用。
    */
    public AiSessionInfoQueryParam id(Long id) {
        setId(id);
        return this;
    }

    /**
    * 获取数组ID。
    */
    public Long[] getIds() {
        return this.ids;
    }

    /**
    * 设置数组ID。
    */
    public void setIds(Long[] ids) {
        this.ids = ids;
    }

    /**
    * 设置数组ID链式调用。
    */
    public AiSessionInfoQueryParam ids(Long[] ids) {
        setIds(ids);
        return this;
    }

    /**
    * 获取用户id。
    */
    public Long getUserId(){
        return this.userId;
    }

    /**
    * 设置用户id。
    */
    public void setUserId(Long userId){
        this.userId = userId;
    }
	
    /**
    * 设置用户id链式调用。
    */
	public AiSessionInfoQueryParam userId(Long userId){
        setUserId(userId);
        return this;
    }
	
    /**
    * 获取用户类型。
    */
    public Integer getUserType(){
        return this.userType;
    }

    /**
    * 设置用户类型。
    */
    public void setUserType(Integer userType){
        this.userType = userType;
    }
	
    /**
    * 设置用户类型链式调用。
    */
	public AiSessionInfoQueryParam userType(Integer userType){
        setUserType(userType);
        return this;
    }
	
    /**
    * 获取用户名。
    */
    public String getUserInfo(){
        return this.userInfo;
    }

    /**
    * 设置用户名。
    */
    public void setUserInfo(String userInfo){
        this.userInfo = userInfo;
    }
	
    /**
    * 设置用户名链式调用。
    */
    public AiSessionInfoQueryParam userInfo(String userInfo) {
        setUserInfo(userInfo);
        return this;
    }
	
    /**
    * 获取配置ID。
    */
    public Long getConfigId(){
        return this.configId;
    }

    /**
    * 设置配置ID。
    */
    public void setConfigId(Long configId){
        this.configId = configId;
    }
	
    /**
    * 设置配置ID链式调用。
    */
	public AiSessionInfoQueryParam configId(Long configId){
        setConfigId(configId);
        return this;
    }
	
    /**
    * 获取session类型。
    */
    public Integer getSessionType(){
        return this.sessionType;
    }

    /**
    * 设置session类型。
    */
    public void setSessionType(Integer sessionType){
        this.sessionType = sessionType;
    }
	
    /**
    * 设置session类型链式调用。
    */
	public AiSessionInfoQueryParam sessionType(Integer sessionType){
        setSessionType(sessionType);
        return this;
    }
	
    /**
    * 获取session名称。
    */
    public String getSessionName(){
        return this.sessionName;
    }

    /**
    * 设置session名称。
    */
    public void setSessionName(String sessionName){
        this.sessionName = sessionName;
    }
	
    /**
    * 设置session名称链式调用。
    */
    public AiSessionInfoQueryParam sessionName(String sessionName) {
        setSessionName(sessionName);
        return this;
    }
	
    /**
    * 获取session大小。
    */
    public Integer getMsgNum(){
        return this.msgNum;
    }

    /**
    * 设置session大小。
    */
    public void setMsgNum(Integer msgNum){
        this.msgNum = msgNum;
    }
	
    /**
    * 设置session大小链式调用。
    */
    public AiSessionInfoQueryParam msgNum(Integer msgNum){
        setMsgNum(msgNum);
        return this;
    }

    /**
    * 获取session大小范围。
    */
    public Integer[] getMsgNumRange(){
        return this.msgNumRange;
    }

    /**
    * 设置session大小范围。
    */
    public void setMsgNumRange(Integer[] msgNumRange){
        this.msgNumRange = msgNumRange;
    }
	
    /**
    * 设置session大小范围链式调用。
    */
    public AiSessionInfoQueryParam msgNumRange(Integer[] msgNumRange){
        setMsgNumRange(msgNumRange);
        return this;
    }
	
    /**
    * 获取历史长度。
    */
    public Integer getWindowSize(){
        return this.windowSize;
    }

    /**
    * 设置历史长度。
    */
    public void setWindowSize(Integer windowSize){
        this.windowSize = windowSize;
    }
	
    /**
    * 设置历史长度链式调用。
    */
    public AiSessionInfoQueryParam windowSize(Integer windowSize){
        setWindowSize(windowSize);
        return this;
    }

    /**
    * 获取历史长度范围。
    */
    public Integer[] getWindowSizeRange(){
        return this.windowSizeRange;
    }

    /**
    * 设置历史长度范围。
    */
    public void setWindowSizeRange(Integer[] windowSizeRange){
        this.windowSizeRange = windowSizeRange;
    }
	
    /**
    * 设置历史长度范围链式调用。
    */
    public AiSessionInfoQueryParam windowSizeRange(Integer[] windowSizeRange){
        setWindowSizeRange(windowSizeRange);
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
    public AiSessionInfoQueryParam requestTokens(Long requestTokens){
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
    public AiSessionInfoQueryParam requestTokensRange(Long[] requestTokensRange){
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
    public AiSessionInfoQueryParam responseTokens(Long responseTokens){
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
    public AiSessionInfoQueryParam responseTokensRange(Long[] responseTokensRange){
        setResponseTokensRange(responseTokensRange);
        return this;
    }
	
    /**
    * 获取创建时间范围。
    */
    public Date[] getCreateDateRange(){
        return this.createDateRange;
    }

    /**
    * 设置创建时间范围。
    */
    public void setCreateDateRange(Date[] createDateRange){
        this.createDateRange = createDateRange;
    }
	
    /**
    * 设置创建时间范围链式调用。
    */
    public AiSessionInfoQueryParam createDateRange(Date[] createDateRange) {
        setCreateDateRange(createDateRange);
        return this;
    }
	
    /**
    * 获取修改时间范围。
    */
    public Date[] getModifyDateRange(){
        return this.modifyDateRange;
    }

    /**
    * 设置修改时间范围。
    */
    public void setModifyDateRange(Date[] modifyDateRange){
        this.modifyDateRange = modifyDateRange;
    }
	
    /**
    * 设置修改时间范围链式调用。
    */
    public AiSessionInfoQueryParam modifyDateRange(Date[] modifyDateRange) {
        setModifyDateRange(modifyDateRange);
        return this;
    }
	
    /**
    * 获取最后更新时间范围。
    */
    public Date[] getLastUpdateRange(){
        return this.lastUpdateRange;
    }

    /**
    * 设置最后更新时间范围。
    */
    public void setLastUpdateRange(Date[] lastUpdateRange){
        this.lastUpdateRange = lastUpdateRange;
    }
	
    /**
    * 设置最后更新时间范围链式调用。
    */
    public AiSessionInfoQueryParam lastUpdateRange(Date[] lastUpdateRange) {
        setLastUpdateRange(lastUpdateRange);
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
    public AiSessionInfoQueryParam state(Integer state) {
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
    public AiSessionInfoQueryParam states(Integer[] states) {
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
    public AiSessionInfoQueryParam stateGte(Integer stateGte) {
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
    public AiSessionInfoQueryParam stateLte(Integer stateLte) {
        setStateLte(stateLte);
        return this;
    }
    

}