package uw.ai.center.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.app.common.dto.AuthPageQueryParam;
import uw.dao.annotation.QueryMeta;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
* session信息列表查询参数。
*/
@Schema(title = "session信息列表查询参数", description = "session信息列表查询参数")
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
            put( "mchId", "mch_id" );
            put( "userId", "user_id" );
            put( "userType", "user_type" );
            put( "groupId", "group_id" );
            put( "userName", "user_name" );
            put( "nickName", "nick_name" );
            put( "realName", "real_name" );
            put( "sessionName", "session_name" );
            put( "createDate", "create_date" );
            put( "modifyDate", "modify_date" );
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
    * 商户ID。
    */
    @QueryMeta(expr = "mch_id=?")
    @Schema(title="商户ID", description = "商户ID")
    private Long mchId;
	
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
    * 用户组ID。
    */
    @QueryMeta(expr = "group_id=?")
    @Schema(title="用户组ID", description = "用户组ID")
    private Long groupId;
	
    /**
    * 用户名。
    */
    @QueryMeta(expr = "user_name like ?")
    @Schema(title="用户名", description = "用户名")
    private String userName;
	
    /**
    * 用户昵称。
    */
    @QueryMeta(expr = "nick_name like ?")
    @Schema(title="用户昵称", description = "用户昵称")
    private String nickName;
	
    /**
    * 真实名称。
    */
    @QueryMeta(expr = "real_name like ?")
    @Schema(title="真实名称", description = "真实名称")
    private String realName;
	
    /**
    * session名称。
    */
    @QueryMeta(expr = "session_name like ?")
    @Schema(title="session名称", description = "session名称")
    private String sessionName;
	
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
	public AiSessionInfoQueryParam id(Long id){
        setId(id);
        return this;
    }
	
    /**
    * 获取商户ID。
    */
    public Long getMchId(){
        return this.mchId;
    }

    /**
    * 设置商户ID。
    */
    public void setMchId(Long mchId){
        this.mchId = mchId;
    }
	
    /**
    * 设置商户ID链式调用。
    */
	public AiSessionInfoQueryParam mchId(Long mchId){
        setMchId(mchId);
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
    * 获取用户组ID。
    */
    public Long getGroupId(){
        return this.groupId;
    }

    /**
    * 设置用户组ID。
    */
    public void setGroupId(Long groupId){
        this.groupId = groupId;
    }
	
    /**
    * 设置用户组ID链式调用。
    */
	public AiSessionInfoQueryParam groupId(Long groupId){
        setGroupId(groupId);
        return this;
    }
	
    /**
    * 获取用户名。
    */
    public String getUserName(){
        return this.userName;
    }

    /**
    * 设置用户名。
    */
    public void setUserName(String userName){
        this.userName = userName;
    }
	
    /**
    * 设置用户名链式调用。
    */
    public AiSessionInfoQueryParam userName(String userName) {
        setUserName(userName);
        return this;
    }
	
    /**
    * 获取用户昵称。
    */
    public String getNickName(){
        return this.nickName;
    }

    /**
    * 设置用户昵称。
    */
    public void setNickName(String nickName){
        this.nickName = nickName;
    }
	
    /**
    * 设置用户昵称链式调用。
    */
    public AiSessionInfoQueryParam nickName(String nickName) {
        setNickName(nickName);
        return this;
    }
	
    /**
    * 获取真实名称。
    */
    public String getRealName(){
        return this.realName;
    }

    /**
    * 设置真实名称。
    */
    public void setRealName(String realName){
        this.realName = realName;
    }
	
    /**
    * 设置真实名称链式调用。
    */
    public AiSessionInfoQueryParam realName(String realName) {
        setRealName(realName);
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