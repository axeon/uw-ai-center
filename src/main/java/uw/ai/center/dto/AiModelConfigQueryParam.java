package uw.ai.center.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.app.dto.AuthPageQueryParam;
import uw.dao.annotation.QueryMeta;

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

    private static final Map<String, String> ALLOWED_SORT_PROPERTY = Map.ofEntries(
        Map.entry("id", "id"),
        Map.entry("saasId", "saas_id"),
        Map.entry("mchId", "mch_id"),
        Map.entry("apiId", "api_id"),
        Map.entry("vendorClass", "vendor_class"),
        Map.entry("modelType", "model_type"),
        Map.entry("modelTag", "model_tag"),
        Map.entry("configCode", "config_code"),
        Map.entry("configName", "config_name"),
        Map.entry("modelName", "model_name"),
        Map.entry("state", "state"),
        Map.entry("createDate", "create_date"),
        Map.entry("modifyDate", "modify_date")
);

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
     * API配置ID。
     */
    @QueryMeta(expr = "api_id=?")
    @Schema(title = "API配置ID", description = "API配置ID")
    private Long apiId;

    /**
     * 供应商类。
     */
    @QueryMeta(expr = "vendor_class like ?")
    @Schema(title = "供应商类", description = "供应商类")
    private String vendorClass;

    /**
     * 模型类型: CHAT/EMBEDDING/RERANK/TTS/OCR/IMAGE_GENERATION/AUDIO_TRANSCRIPTION。
     */
    @QueryMeta(expr = "model_type like ?")
    @Schema(title = "模型类型: CHAT/EMBEDDING/RERANK/TTS/OCR/IMAGE_GENERATION/AUDIO_TRANSCRIPTION", description = "模型类型: CHAT/EMBEDDING/RERANK/TTS/OCR/IMAGE_GENERATION/AUDIO_TRANSCRIPTION")
    private String modelType;

    /**
     * 模型能力标签。
     */
    @QueryMeta(expr = "model_tag like ?")
    @Schema(title = "模型能力标签", description = "模型能力标签")
    private String modelTag;

    /**
     * 配置代码。
     */
    @QueryMeta(expr = "config_code like ?")
    @Schema(title = "配置代码", description = "配置代码")
    private String configCode;

    /**
     * 配置名称。
     */
    @QueryMeta(expr = "config_name like ?")
    @Schema(title = "配置名称", description = "配置名称")
    private String configName;

    /**
     * 模型名。
     */
    @QueryMeta(expr = "model_name like ?")
    @Schema(title = "模型名", description = "模型名")
    private String modelName;

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
    public AiModelConfigQueryParam id(Long id) {
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
    public AiModelConfigQueryParam ids(Long[] ids) {
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
    public AiModelConfigQueryParam mchId(Long mchId) {
        setMchId(mchId);
        return this;
    }

    /**
     * 获取API配置ID。
     */
    public Long getApiId() {
        return this.apiId;
    }

    /**
     * 设置API配置ID。
     */
    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    /**
     * 设置API配置ID链式调用。
     */
    public AiModelConfigQueryParam apiId(Long apiId) {
        setApiId(apiId);
        return this;
    }

    /**
     * 获取供应商类。
     */
    public String getVendorClass() {
        return this.vendorClass;
    }

    /**
     * 设置供应商类。
     */
    public void setVendorClass(String vendorClass) {
        this.vendorClass = vendorClass;
    }

    /**
     * 设置供应商类链式调用。
     */
    public AiModelConfigQueryParam vendorClass(String vendorClass) {
        setVendorClass(vendorClass);
        return this;
    }

    /**
     * 获取模型类型: CHAT/EMBEDDING/RERANK/TTS/OCR/IMAGE_GENERATION/AUDIO_TRANSCRIPTION。
     */
    public String getModelType() {
        return this.modelType;
    }

    /**
     * 设置模型类型: CHAT/EMBEDDING/RERANK/TTS/OCR/IMAGE_GENERATION/AUDIO_TRANSCRIPTION。
     */
    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    /**
     * 设置模型类型: CHAT/EMBEDDING/RERANK/TTS/OCR/IMAGE_GENERATION/AUDIO_TRANSCRIPTION链式调用。
     */
    public AiModelConfigQueryParam modelType(String modelType) {
        setModelType(modelType);
        return this;
    }

    /**
     * 获取模型能力标签。
     */
    public String getModelTag() {
        return this.modelTag;
    }

    /**
     * 设置模型能力标签。
     */
    public void setModelTag(String modelTag) {
        this.modelTag = modelTag;
    }

    /**
     * 设置模型能力标签链式调用。
     */
    public AiModelConfigQueryParam modelTag(String modelTag) {
        setModelTag(modelTag);
        return this;
    }

    /**
     * 获取配置代码。
     */
    public String getConfigCode() {
        return this.configCode;
    }

    /**
     * 设置配置代码。
     */
    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    /**
     * 设置配置代码链式调用。
     */
    public AiModelConfigQueryParam configCode(String configCode) {
        setConfigCode(configCode);
        return this;
    }

    /**
     * 获取配置名称。
     */
    public String getConfigName() {
        return this.configName;
    }

    /**
     * 设置配置名称。
     */
    public void setConfigName(String configName) {
        this.configName = configName;
    }

    /**
     * 设置配置名称链式调用。
     */
    public AiModelConfigQueryParam configName(String configName) {
        setConfigName(configName);
        return this;
    }

    /**
     * 获取模型名。
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * 设置模型名。
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 设置模型名链式调用。
     */
    public AiModelConfigQueryParam modelName(String modelName) {
        setModelName(modelName);
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
    public AiModelConfigQueryParam state(Integer state) {
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
    public AiModelConfigQueryParam states(Integer[] states) {
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
    public AiModelConfigQueryParam stateGte(Integer stateGte) {
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
    public AiModelConfigQueryParam stateLte(Integer stateLte) {
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
    public AiModelConfigQueryParam createDateRange(java.util.Date[] createDateRange) {
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
    public AiModelConfigQueryParam modifyDateRange(java.util.Date[] modifyDateRange) {
        setModifyDateRange(modifyDateRange);
        return this;
    }


}