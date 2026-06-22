package uw.ai.center.controller.admin.model;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiModelConfigQueryParam;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.center.vendor.AiVendorHelper;
import uw.ai.center.vo.AiVendorInfo;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.common.app.constant.CommonState;
import uw.common.app.dto.*;
import uw.common.app.entity.*;
import uw.common.app.helper.SysDataHistoryHelper;
import uw.common.response.ResponseData;
import uw.common.util.SystemClock;
import uw.dao.DaoManager;
import uw.common.data.PageList;

import java.util.List;

/**
 * AI模型配置管理。
 * <p>平台管理（ADMIN）角色的 AI 模型配置增删改查接口，路径前缀 {@code /admin/model/config}。
 */
@RestController
@RequestMapping("/admin/model/config")
@Tag(name = "AI模型配置管理", description = "AI模型配置增删改查列管理")
@MscPermDeclare(user = UserType.ADMIN)
public class AiModelConfigController {

    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 列表AI服务（供应商），返回所有已注册 Vendor 的名称/描述/版本/类名/配置参数模板。
     *
     * @return 供应商信息列表
     */
    @GetMapping("/listVendor")
    @Operation(summary = "列表AI服务", description = "列表AI服务")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.USER, log = ActionLog.NONE)
    public List<AiVendorInfo> listVendor() {
        return AiVendorHelper.getVendorMap().values().stream().map(AiVendorInfo::new).toList();
    }

    /**
     * 列表指定供应商在指定 API 端点下可用的模型名。
     *
     * @param vendorClass 供应商类名
     * @param apiUrl      API 端点 URL
     * @param apiKey      API 密钥（可选）
     * @return 模型名列表
     */
    @GetMapping("/listModel")
    @Operation(summary = "列表模型列表", description = "列表模型列表")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.USER, log = ActionLog.NONE)
    public List<String> listModel(@Parameter(description = "vendorClass", required = true) @RequestParam String vendorClass,
                                  @Parameter(description = "apiUrl", required = true) @RequestParam String apiUrl,
                                  @Parameter(description = "apiKey", required = false) @RequestParam(required = false) String apiKey) {
        return AiVendorHelper.listModel(vendorClass, apiUrl, apiKey);
    }

    /**
     * 分页列表AI模型配置。
     *
     * @param queryParam 查询参数（自动绑定当前租户 saasId）
     * @return 模型配置分页列表
     */
    @GetMapping("/list")
    @Operation(summary = "列表AI模型配置", description = "列表AI模型配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<AiModelConfig>> list(AiModelConfigQueryParam queryParam){
        AuthServiceHelper.logRef(AiModelConfig.class);
        return dao.list(AiModelConfig.class, queryParam);
    }

    /**
     * 轻量级列表AI模型配置（仅关键列，不含 modelData 等大字段），一般用于前端 select 控件。
     *
     * @param queryParam 查询参数（自动绑定当前租户 saasId）
     * @return 模型配置分页列表（精简字段）
     */
    @GetMapping("/listLite")
    @Operation(summary = "轻量级列表AI模型配置", description = "轻量级列表AI模型配置，一般用于select控件。")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.USER, log = ActionLog.NONE)
    public ResponseData<PageList<AiModelConfig>> listLite(AiModelConfigQueryParam queryParam){
        queryParam.SELECT_SQL( "SELECT id,saas_id,mch_id,api_id,vendor_class,model_type,model_tag,config_code,config_name,model_name,state,create_date,modify_date from ai_model_config " );
        return dao.list(AiModelConfig.class, queryParam);
    }

    /**
     * 按主键加载单条AI模型配置。
     *
     * @param id 主键ID
     * @return 模型配置
     */
    @GetMapping("/load")
    @Operation(summary = "加载AI模型配置", description = "加载AI模型配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<AiModelConfig> load(@Parameter(description = "主键ID", required = true) @RequestParam long id)  {
        AuthServiceHelper.logRef(AiModelConfig.class,id);
        return dao.queryForObject(AiModelConfig.class, new AuthIdQueryParam(id));
    }

    /**
     * 查询指定AI模型配置的数据变更历史。
     *
     * @param queryParam 历史查询参数（按 entityId 过滤）
     * @return 数据历史分页列表
     */
    @GetMapping("/listDataHistory")
    @Operation(summary = "查询数据历史", description = "查询数据历史")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<SysDataHistory>> listDataHistory(SysDataHistoryQueryParam queryParam){
        AuthServiceHelper.logRef(AiModelConfig.class, queryParam.getEntityId());
        queryParam.setEntityClass(AiModelConfig.class);
        return dao.list(SysDataHistory.class, queryParam);
    }

    /**
     * 查询指定AI模型配置的关键操作日志。
     *
     * @param queryParam 日志查询参数（按 bizId 过滤）
     * @return 操作日志分页列表
     */
    @GetMapping("/listCritLog")
    @Operation(summary = "查询操作日志", description = "查询操作日志")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<SysCritLog>> listCritLog(SysCritLogQueryParam queryParam)  {
        AuthServiceHelper.logRef(AiModelConfig.class, queryParam.getBizId());
        queryParam.setBizTypeClass(AiModelConfig.class);
        return dao.list(SysCritLog.class, queryParam);
    }

    /**
     * 新增AI模型配置。
     * <p>configCode 非空时做全局唯一性校验；saasId 强制绑定当前租户；保存后记录数据历史。
     *
     * @param aiModelConfig 模型配置（configCode/configName/modelName/vendorClass/modelType 等）
     * @return 保存后的模型配置
     */
    @PostMapping("/save")
    @Operation(summary = "新增AI模型配置", description = "新增AI模型配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiModelConfig> save(@RequestBody AiModelConfig aiModelConfig){
        if (StringUtils.isNotBlank(aiModelConfig.getConfigCode())) {
            long count = dao.queryForValue(Long.class, "select count(*) from ai_model_config where config_code=? and state=?", new Object[]{aiModelConfig.getConfigCode(), CommonState.ENABLED.getValue()}).getData();
            if (count > 0) {
                return ResponseData.errorMsg("配置代码[" + aiModelConfig.getConfigCode() + "]已存在！");
            }
        }
        long id = dao.getSequenceId(AiModelConfig.class);
        AuthServiceHelper.logRef(AiModelConfig.class,id);
        aiModelConfig.setId(id);
        aiModelConfig.setSaasId(AuthServiceHelper.getSaasId());
        aiModelConfig.setCreateDate(SystemClock.nowDate());
        aiModelConfig.setModifyDate(null);
        aiModelConfig.setState(CommonState.ENABLED.getValue());
        return dao.save( aiModelConfig ).onSuccess(savedEntity -> {
            SysDataHistoryHelper.saveHistory(aiModelConfig);
        });
    }

    /**
     * 修改AI模型配置。
     * <p>configCode 唯一性校验排除自身（id&lt;&gt;?）；更新后级联失效 Vendor 客户端缓存并记录数据历史。
     *
     * @param aiModelConfig 待更新的模型配置
     * @param remark        操作备注（记入日志与历史）
     * @return 更新后的模型配置
     */
    @PutMapping("/update")
    @Operation(summary = "修改AI模型配置", description = "修改AI模型配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiModelConfig> update(@RequestBody AiModelConfig aiModelConfig, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelConfig.class,aiModelConfig.getId(),remark);
        // 判重：配置代码在未删除记录中必须全局唯一（排除自身，避免修改自身时误判为重复）
        if (StringUtils.isNotBlank(aiModelConfig.getConfigCode())) {
            long count = dao.queryForValue(Long.class, "select count(*) from ai_model_config where config_code=? and state=? and id<>?", new Object[]{aiModelConfig.getConfigCode(), CommonState.ENABLED.getValue(), aiModelConfig.getId()}).getData();
            if (count > 0) {
                return ResponseData.errorMsg("配置代码[" + aiModelConfig.getConfigCode() + "]已存在！");
            }
        }
        return dao.queryForObject( AiModelConfig.class,new AuthIdQueryParam(aiModelConfig.getId()) ).onSuccess(aiModelConfigDb-> {
            // 失效缓存前先取 DB 中旧 configCode（避免 TOCTOU：失效期间若被改名，读缓存拿到的可能已是新值）
            String previousConfigCode = aiModelConfigDb.getConfigCode();
            aiModelConfigDb.setMchId(aiModelConfig.getMchId());
            aiModelConfigDb.setApiId(aiModelConfig.getApiId());
            aiModelConfigDb.setVendorClass(aiModelConfig.getVendorClass());
            aiModelConfigDb.setModelType(aiModelConfig.getModelType());
            aiModelConfigDb.setModelTag(aiModelConfig.getModelTag());
            aiModelConfigDb.setConfigCode(aiModelConfig.getConfigCode());
            aiModelConfigDb.setConfigName(aiModelConfig.getConfigName());
            aiModelConfigDb.setConfigDesc(aiModelConfig.getConfigDesc());
            aiModelConfigDb.setModelName(aiModelConfig.getModelName());
            aiModelConfigDb.setModelData(aiModelConfig.getModelData());
            aiModelConfigDb.setModifyDate(SystemClock.nowDate());
            return dao.update( aiModelConfigDb ).onSuccess(updatedEntity -> {
                AiVendorHelper.invalidateConfig(aiModelConfigDb.getId(), previousConfigCode);
                SysDataHistoryHelper.saveHistory( aiModelConfigDb,remark );
            } );
        } );
    }

    /**
     * 启用AI模型配置（状态：禁用 → 启用），并失效 Vendor 客户端缓存。
     *
     * @param id     主键ID
     * @param remark 操作备注
     * @return 操作结果
     */
    @PutMapping("/enable")
    @Operation(summary = "启用AI模型配置", description = "启用AI模型配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelConfig.class,id,remark);
        return dao.update(new AiModelConfig().modifyDate(SystemClock.nowDate()).state(CommonState.ENABLED.getValue()), new AuthIdStateQueryParam(id, CommonState.DISABLED.getValue())).onSuccess(() -> AiVendorHelper.invalidateConfig(id));
    }

    /**
     * 禁用AI模型配置（状态：启用 → 禁用），并失效 Vendor 客户端缓存。
     *
     * @param id     主键ID
     * @param remark 操作备注
     * @return 操作结果
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用AI模型配置", description = "禁用AI模型配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelConfig.class,id,remark);
        return dao.update(new AiModelConfig().modifyDate(SystemClock.nowDate()).state(CommonState.DISABLED.getValue()), new AuthIdStateQueryParam(id, CommonState.ENABLED.getValue())).onSuccess(() -> AiVendorHelper.invalidateConfig(id));
    }

    /**
     * 删除AI模型配置（软删除：状态 → 已删除），并失效 Vendor 客户端缓存。
     *
     * @param id     主键ID
     * @param remark 操作备注
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除AI模型配置", description = "删除AI模型配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelConfig.class,id,remark);
        return dao.update(new AiModelConfig().modifyDate(SystemClock.nowDate()).state(CommonState.DELETED.getValue()), new AuthIdStateQueryParam(id, CommonState.DISABLED.getValue())).onSuccess(() -> AiVendorHelper.invalidateConfig(id));
    }
}
