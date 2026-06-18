package uw.ai.center.controller.saas.model;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiModelConfigQueryParam;
import uw.ai.center.entity.AiModelApi;
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
 */
@RestController
@RequestMapping("/saas/model/config")
@Tag(name = "AI模型配置管理", description = "AI模型配置增删改查列管理")
@MscPermDeclare(user = UserType.SAAS)
public class AiModelConfigController {

    private final DaoManager dao = DaoManager.getInstance();

    @GetMapping("/listVendor")
    @Operation(summary = "列表AI服务", description = "列表AI服务")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.USER, log = ActionLog.NONE)
    public List<AiVendorInfo> listVendor() {
        return AiVendorHelper.getVendorMap().values().stream().map(AiVendorInfo::new).toList();
    }

    @GetMapping("/listModel")
    @Operation(summary = "列表模型列表", description = "列表模型列表")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.USER, log = ActionLog.NONE)
    public ResponseData<List<String>> listModel(@Parameter(description = "vendorClass", required = true) @RequestParam String vendorClass,
                                                 @Parameter(description = "apiId", required = true) @RequestParam long apiId) {
        // 通过 apiId 从库中取 apiUrl/apiKey（带 saasId 校验），避免 SSRF
        AiModelApi apiConfig = dao.queryForObject(AiModelApi.class,
                "select * from ai_model_api where id=? and saas_id=? and state=?", new Object[]{apiId, AuthServiceHelper.getSaasId(), CommonState.ENABLED.getValue()}).getData();
        if (apiConfig == null) {
            return ResponseData.errorMsg("API配置不存在或无权访问");
        }
        return ResponseData.success(AiVendorHelper.listModel(vendorClass, apiConfig.getApiUrl(), apiConfig.getApiKey()));
    }

    @GetMapping("/list")
    @Operation(summary = "列表AI模型配置", description = "列表AI模型配置")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<AiModelConfig>> list(AiModelConfigQueryParam queryParam){
        AuthServiceHelper.logRef(AiModelConfig.class);
        return dao.list(AiModelConfig.class, queryParam);
    }

    @GetMapping("/listLite")
    @Operation(summary = "轻量级列表AI模型配置", description = "轻量级列表AI模型配置，一般用于select控件。")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.USER, log = ActionLog.NONE)
    public ResponseData<PageList<AiModelConfig>> listLite(AiModelConfigQueryParam queryParam){
        queryParam.SELECT_SQL( "SELECT id,saas_id,mch_id,api_id,vendor_class,model_type,model_tag,config_code,config_name,model_name,state,create_date,modify_date from ai_model_config " );
        return dao.list(AiModelConfig.class, queryParam);
    }

    @GetMapping("/load")
    @Operation(summary = "加载AI模型配置", description = "加载AI模型配置")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<AiModelConfig> load(@Parameter(description = "主键ID", required = true) @RequestParam long id)  {
        AuthServiceHelper.logRef(AiModelConfig.class,id);
        return dao.queryForObject(AiModelConfig.class, new AuthIdQueryParam(id));
    }

    @GetMapping("/listDataHistory")
    @Operation(summary = "查询数据历史", description = "查询数据历史")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<SysDataHistory>> listDataHistory(SysDataHistoryQueryParam queryParam){
        AuthServiceHelper.logRef(AiModelConfig.class, queryParam.getEntityId());
        queryParam.setEntityClass(AiModelConfig.class);
        return dao.list(SysDataHistory.class, queryParam);
    }

    @GetMapping("/listCritLog")
    @Operation(summary = "查询操作日志", description = "查询操作日志")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<SysCritLog>> listCritLog(SysCritLogQueryParam queryParam)  {
        AuthServiceHelper.logRef(AiModelConfig.class, queryParam.getBizId());
        queryParam.setBizTypeClass(AiModelConfig.class);
        return dao.list(SysCritLog.class, queryParam);
    }

    @PostMapping("/save")
    @Operation(summary = "新增AI模型配置", description = "新增AI模型配置")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
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

    @PutMapping("/update")
    @Operation(summary = "修改AI模型配置", description = "修改AI模型配置")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiModelConfig> update(@RequestBody AiModelConfig aiModelConfig, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelConfig.class,aiModelConfig.getId(),remark);
        if (StringUtils.isNotBlank(aiModelConfig.getConfigCode())) {
            long count = dao.queryForValue(Long.class, "select count(*) from ai_model_config where config_code=? and state=? and id =?", new Object[]{aiModelConfig.getConfigCode(), CommonState.ENABLED.getValue(), aiModelConfig.getId()}).getData();
            if (count > 0) {
                return ResponseData.errorMsg("配置代码[" + aiModelConfig.getConfigCode() + "]已存在！");
            }
        }
        return dao.queryForObject( AiModelConfig.class,new AuthIdQueryParam(aiModelConfig.getId()) ).onSuccess(aiModelConfigDb-> {
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
                AiVendorHelper.invalidateConfig(aiModelConfigDb.getId());
                SysDataHistoryHelper.saveHistory( aiModelConfigDb,remark );
            } );
        } );
    }

    @PutMapping("/enable")
    @Operation(summary = "启用AI模型配置", description = "启用AI模型配置")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelConfig.class,id,remark);
        return dao.update(new AiModelConfig().modifyDate(SystemClock.nowDate()).state(CommonState.ENABLED.getValue()), new AuthIdStateQueryParam(id, CommonState.DISABLED.getValue())).onSuccess(() -> AiVendorHelper.invalidateConfig(id));
    }

    @PutMapping("/disable")
    @Operation(summary = "禁用AI模型配置", description = "禁用AI模型配置")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelConfig.class,id,remark);
        return dao.update(new AiModelConfig().modifyDate(SystemClock.nowDate()).state(CommonState.DISABLED.getValue()), new AuthIdStateQueryParam(id, CommonState.ENABLED.getValue())).onSuccess(() -> AiVendorHelper.invalidateConfig(id));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除AI模型配置", description = "删除AI模型配置")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelConfig.class,id,remark);
        return dao.update(new AiModelConfig().modifyDate(SystemClock.nowDate()).state(CommonState.DELETED.getValue()), new AuthIdStateQueryParam(id, CommonState.DISABLED.getValue())).onSuccess(() -> AiVendorHelper.invalidateConfig(id));
    }
}