package uw.ai.center.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.dao.PageQueryParam;
import uw.dao.annotation.QueryMeta;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
* AI工具配置列表查询参数。
*/
@Schema(title = "AI工具配置列表查询参数", description = "AI工具配置列表查询参数")
public class AiToolConfigQueryParam extends PageQueryParam{

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
            put( "appName", "app_name" );
            put( "toolCode", "tool_code" );
            put( "toolVersion", "tool_version" );
            put( "toolName", "tool_name" );
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
    * 应用名。
    */
    @QueryMeta(expr = "app_name like ?")
    @Schema(title="应用名", description = "应用名")
    private String appName;
	
    /**
    * 工具代码。
    */
    @QueryMeta(expr = "tool_code like ?")
    @Schema(title="工具代码", description = "工具代码")
    private String toolCode;
	
    /**
    * 工具版本。
    */
    @QueryMeta(expr = "tool_version like ?")
    @Schema(title="工具版本", description = "工具版本")
    private String toolVersion;
	
    /**
    * 工具名称。
    */
    @QueryMeta(expr = "tool_name like ?")
    @Schema(title="工具名称", description = "工具名称")
    private String toolName;
	
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
	public AiToolConfigQueryParam id(Long id){
        setId(id);
        return this;
    }
	
    /**
    * 获取应用名。
    */
    public String getAppName(){
        return this.appName;
    }

    /**
    * 设置应用名。
    */
    public void setAppName(String appName){
        this.appName = appName;
    }
	
    /**
    * 设置应用名链式调用。
    */
    public AiToolConfigQueryParam appName(String appName) {
        setAppName(appName);
        return this;
    }
	
    /**
    * 获取工具代码。
    */
    public String getToolCode(){
        return this.toolCode;
    }

    /**
    * 设置工具代码。
    */
    public void setToolCode(String toolCode){
        this.toolCode = toolCode;
    }
	
    /**
    * 设置工具代码链式调用。
    */
    public AiToolConfigQueryParam toolCode(String toolCode) {
        setToolCode(toolCode);
        return this;
    }
	
    /**
    * 获取工具版本。
    */
    public String getToolVersion(){
        return this.toolVersion;
    }

    /**
    * 设置工具版本。
    */
    public void setToolVersion(String toolVersion){
        this.toolVersion = toolVersion;
    }
	
    /**
    * 设置工具版本链式调用。
    */
    public AiToolConfigQueryParam toolVersion(String toolVersion) {
        setToolVersion(toolVersion);
        return this;
    }
	
    /**
    * 获取工具名称。
    */
    public String getToolName(){
        return this.toolName;
    }

    /**
    * 设置工具名称。
    */
    public void setToolName(String toolName){
        this.toolName = toolName;
    }
	
    /**
    * 设置工具名称链式调用。
    */
    public AiToolConfigQueryParam toolName(String toolName) {
        setToolName(toolName);
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
    public AiToolConfigQueryParam createDateRange(Date[] createDateRange) {
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
    public AiToolConfigQueryParam modifyDateRange(Date[] modifyDateRange) {
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
    public AiToolConfigQueryParam state(Integer state) {
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
    public AiToolConfigQueryParam states(Integer[] states) {
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
    public AiToolConfigQueryParam stateGte(Integer stateGte) {
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
    public AiToolConfigQueryParam stateLte(Integer stateLte) {
        setStateLte(stateLte);
        return this;
    }
    

}