package uw.ai.center.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.dao.PageQueryParam;
import uw.dao.annotation.QueryMeta;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
* AI服务模型列表查询参数。
*/
@Schema(title = "AI服务模型列表查询参数", description = "AI服务模型列表查询参数")
public class AiVendorModelQueryParam extends PageQueryParam{

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
            put( "vendorId", "vendor_id" );
            put( "modelCode", "model_code" );
            put( "modelName", "model_name" );
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
    * 服务商ID。
    */
    @QueryMeta(expr = "vendor_id=?")
    @Schema(title="服务商ID", description = "服务商ID")
    private Long vendorId;
	
    /**
    * 服务商代码。
    */
    @QueryMeta(expr = "model_code like ?")
    @Schema(title="服务商代码", description = "服务商代码")
    private String modelCode;
	
    /**
    * 服务商名称。
    */
    @QueryMeta(expr = "model_name like ?")
    @Schema(title="服务商名称", description = "服务商名称")
    private String modelName;
	
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
	public AiVendorModelQueryParam id(Long id){
        setId(id);
        return this;
    }
	
    /**
    * 获取服务商ID。
    */
    public Long getVendorId(){
        return this.vendorId;
    }

    /**
    * 设置服务商ID。
    */
    public void setVendorId(Long vendorId){
        this.vendorId = vendorId;
    }
	
    /**
    * 设置服务商ID链式调用。
    */
	public AiVendorModelQueryParam vendorId(Long vendorId){
        setVendorId(vendorId);
        return this;
    }
	
    /**
    * 获取服务商代码。
    */
    public String getModelCode(){
        return this.modelCode;
    }

    /**
    * 设置服务商代码。
    */
    public void setModelCode(String modelCode){
        this.modelCode = modelCode;
    }
	
    /**
    * 设置服务商代码链式调用。
    */
    public AiVendorModelQueryParam modelCode(String modelCode) {
        setModelCode(modelCode);
        return this;
    }
	
    /**
    * 获取服务商名称。
    */
    public String getModelName(){
        return this.modelName;
    }

    /**
    * 设置服务商名称。
    */
    public void setModelName(String modelName){
        this.modelName = modelName;
    }
	
    /**
    * 设置服务商名称链式调用。
    */
    public AiVendorModelQueryParam modelName(String modelName) {
        setModelName(modelName);
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
    public AiVendorModelQueryParam createDateRange(Date[] createDateRange) {
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
    public AiVendorModelQueryParam modifyDateRange(Date[] modifyDateRange) {
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
    public AiVendorModelQueryParam state(Integer state) {
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
    public AiVendorModelQueryParam states(Integer[] states) {
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
    public AiVendorModelQueryParam stateGte(Integer stateGte) {
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
    public AiVendorModelQueryParam stateLte(Integer stateLte) {
        setStateLte(stateLte);
        return this;
    }
    

}