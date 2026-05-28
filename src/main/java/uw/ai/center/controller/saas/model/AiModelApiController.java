package uw.ai.center.controller.saas.model;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiApiConfigQueryParam;
import uw.ai.center.entity.AiModelApi;
import uw.auth.service.AuthServiceHelper;

import java.time.Duration;
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

/**
 * AI模型API配置管理。
 */
@RestController
@RequestMapping("/saas/model/api")
@Tag(name = "AI模型API配置管理", description = "AI模型API配置增删改查列管理")
@MscPermDeclare(user = UserType.SAAS)
public class AiModelApiController {

    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 本地缓存。
     */
    private static final Cache<String, Object> modelCache = Caffeine.newBuilder()
            .maximumSize(200)
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    private static String loadKey(long id) {
        return "load_aiModelApi_" + id;
    }

    private static final String LITE_LIST_KEY = "liteList_aiModelApi";

    /**
     * 列表AI模型API配置。
     */
    @GetMapping("/list")
    @Operation(summary = "列表AI模型API配置", description = "列表AI模型API配置")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<DataList<AiModelApi>> list(AiApiConfigQueryParam queryParam){
        AuthServiceHelper.logRef(AiModelApi.class);
        return dao.list(AiModelApi.class, queryParam);
    }

    /**
     * 轻量级列表AI模型API配置，一般用于select控件。
     */
    @GetMapping("/liteList")
    @Operation(summary = "轻量级列表AI模型API配置", description = "轻量级列表AI模型API配置，一般用于select控件。")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.USER, log = ActionLog.NONE)
    public ResponseData<DataList<AiModelApi>> liteList(AiApiConfigQueryParam queryParam){
        DataList<AiModelApi> cached = (DataList<AiModelApi>) modelCache.getIfPresent(LITE_LIST_KEY);
        if (cached != null) {
            return ResponseData.success(cached);
        }
        queryParam.SELECT_SQL( "SELECT id,saas_id,mch_id,api_code,api_name,api_url,api_key,state,create_date,modify_date from ai_model_api " );
        return dao.list(AiModelApi.class, queryParam).onSuccess(list -> {
            modelCache.put(LITE_LIST_KEY, list);
        });
    }

    /**
     * 加载AI模型API配置。
     */
    @GetMapping("/load")
    @Operation(summary = "加载AI模型API配置", description = "加载AI模型API配置")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<AiModelApi> load(@Parameter(description = "主键ID", required = true) @RequestParam long id)  {
        AuthServiceHelper.logRef(AiModelApi.class,id);
        String cacheKey = loadKey(id);
        AiModelApi cached = (AiModelApi) modelCache.getIfPresent(cacheKey);
        if (cached != null) {
            return ResponseData.success(cached);
        }
        return dao.queryForSingleObject(AiModelApi.class, new AuthIdQueryParam(id)).onSuccess(api -> {
            modelCache.put(cacheKey, api);
        });
    }

    /**
     * 查询数据历史。
     */
    @GetMapping("/listDataHistory")
    @Operation(summary = "查询数据历史", description = "查询数据历史")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<DataList<SysDataHistory>> listDataHistory(SysDataHistoryQueryParam queryParam){
        AuthServiceHelper.logRef(AiModelApi.class, queryParam.getEntityId());
        queryParam.setEntityClass(AiModelApi.class);
        return dao.list(SysDataHistory.class, queryParam);
    }

    /**
     * 查询操作日志。
     */
    @GetMapping("/listCritLog")
    @Operation(summary = "查询操作日志", description = "查询操作日志")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<DataList<SysCritLog>> listCritLog(SysCritLogQueryParam queryParam)  {
        AuthServiceHelper.logRef(AiModelApi.class, queryParam.getBizId());
        queryParam.setBizTypeClass(AiModelApi.class);
        return dao.list(SysCritLog.class, queryParam);
    }

    /**
     * 新增AI模型API配置。
     */
    @PostMapping("/save")
    @Operation(summary = "新增AI模型API配置", description = "新增AI模型API配置")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiModelApi> save(@RequestBody AiModelApi aiModelApi){
        long id = dao.getSequenceId(AiModelApi.class);
        AuthServiceHelper.logRef(AiModelApi.class,id);
        aiModelApi.setId(id);
        aiModelApi.setSaasId(AuthServiceHelper.getSaasId());
        aiModelApi.setCreateDate(SystemClock.nowDate());
        aiModelApi.setModifyDate(null);
        aiModelApi.setState(CommonState.ENABLED.getValue());
        return dao.save( aiModelApi ).onSuccess(savedEntity -> {
            modelCache.invalidate(LITE_LIST_KEY);
            SysDataHistoryHelper.saveHistory(aiModelApi);
        });
    }

    /**
     * 修改AI模型API配置。
     */
    @PutMapping("/update")
    @Operation(summary = "修改AI模型API配置", description = "修改AI模型API配置")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiModelApi> update(@RequestBody AiModelApi aiModelApi, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelApi.class,aiModelApi.getId(),remark);
        return dao.queryForSingleObject( AiModelApi.class,new AuthIdQueryParam(aiModelApi.getId()) ).onSuccess(aiModelApiDb-> {
            aiModelApiDb.setMchId(aiModelApi.getMchId());
            aiModelApiDb.setApiCode(aiModelApi.getApiCode());
            aiModelApiDb.setApiName(aiModelApi.getApiName());
            aiModelApiDb.setApiDesc(aiModelApi.getApiDesc());
            aiModelApiDb.setApiUrl(aiModelApi.getApiUrl());
            aiModelApiDb.setApiKey(aiModelApi.getApiKey());
            aiModelApiDb.setModifyDate(SystemClock.nowDate());
            return dao.update( aiModelApiDb ).onSuccess(updatedEntity -> {
                modelCache.invalidate(loadKey(aiModelApi.getId()));
                modelCache.invalidate(LITE_LIST_KEY);
                SysDataHistoryHelper.saveHistory( aiModelApiDb,remark );
            } );
        } );
    }

    /**
     * 启用AI模型API配置。
     */
    @PutMapping("/enable")
    @Operation(summary = "启用AI模型API配置", description = "启用AI模型API配置")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelApi.class,id,remark);
        modelCache.invalidate(loadKey(id));
        modelCache.invalidate(LITE_LIST_KEY);
        return dao.update(new AiModelApi().modifyDate(SystemClock.nowDate()).state(CommonState.ENABLED.getValue()), new AuthIdStateQueryParam(id, CommonState.DISABLED.getValue()));
    }

    /**
     * 禁用AI模型API配置。
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用AI模型API配置", description = "禁用AI模型API配置")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelApi.class,id,remark);
        modelCache.invalidate(loadKey(id));
        modelCache.invalidate(LITE_LIST_KEY);
        return dao.update(new AiModelApi().modifyDate(SystemClock.nowDate()).state(CommonState.DISABLED.getValue()), new AuthIdStateQueryParam(id, CommonState.ENABLED.getValue()));
    }

    /**
     * 删除AI模型API配置。
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除AI模型API配置", description = "删除AI模型API配置")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelApi.class,id,remark);
        modelCache.invalidate(loadKey(id));
        modelCache.invalidate(LITE_LIST_KEY);
        return dao.update(new AiModelApi().modifyDate(SystemClock.nowDate()).state(CommonState.DELETED.getValue()), new AuthIdStateQueryParam(id, CommonState.DISABLED.getValue()));
    }
}