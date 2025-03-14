package uw.ai.center.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.app.common.dto.AuthPageQueryParam;
import uw.dao.annotation.QueryMeta;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
* rag文档信息列表查询参数。
*/
@Schema(title = "rag文档信息列表查询参数", description = "rag文档信息列表查询参数")
public class AiRagDocQueryParam extends AuthPageQueryParam{

    public AiRagDocQueryParam() {
        super();
    }

    public AiRagDocQueryParam(Long saasId) {
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
            put( "libId", "lib_id" );
            put( "docType", "doc_type" );
            put( "docName", "doc_name" );
            put( "docSize", "doc_size" );
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
    * libId。
    */
    @QueryMeta(expr = "lib_id=?")
    @Schema(title="libId", description = "libId")
    private Long libId;
	
    /**
    * 文档类型。
    */
    @QueryMeta(expr = "doc_type=?")
    @Schema(title="文档类型", description = "文档类型")
    private Integer docType;
	
    /**
    * 文档名称。
    */
    @QueryMeta(expr = "doc_name like ?")
    @Schema(title="文档名称", description = "文档名称")
    private String docName;
	
    /**
    * 文档大小。
    */
    @QueryMeta(expr = "doc_size=?")
    @Schema(title="文档大小", description = "文档大小")
    private Long docSize;

    /**
    * 文档大小范围。
    */
    @QueryMeta(expr = "doc_size between ? and ?")
    @Schema(title="文档大小范围", description = "文档大小范围")
    private Long[] docSizeRange;
	
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
    public AiRagDocQueryParam id(Long id) {
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
    public AiRagDocQueryParam ids(Long[] ids) {
        setIds(ids);
        return this;
    }

    /**
    * 获取libId。
    */
    public Long getLibId(){
        return this.libId;
    }

    /**
    * 设置libId。
    */
    public void setLibId(Long libId){
        this.libId = libId;
    }
	
    /**
    * 设置libId链式调用。
    */
	public AiRagDocQueryParam libId(Long libId){
        setLibId(libId);
        return this;
    }
	
    /**
    * 获取文档类型。
    */
    public Integer getDocType(){
        return this.docType;
    }

    /**
    * 设置文档类型。
    */
    public void setDocType(Integer docType){
        this.docType = docType;
    }
	
    /**
    * 设置文档类型链式调用。
    */
	public AiRagDocQueryParam docType(Integer docType){
        setDocType(docType);
        return this;
    }
	
    /**
    * 获取文档名称。
    */
    public String getDocName(){
        return this.docName;
    }

    /**
    * 设置文档名称。
    */
    public void setDocName(String docName){
        this.docName = docName;
    }
	
    /**
    * 设置文档名称链式调用。
    */
    public AiRagDocQueryParam docName(String docName) {
        setDocName(docName);
        return this;
    }
	
    /**
    * 获取文档大小。
    */
    public Long getDocSize(){
        return this.docSize;
    }

    /**
    * 设置文档大小。
    */
    public void setDocSize(Long docSize){
        this.docSize = docSize;
    }
	
    /**
    * 设置文档大小链式调用。
    */
    public AiRagDocQueryParam docSize(Long docSize){
        setDocSize(docSize);
        return this;
    }

    /**
    * 获取文档大小范围。
    */
    public Long[] getDocSizeRange(){
        return this.docSizeRange;
    }

    /**
    * 设置文档大小范围。
    */
    public void setDocSizeRange(Long[] docSizeRange){
        this.docSizeRange = docSizeRange;
    }
	
    /**
    * 设置文档大小范围链式调用。
    */
    public AiRagDocQueryParam docSizeRange(Long[] docSizeRange){
        setDocSizeRange(docSizeRange);
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
    public AiRagDocQueryParam createDateRange(Date[] createDateRange) {
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
    public AiRagDocQueryParam modifyDateRange(Date[] modifyDateRange) {
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
    public AiRagDocQueryParam state(Integer state) {
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
    public AiRagDocQueryParam states(Integer[] states) {
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
    public AiRagDocQueryParam stateGte(Integer stateGte) {
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
    public AiRagDocQueryParam stateLte(Integer stateLte) {
        setStateLte(stateLte);
        return this;
    }
    

}