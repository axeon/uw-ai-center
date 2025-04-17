package uw.ai.center.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.app.dto.AuthPageQueryParam;
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
            put( "docBodySize", "doc_body_size" );
            put( "docContentSize", "doc_content_size" );
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
    * ID数组。
    */
    @QueryMeta(expr = "id in (?)")
    @Schema(title="ID数组", description = "ID数组，可同时匹配多个。")
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
    @QueryMeta(expr = "doc_type like ?")
    @Schema(title="文档类型", description = "文档类型")
    private String docType;
	
    /**
    * 文档名称。
    */
    @QueryMeta(expr = "doc_name like ?")
    @Schema(title="文档名称", description = "文档名称")
    private String docName;
	
    /**
    * 文档主体。
    */
    @QueryMeta(expr = "doc_body=?")
    @Schema(title="文档主体", description = "文档主体")
    private Object docBody;
	
    /**
    * 文档主体大小。
    */
    @QueryMeta(expr = "doc_body_size=?")
    @Schema(title="文档主体大小", description = "文档主体大小")
    private Long docBodySize;

    /**
    * 文档主体大小范围。
    */
    @QueryMeta(expr = "doc_body_size between ? and ?")
    @Schema(title="文档主体大小范围", description = "文档主体大小范围")
    private Long[] docBodySizeRange;
	
    /**
    * 文档内容大小。
    */
    @QueryMeta(expr = "doc_content_size=?")
    @Schema(title="文档内容大小", description = "文档内容大小")
    private Long docContentSize;

    /**
    * 文档内容大小范围。
    */
    @QueryMeta(expr = "doc_content_size between ? and ?")
    @Schema(title="文档内容大小范围", description = "文档内容大小范围")
    private Long[] docContentSizeRange;
	
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
    * 状态数组。
    */
    @QueryMeta(expr = "state in (?)")
    @Schema(title="状态数组", description = "状态数组，可同时匹配多个状态。")
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
    * 获取ID数组。
    */
    public Long[] getIds() {
        return this.ids;
    }

    /**
    * 设置ID数组。
    */
    public void setIds(Long[] ids) {
        this.ids = ids;
    }

    /**
    * 设置ID数组链式调用。
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
    public String getDocType(){
        return this.docType;
    }

    /**
    * 设置文档类型。
    */
    public void setDocType(String docType){
        this.docType = docType;
    }
	
    /**
    * 设置文档类型链式调用。
    */
    public AiRagDocQueryParam docType(String docType) {
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
    * 获取文档主体。
    */
    public Object getDocBody(){
        return this.docBody;
    }

    /**
    * 设置文档主体。
    */
    public void setDocBody(Object docBody){
        this.docBody = docBody;
    }
	
    /**
    * 设置文档主体链式调用。
    */
	public AiRagDocQueryParam docBody(Object docBody){
        setDocBody(docBody);
        return this;
    }
	
    /**
    * 获取文档主体大小。
    */
    public Long getDocBodySize(){
        return this.docBodySize;
    }

    /**
    * 设置文档主体大小。
    */
    public void setDocBodySize(Long docBodySize){
        this.docBodySize = docBodySize;
    }
	
    /**
    * 设置文档主体大小链式调用。
    */
    public AiRagDocQueryParam docBodySize(Long docBodySize){
        setDocBodySize(docBodySize);
        return this;
    }

    /**
    * 获取文档主体大小范围。
    */
    public Long[] getDocBodySizeRange(){
        return this.docBodySizeRange;
    }

    /**
    * 设置文档主体大小范围。
    */
    public void setDocBodySizeRange(Long[] docBodySizeRange){
        this.docBodySizeRange = docBodySizeRange;
    }
	
    /**
    * 设置文档主体大小范围链式调用。
    */
    public AiRagDocQueryParam docBodySizeRange(Long[] docBodySizeRange){
        setDocBodySizeRange(docBodySizeRange);
        return this;
    }
	
    /**
    * 获取文档内容大小。
    */
    public Long getDocContentSize(){
        return this.docContentSize;
    }

    /**
    * 设置文档内容大小。
    */
    public void setDocContentSize(Long docContentSize){
        this.docContentSize = docContentSize;
    }
	
    /**
    * 设置文档内容大小链式调用。
    */
    public AiRagDocQueryParam docContentSize(Long docContentSize){
        setDocContentSize(docContentSize);
        return this;
    }

    /**
    * 获取文档内容大小范围。
    */
    public Long[] getDocContentSizeRange(){
        return this.docContentSizeRange;
    }

    /**
    * 设置文档内容大小范围。
    */
    public void setDocContentSizeRange(Long[] docContentSizeRange){
        this.docContentSizeRange = docContentSizeRange;
    }
	
    /**
    * 设置文档内容大小范围链式调用。
    */
    public AiRagDocQueryParam docContentSizeRange(Long[] docContentSizeRange){
        setDocContentSizeRange(docContentSizeRange);
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
    * 获取状态数组。
    */
    public Integer[] getStates(){
        return this.states;
    }

    /**
    * 设置状态数组。
    */
    public void setStates(Integer[] states){
        this.states = states;
    }
	
    /**
    * 设置状态数组链式调用。
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