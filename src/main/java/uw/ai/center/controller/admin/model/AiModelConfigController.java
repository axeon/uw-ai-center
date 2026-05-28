package uw.ai.center.controller.admin.model;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiModelConfigQueryParam;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.center.vendor.AiVendorHelper;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.common.app.constant.CommonState;
import uw.common.app.dto.*;
import uw.common.app.entity.*;
import uw.common.app.helper.SysDataHistoryHelper;
import uw.common.dto.ResponseData;
import uw.common.util.SystemClock;
import uw.dao.DaoManager;
import uw.dao.DataList;

import java.time.Duration;

/**
 * AI模型配置管理。
 */
@RestController
@RequestMapping("/admin/model/config")
@Tag(name = "AI模型配置管理", description = "AI模型配置增删改查列管理")
@MscPermDeclare(user = UserType.ADMIN)
public class AiModelConfigController {

    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 本地缓存。
     */
    private static final Cache<String, Object> modelCache = Caffeine.newBuilder()
            .maximumSize(200)
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    private static String loadKey(long id) {
        return "load_aiModelConfig_" + id;
    }

    private static final String LITE_LIST_KEY = "liteList_aiModelConfig";

    @GetMapping("/list")
    @Operation(summary = "列表AI模型配置", description = "列表AI模型配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<DataList<AiModelConfig>> list(AiModelConfigQueryParam queryParam){
        AuthServiceHelper.logRef(AiModelConfig.class);
        return dao.list(AiModelConfig.class, queryParam);
    }

    @GetMapping("/liteList")
    @Operation(summary = "轻量级列表AI模型配置", description = "轻量级列表AI模型配置，一般用于select控件。")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.USER, log = ActionLog.NONE)
    public ResponseData<DataList<AiModelConfig>> liteList(AiModelConfigQueryParam queryParam){
        DataList<AiModelConfig> cached = (DataList<AiModelConfig>) modelCache.getIfPresent(LITE_LIST_KEY);
        if (cached != null) {
            return ResponseData.success(cached);
        }
        queryParam.SELECT_SQL( "SELECT id,saas_id,mch_id,api_id,vendor_class,model_type,config_code,config_name,model_name,state,create_date,modify_date from ai_model_config " );
        return dao.list(AiModelConfig.class, queryParam).onSuccess(list -> {
            modelCache.put(LITE_LIST_KEY, list);
        });
    }

    @GetMapping("/load")
    @Operation(summary = "加载AI模型配置", description = "加载AI模型配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<AiModelConfig> load(@Parameter(description = "主键ID", required = true) @RequestParam long id)  {
        AuthServiceHelper.logRef(AiModelConfig.class,id);
        String cacheKey = loadKey(id);
        AiModelConfig cached = (AiModelConfig) modelCache.getIfPresent(cacheKey);
        if (cached != null) {
            return ResponseData.success(cached);
        }
        return dao.queryForSingleObject(AiModelConfig.class, new AuthIdQueryParam(id)).onSuccess(config -> {
            modelCache.put(cacheKey, config);
        });
    }

    @GetMapping("/listDataHistory")
    @Operation(summary = "查询数据历史", description = "查询数据历史")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<DataList<SysDataHistory>> listDataHistory(SysDataHistoryQueryParam queryParam){
        AuthServiceHelper.logRef(AiModelConfig.class, queryParam.getEntityId());
        queryParam.setEntityClass(AiModelConfig.class);
        return dao.list(SysDataHistory.class, queryParam);
    }

    @GetMapping("/listCritLog")
    @Operation(summary = "查询操作日志", description = "查询操作日志")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<DataList<SysCritLog>> listCritLog(SysCritLogQueryParam queryParam)  {
        AuthServiceHelper.logRef(AiModelConfig.class, queryParam.getBizId());
        queryParam.setBizTypeClass(AiModelConfig.class);
        return dao.list(SysCritLog.class, queryParam);
    }

    @PostMapping("/save")
    @Operation(summary = "新增AI模型配置", description = "新增AI模型配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiModelConfig> save(@RequestBody AiModelConfig aiModelConfig){
        long id = dao.getSequenceId(AiModelConfig.class);
        AuthServiceHelper.logRef(AiModelConfig.class,id);
        aiModelConfig.setId(id);
        aiModelConfig.setSaasId(AuthServiceHelper.getSaasId());
        aiModelConfig.setCreateDate(SystemClock.nowDate());
        aiModelConfig.setModifyDate(null);
        aiModelConfig.setState(CommonState.ENABLED.getValue());
        return dao.save( aiModelConfig ).onSuccess(savedEntity -> {
            modelCache.invalidate(LITE_LIST_KEY);
            SysDataHistoryHelper.saveHistory(aiModelConfig);
        });
    }

    @PutMapping("/update")
    @Operation(summary = "修改AI模型配置", description = "修改AI模型配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiModelConfig> update(@RequestBody AiModelConfig aiModelConfig, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelConfig.class,aiModelConfig.getId(),remark);
        return dao.queryForSingleObject( AiModelConfig.class,new AuthIdQueryParam(aiModelConfig.getId()) ).onSuccess(aiModelConfigDb-> {
            aiModelConfigDb.setMchId(aiModelConfig.getMchId());
            aiModelConfigDb.setApiId(aiModelConfig.getApiId());
            aiModelConfigDb.setVendorClass(aiModelConfig.getVendorClass());
            aiModelConfigDb.setModelType(aiModelConfig.getModelType());
            aiModelConfigDb.setConfigCode(aiModelConfig.getConfigCode());
            aiModelConfigDb.setConfigName(aiModelConfig.getConfigName());
            aiModelConfigDb.setConfigDesc(aiModelConfig.getConfigDesc());
            aiModelConfigDb.setModelName(aiModelConfig.getModelName());
            aiModelConfigDb.setModelData(aiModelConfig.getModelData());
            aiModelConfigDb.setModifyDate(SystemClock.nowDate());
            return dao.update( aiModelConfigDb ).onSuccess(updatedEntity -> {
                AiVendorHelper.invalidateConfig(aiModelConfigDb.getId());
                modelCache.invalidate(loadKey(aiModelConfig.getId()));
                modelCache.invalidate(LITE_LIST_KEY);
                SysDataHistoryHelper.saveHistory( aiModelConfigDb,remark );
            } );
        } );
    }

    @PutMapping("/enable")
    @Operation(summary = "启用AI模型配置", description = "启用AI模型配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelConfig.class,id,remark);
        modelCache.invalidate(loadKey(id));
        modelCache.invalidate(LITE_LIST_KEY);
        return dao.update(new AiModelConfig().modifyDate(SystemClock.nowDate()).state(CommonState.ENABLED.getValue()), new AuthIdStateQueryParam(id, CommonState.DISABLED.getValue()));
    }

    @PutMapping("/disable")
    @Operation(summary = "禁用AI模型配置", description = "禁用AI模型配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelConfig.class,id,remark);
        modelCache.invalidate(loadKey(id));
        modelCache.invalidate(LITE_LIST_KEY);
        return dao.update(new AiModelConfig().modifyDate(SystemClock.nowDate()).state(CommonState.DISABLED.getValue()), new AuthIdStateQueryParam(id, CommonState.ENABLED.getValue()));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除AI模型配置", description = "删除AI模型配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelConfig.class,id,remark);
        modelCache.invalidate(loadKey(id));
        modelCache.invalidate(LITE_LIST_KEY);
        return dao.update(new AiModelConfig().modifyDate(SystemClock.nowDate()).state(CommonState.DELETED.getValue()), new AuthIdStateQueryParam(id, CommonState.DISABLED.getValue()));
    }
}