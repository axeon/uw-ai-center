package uw.ai.center.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.app.dto.AuthPageQueryParam;
import uw.dao.annotation.QueryMeta;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * AI模型配置列表查询参数。
 */
@Schema(title = "AI模型配置列表查询参数", description = "AI模型配置列表查询参数")
public class AiModelConfigQueryParam extends AuthPageQueryParam {

    public AiModelConfigQueryParam() {
        super();
    }

    public AiModelConfigQueryParam(Long saasId) {
        super(saasId);
    }

    @Override
    public Map<String, String> ALLOWED_SORT_PROPERTY() {
        return new HashMap<>() {{
            put("id", "id");
            put("saasId", "saas_id");
            put("mchId", "mch_id");
            put("apiId", "api_id");
            put("vendorClass", "vendor_class");
            put("modelType", "model_type");
            put("configCode", "config_code");
            put("configName", "config_name");
            put("modelName", "model_name");
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

    @QueryMeta(expr = "api_id=?")
    @Schema(title = "API配置ID", description = "API配置ID")
    private Long apiId;

    @QueryMeta(expr = "vendor_class like ?")
    @Schema(title = "供应商类", description = "供应商类")
    private String vendorClass;

    @QueryMeta(expr = "model_type like ?")
    @Schema(title = "模型类型", description = "CHAT/EMBEDDING/RERANK...")
    private String modelType;

    @QueryMeta(expr = "config_code like ?")
    @Schema(title = "配置代码", description = "配置代码")
    private String configCode;

    @QueryMeta(expr = "config_name like ?")
    @Schema(title = "配置名称", description = "配置名称")
    private String configName;

    @QueryMeta(expr = "model_name like ?")
    @Schema(title = "模型名", description = "模型名")
    private String modelName;

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
    public AiModelConfigQueryParam id(Long id) { setId(id); return this; }

    public Long[] getIds() { return this.ids; }
    public void setIds(Long[] ids) { this.ids = ids; }
    public AiModelConfigQueryParam ids(Long[] ids) { setIds(ids); return this; }

    public Long getMchId() { return this.mchId; }
    public void setMchId(Long mchId) { this.mchId = mchId; }
    public AiModelConfigQueryParam mchId(Long mchId) { setMchId(mchId); return this; }

    public Long getApiId() { return this.apiId; }
    public void setApiId(Long apiId) { this.apiId = apiId; }
    public AiModelConfigQueryParam apiId(Long apiId) { setApiId(apiId); return this; }

    public String getVendorClass() { return this.vendorClass; }
    public void setVendorClass(String vendorClass) { this.vendorClass = vendorClass; }
    public AiModelConfigQueryParam vendorClass(String vendorClass) { setVendorClass(vendorClass); return this; }

    public String getModelType() { return this.modelType; }
    public void setModelType(String modelType) { this.modelType = modelType; }
    public AiModelConfigQueryParam modelType(String modelType) { setModelType(modelType); return this; }

    public String getConfigCode() { return this.configCode; }
    public void setConfigCode(String configCode) { this.configCode = configCode; }
    public AiModelConfigQueryParam configCode(String configCode) { setConfigCode(configCode); return this; }

    public String getConfigName() { return this.configName; }
    public void setConfigName(String configName) { this.configName = configName; }
    public AiModelConfigQueryParam configName(String configName) { setConfigName(configName); return this; }

    public String getModelName() { return this.modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public AiModelConfigQueryParam modelName(String modelName) { setModelName(modelName); return this; }

    public Date[] getCreateDateRange() { return this.createDateRange; }
    public void setCreateDateRange(Date[] createDateRange) { this.createDateRange = createDateRange; }
    public AiModelConfigQueryParam createDateRange(Date[] createDateRange) { setCreateDateRange(createDateRange); return this; }

    public Date[] getModifyDateRange() { return this.modifyDateRange; }
    public void setModifyDateRange(Date[] modifyDateRange) { this.modifyDateRange = modifyDateRange; }
    public AiModelConfigQueryParam modifyDateRange(Date[] modifyDateRange) { setModifyDateRange(modifyDateRange); return this; }

    public Integer getState() { return this.state; }
    public void setState(Integer state) { this.state = state; }
    public AiModelConfigQueryParam state(Integer state) { setState(state); return this; }

    public Integer[] getStates() { return this.states; }
    public void setStates(Integer[] states) { this.states = states; }
    public AiModelConfigQueryParam states(Integer[] states) { setStates(states); return this; }

    public Integer getStateGte() { return this.stateGte; }
    public void setStateGte(Integer stateGte) { this.stateGte = stateGte; }
    public AiModelConfigQueryParam stateGte(Integer stateGte) { setStateGte(stateGte); return this; }

    public Integer getStateLte() { return this.stateLte; }
    public void setStateLte(Integer stateLte) { this.stateLte = stateLte; }
    public AiModelConfigQueryParam stateLte(Integer stateLte) { setStateLte(stateLte); return this; }
}