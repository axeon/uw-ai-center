package uw.ai.center.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.app.common.dto.AuthPageQueryParam;
import uw.dao.annotation.QueryMeta;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
* rag文档库列表查询参数。
*/
@Schema(title = "rag文档库列表查询参数", description = "rag文档库列表查询参数")
public class AiRagLibQueryParam extends AuthPageQueryParam{

    public AiRagLibQueryParam() {
        super();
    }

    public AiRagLibQueryParam(Long saasId) {
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
            put( "libType", "lib_type" );
            put( "libName", "lib_name" );
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
    * 文档库类型。
    */
    @QueryMeta(expr = "lib_type=?")
    @Schema(title="文档库类型", description = "文档库类型")
    private Integer libType;
	
    /**
    * 文档库名称。
    */
    @QueryMeta(expr = "lib_name like ?")
    @Schema(title="文档库名称", description = "文档库名称")
    private String libName;
	
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
    public AiRagLibQueryParam id(Long id) {
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
    public AiRagLibQueryParam ids(Long[] ids) {
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
	public AiRagLibQueryParam userId(Long userId){
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
	public AiRagLibQueryParam userType(Integer userType){
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
    public AiRagLibQueryParam userInfo(String userInfo) {
        setUserInfo(userInfo);
        return this;
    }
	
    /**
    * 获取文档库类型。
    */
    public Integer getLibType(){
        return this.libType;
    }

    /**
    * 设置文档库类型。
    */
    public void setLibType(Integer libType){
        this.libType = libType;
    }
	
    /**
    * 设置文档库类型链式调用。
    */
	public AiRagLibQueryParam libType(Integer libType){
        setLibType(libType);
        return this;
    }
	
    /**
    * 获取文档库名称。
    */
    public String getLibName(){
        return this.libName;
    }

    /**
    * 设置文档库名称。
    */
    public void setLibName(String libName){
        this.libName = libName;
    }
	
    /**
    * 设置文档库名称链式调用。
    */
    public AiRagLibQueryParam libName(String libName) {
        setLibName(libName);
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
    public AiRagLibQueryParam createDateRange(Date[] createDateRange) {
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
    public AiRagLibQueryParam modifyDateRange(Date[] modifyDateRange) {
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
    public AiRagLibQueryParam state(Integer state) {
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
    public AiRagLibQueryParam states(Integer[] states) {
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
    public AiRagLibQueryParam stateGte(Integer stateGte) {
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
    public AiRagLibQueryParam stateLte(Integer stateLte) {
        setStateLte(stateLte);
        return this;
    }
    

}