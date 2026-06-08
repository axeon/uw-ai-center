package uw.ai.center.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.app.dto.AuthPageQueryParam;
import uw.dao.annotation.QueryMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * AI模型API配置列表查询参数。
 */
@Schema(title = "AI模型API配置列表查询参数", description = "AI模型API配置列表查询参数")
public class AiApiConfigQueryParam extends AuthPageQueryParam {

    public AiApiConfigQueryParam() {
        super();
    }

    public AiApiConfigQueryParam(Long saasId) {
        super(saasId);
    }

    private static final Map<String, String> ALLOWED_SORT_PROPERTY = new HashMap<>() {{
        put("id", "id");
        put("saasId", "saas_id");
        put("mchId", "mch_id");
        put("apiCode", "api_code");
        put("apiName", "api_name");
        put("apiUrl", "api_url");
        put("apiKey", "api_key");
        put("state", "state");
        put("createDate", "create_date");
        put("modifyDate", "modify_date");
    }};

    /**
     * 允许的排序属性。
     *
     * @return
     */
    @Override
    public Map<String, String> ALLOWED_SORT_PROPERTY() {
        return ALLOWED_SORT_PROPERTY;
    }

    /**
     * ID。
     */
    @QueryMeta(expr = "id=?")
    @Schema(title = "ID", description = "ID")
    private Long id;

    /**
     * ID数组。
     */
    @QueryMeta(expr = "id in (?)")
    @Schema(title = "ID数组", description = "ID数组，可同时匹配多个。")
    private Long[] ids;

    /**
     * 商户ID。
     */
    @QueryMeta(expr = "mch_id=?")
    @Schema(title = "商户ID", description = "商户ID")
    private Long mchId;

    /**
     * 配置代码。
     */
    @QueryMeta(expr = "api_code like ?")
    @Schema(title = "配置代码", description = "配置代码")
    private String apiCode;

    /**
     * 配置名称。
     */
    @QueryMeta(expr = "api_name like ?")
    @Schema(title = "配置名称", description = "配置名称")
    private String apiName;

    /**
     * API地址。
     */
    @QueryMeta(expr = "api_url like ?")
    @Schema(title = "API地址", description = "API地址")
    private String apiUrl;

    /**
     * API密钥。
     */
    @QueryMeta(expr = "api_key like ?")
    @Schema(title = "API密钥", description = "API密钥")
    private String apiKey;

    /**
     * 状态。
     */
    @QueryMeta(expr = "state=?")
    @Schema(title = "状态", description = "状态")
    private Integer state;

    /**
     * 状态数组。
     */
    @QueryMeta(expr = "state in (?)")
    @Schema(title = "状态数组", description = "状态数组，可同时匹配多个状态。")
    private Integer[] states;

    /**
     * 大于等于状态。
     */
    @QueryMeta(expr = "state>=?")
    @Schema(title = "大于等于状态", description = "大于等于状态")
    private Integer stateGte;

    /**
     * 小于等于状态。
     */
    @QueryMeta(expr = "state<=?")
    @Schema(title = "小于等于状态", description = "小于等于状态")
    private Integer stateLte;

    /**
     * 创建时间范围。
     */
    @QueryMeta(expr = "create_date between ? and ?")
    @Schema(title = "创建时间范围", description = "创建时间范围")
    private java.util.Date[] createDateRange;

    /**
     * 修改时间范围。
     */
    @QueryMeta(expr = "modify_date between ? and ?")
    @Schema(title = "修改时间范围", description = "修改时间范围")
    private java.util.Date[] modifyDateRange;


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
    public AiApiConfigQueryParam id(Long id) {
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
    public AiApiConfigQueryParam ids(Long[] ids) {
        setIds(ids);
        return this;
    }

    /**
     * 获取商户ID。
     */
    public Long getMchId() {
        return this.mchId;
    }

    /**
     * 设置商户ID。
     */
    public void setMchId(Long mchId) {
        this.mchId = mchId;
    }

    /**
     * 设置商户ID链式调用。
     */
    public AiApiConfigQueryParam mchId(Long mchId) {
        setMchId(mchId);
        return this;
    }

    /**
     * 获取配置代码。
     */
    public String getApiCode() {
        return this.apiCode;
    }

    /**
     * 设置配置代码。
     */
    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }

    /**
     * 设置配置代码链式调用。
     */
    public AiApiConfigQueryParam apiCode(String apiCode) {
        setApiCode(apiCode);
        return this;
    }

    /**
     * 获取配置名称。
     */
    public String getApiName() {
        return this.apiName;
    }

    /**
     * 设置配置名称。
     */
    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    /**
     * 设置配置名称链式调用。
     */
    public AiApiConfigQueryParam apiName(String apiName) {
        setApiName(apiName);
        return this;
    }

    /**
     * 获取API地址。
     */
    public String getApiUrl() {
        return this.apiUrl;
    }

    /**
     * 设置API地址。
     */
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    /**
     * 设置API地址链式调用。
     */
    public AiApiConfigQueryParam apiUrl(String apiUrl) {
        setApiUrl(apiUrl);
        return this;
    }

    /**
     * 获取API密钥。
     */
    public String getApiKey() {
        return this.apiKey;
    }

    /**
     * 设置API密钥。
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * 设置API密钥链式调用。
     */
    public AiApiConfigQueryParam apiKey(String apiKey) {
        setApiKey(apiKey);
        return this;
    }

    /**
     * 获取状态。
     */
    public Integer getState() {
        return this.state;
    }

    /**
     * 设置状态。
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 设置状态链式调用。
     */
    public AiApiConfigQueryParam state(Integer state) {
        setState(state);
        return this;
    }

    /**
     * 获取状态数组。
     */
    public Integer[] getStates() {
        return this.states;
    }

    /**
     * 设置状态数组。
     */
    public void setStates(Integer[] states) {
        this.states = states;
    }

    /**
     * 设置状态数组链式调用。
     */
    public AiApiConfigQueryParam states(Integer[] states) {
        setStates(states);
        return this;
    }

    /**
     * 获取大于等于状态。
     */
    public Integer getStateGte() {
        return this.stateGte;
    }

    /**
     * 设置大于等于状态。
     */
    public void setStateGte(Integer stateGte) {
        this.stateGte = stateGte;
    }

    /**
     * 设置大于等于状态链式调用。
     */
    public AiApiConfigQueryParam stateGte(Integer stateGte) {
        setStateGte(stateGte);
        return this;
    }

    /**
     * 获取小于等于状态。
     */
    public Integer getStateLte() {
        return this.stateLte;
    }

    /**
     * 获取小于等于状态。
     */
    public void setStateLte(Integer stateLte) {
        this.stateLte = stateLte;
    }

    /**
     * 获取小于等于状态链式调用。
     */
    public AiApiConfigQueryParam stateLte(Integer stateLte) {
        setStateLte(stateLte);
        return this;
    }

    /**
     * 获取创建时间范围。
     */
    public java.util.Date[] getCreateDateRange() {
        return this.createDateRange;
    }

    /**
     * 设置创建时间范围。
     */
    public void setCreateDateRange(java.util.Date[] createDateRange) {
        this.createDateRange = createDateRange;
    }

    /**
     * 设置创建时间范围链式调用。
     */
    public AiApiConfigQueryParam createDateRange(java.util.Date[] createDateRange) {
        setCreateDateRange(createDateRange);
        return this;
    }

    /**
     * 获取修改时间范围。
     */
    public java.util.Date[] getModifyDateRange() {
        return this.modifyDateRange;
    }

    /**
     * 设置修改时间范围。
     */
    public void setModifyDateRange(java.util.Date[] modifyDateRange) {
        this.modifyDateRange = modifyDateRange;
    }

    /**
     * 设置修改时间范围链式调用。
     */
    public AiApiConfigQueryParam modifyDateRange(java.util.Date[] modifyDateRange) {
        setModifyDateRange(modifyDateRange);
        return this;
    }


}