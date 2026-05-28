package uw.ai.center.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.app.dto.AuthPageQueryParam;
import uw.dao.annotation.QueryMeta;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * AI模型API连接配置列表查询参数。
 */
@Schema(title = "AI模型API连接配置列表查询参数", description = "AI模型API连接配置列表查询参数")
public class AiApiConfigQueryParam extends AuthPageQueryParam {

    public AiApiConfigQueryParam() {
        super();
    }

    public AiApiConfigQueryParam(Long saasId) {
        super(saasId);
    }

    @Override
    public Map<String, String> ALLOWED_SORT_PROPERTY() {
        return new HashMap<>() {{
            put("id", "id");
            put("saasId", "saas_id");
            put("mchId", "mch_id");
            put("apiCode", "api_code");
            put("apiName", "api_name");
            put("apiUrl", "api_url");
            put("createDate", "create_date");
            put("modifyDate", "modify_date");
            put("state", "state");
        }};
    }

    @QueryMeta(expr = "id=?")
    @Schema(title = "ID", description = "ID")
    private Long id;

    @QueryMeta(expr = "id in (?)")
    @Schema(title = "ID数组", description = "ID数组")
    private Long[] ids;

    @QueryMeta(expr = "mch_id=?")
    @Schema(title = "商户ID", description = "商户ID")
    private Long mchId;

    @QueryMeta(expr = "api_code like ?")
    @Schema(title = "配置代码", description = "配置代码")
    private String apiCode;

    @QueryMeta(expr = "api_name like ?")
    @Schema(title = "配置名称", description = "配置名称")
    private String apiName;

    @QueryMeta(expr = "api_url like ?")
    @Schema(title = "API地址", description = "API地址")
    private String apiUrl;

    @QueryMeta(expr = "create_date between ? and ?")
    @Schema(title = "创建时间范围", description = "创建时间范围")
    private Date[] createDateRange;

    @QueryMeta(expr = "modify_date between ? and ?")
    @Schema(title = "修改时间范围", description = "修改时间范围")
    private Date[] modifyDateRange;

    @QueryMeta(expr = "state=?")
    @Schema(title = "状态", description = "状态")
    private Integer state;

    @QueryMeta(expr = "state in (?)")
    @Schema(title = "状态数组", description = "状态数组")
    private Integer[] states;

    @QueryMeta(expr = "state>=?")
    @Schema(title = "大于等于状态", description = "大于等于状态")
    private Integer stateGte;

    @QueryMeta(expr = "state<=?")
    @Schema(title = "小于等于状态", description = "小于等于状态")
    private Integer stateLte;

    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public AiApiConfigQueryParam id(Long id) { setId(id); return this; }

    public Long[] getIds() { return this.ids; }
    public void setIds(Long[] ids) { this.ids = ids; }
    public AiApiConfigQueryParam ids(Long[] ids) { setIds(ids); return this; }

    public Long getMchId() { return this.mchId; }
    public void setMchId(Long mchId) { this.mchId = mchId; }
    public AiApiConfigQueryParam mchId(Long mchId) { setMchId(mchId); return this; }

    public String getApiCode() { return this.apiCode; }
    public void setApiCode(String apiCode) { this.apiCode = apiCode; }
    public AiApiConfigQueryParam apiCode(String apiCode) { setApiCode(apiCode); return this; }

    public String getApiName() { return this.apiName; }
    public void setApiName(String apiName) { this.apiName = apiName; }
    public AiApiConfigQueryParam apiName(String apiName) { setApiName(apiName); return this; }

    public String getApiUrl() { return this.apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
    public AiApiConfigQueryParam apiUrl(String apiUrl) { setApiUrl(apiUrl); return this; }

    public Date[] getCreateDateRange() { return this.createDateRange; }
    public void setCreateDateRange(Date[] createDateRange) { this.createDateRange = createDateRange; }
    public AiApiConfigQueryParam createDateRange(Date[] createDateRange) { setCreateDateRange(createDateRange); return this; }

    public Date[] getModifyDateRange() { return this.modifyDateRange; }
    public void setModifyDateRange(Date[] modifyDateRange) { this.modifyDateRange = modifyDateRange; }
    public AiApiConfigQueryParam modifyDateRange(Date[] modifyDateRange) { setModifyDateRange(modifyDateRange); return this; }

    public Integer getState() { return this.state; }
    public void setState(Integer state) { this.state = state; }
    public AiApiConfigQueryParam state(Integer state) { setState(state); return this; }

    public Integer[] getStates() { return this.states; }
    public void setStates(Integer[] states) { this.states = states; }
    public AiApiConfigQueryParam states(Integer[] states) { setStates(states); return this; }

    public Integer getStateGte() { return this.stateGte; }
    public void setStateGte(Integer stateGte) { this.stateGte = stateGte; }
    public AiApiConfigQueryParam stateGte(Integer stateGte) { setStateGte(stateGte); return this; }

    public Integer getStateLte() { return this.stateLte; }
    public void setStateLte(Integer stateLte) { this.stateLte = stateLte; }
    public AiApiConfigQueryParam stateLte(Integer stateLte) { setStateLte(stateLte); return this; }
}