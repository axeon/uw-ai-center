package uw.ai.center.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.app.common.dto.AuthPageQueryParam;
import uw.dao.annotation.QueryMeta;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
* AI服务模型列表查询参数。
*/
@Schema(title = "AI服务模型列表查询参数", description = "AI服务模型列表查询参数")
public class AiModelConfigQueryParam extends AuthPageQueryParam{

    public AiModelConfigQueryParam() {
        super();
    }

    public AiModelConfigQueryParam(Long saasId) {
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
            put( "vendorClass", "vendor_class" );
            put( "configCode", "config_code" );
            put( "configName", "config_name" );
            put( "apiUrl", "api_url" );
            put( "apiKey", "api_key" );
            put( "modelMain", "model_main" );
            put( "modelEmbed", "model_embed" );
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
    * 服务商类。
    */
    @QueryMeta(expr = "vendor_class like ?")
    @Schema(title="服务商类", description = "服务商类")
    private String vendorClass;
	
    /**
    * 服务商代码。
    */
    @QueryMeta(expr = "config_code like ?")
    @Schema(title="服务商代码", description = "服务商代码")
    private String configCode;
	
    /**
    * 服务商名称。
    */
    @QueryMeta(expr = "config_name like ?")
    @Schema(title="服务商名称", description = "服务商名称")
    private String configName;
	
    /**
    * api地址。
    */
    @QueryMeta(expr = "api_url like ?")
    @Schema(title="api地址", description = "api地址")
    private String apiUrl;
	
    /**
    * api key。
    */
    @QueryMeta(expr = "api_key like ?")
    @Schema(title="api key", description = "api key")
    private String apiKey;
	
    /**
    * 主模型。
    */
    @QueryMeta(expr = "model_main like ?")
    @Schema(title="主模型", description = "主模型")
    private String modelMain;
	
    /**
    * 嵌入模型。
    */
    @QueryMeta(expr = "model_embed like ?")
    @Schema(title="嵌入模型", description = "嵌入模型")
    private String modelEmbed;
	
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
	public AiModelConfigQueryParam id(Long id){
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
	public AiModelConfigQueryParam mchId(Long mchId){
        setMchId(mchId);
        return this;
    }
	
    /**
    * 获取服务商类。
    */
    public String getVendorClass(){
        return this.vendorClass;
    }

    /**
    * 设置服务商类。
    */
    public void setVendorClass(String vendorClass){
        this.vendorClass = vendorClass;
    }
	
    /**
    * 设置服务商类链式调用。
    */
    public AiModelConfigQueryParam vendorClass(String vendorClass) {
        setVendorClass(vendorClass);
        return this;
    }
	
    /**
    * 获取服务商代码。
    */
    public String getConfigCode(){
        return this.configCode;
    }

    /**
    * 设置服务商代码。
    */
    public void setConfigCode(String configCode){
        this.configCode = configCode;
    }
	
    /**
    * 设置服务商代码链式调用。
    */
    public AiModelConfigQueryParam configCode(String configCode) {
        setConfigCode(configCode);
        return this;
    }
	
    /**
    * 获取服务商名称。
    */
    public String getConfigName(){
        return this.configName;
    }

    /**
    * 设置服务商名称。
    */
    public void setConfigName(String configName){
        this.configName = configName;
    }
	
    /**
    * 设置服务商名称链式调用。
    */
    public AiModelConfigQueryParam configName(String configName) {
        setConfigName(configName);
        return this;
    }
	
    /**
    * 获取api地址。
    */
    public String getApiUrl(){
        return this.apiUrl;
    }

    /**
    * 设置api地址。
    */
    public void setApiUrl(String apiUrl){
        this.apiUrl = apiUrl;
    }
	
    /**
    * 设置api地址链式调用。
    */
    public AiModelConfigQueryParam apiUrl(String apiUrl) {
        setApiUrl(apiUrl);
        return this;
    }
	
    /**
    * 获取api key。
    */
    public String getApiKey(){
        return this.apiKey;
    }

    /**
    * 设置api key。
    */
    public void setApiKey(String apiKey){
        this.apiKey = apiKey;
    }
	
    /**
    * 设置api key链式调用。
    */
    public AiModelConfigQueryParam apiKey(String apiKey) {
        setApiKey(apiKey);
        return this;
    }
	
    /**
    * 获取主模型。
    */
    public String getModelMain(){
        return this.modelMain;
    }

    /**
    * 设置主模型。
    */
    public void setModelMain(String modelMain){
        this.modelMain = modelMain;
    }
	
    /**
    * 设置主模型链式调用。
    */
    public AiModelConfigQueryParam modelMain(String modelMain) {
        setModelMain(modelMain);
        return this;
    }
	
    /**
    * 获取嵌入模型。
    */
    public String getModelEmbed(){
        return this.modelEmbed;
    }

    /**
    * 设置嵌入模型。
    */
    public void setModelEmbed(String modelEmbed){
        this.modelEmbed = modelEmbed;
    }
	
    /**
    * 设置嵌入模型链式调用。
    */
    public AiModelConfigQueryParam modelEmbed(String modelEmbed) {
        setModelEmbed(modelEmbed);
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
    public AiModelConfigQueryParam createDateRange(Date[] createDateRange) {
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
    public AiModelConfigQueryParam modifyDateRange(Date[] modifyDateRange) {
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
    public AiModelConfigQueryParam state(Integer state) {
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
    public AiModelConfigQueryParam states(Integer[] states) {
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
    public AiModelConfigQueryParam stateGte(Integer stateGte) {
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
    public AiModelConfigQueryParam stateLte(Integer stateLte) {
        setStateLte(stateLte);
        return this;
    }
    

}