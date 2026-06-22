package uw.ai.center.controller.ops.model;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiApiConfigQueryParam;
import uw.ai.center.entity.AiModelApi;
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
import uw.common.response.ResponseData;
import uw.common.util.SystemClock;
import uw.dao.DaoManager;
import uw.common.data.PageList;

/**
 * AI模型API配置管理。
 * <p>运维（OPS）角色的 AI 模型 API 配置增删改查接口，路径前缀 {@code /ops/model/api}。
 */
@RestController
@RequestMapping("/ops/model/api")
@Tag(name = "AI模型API配置管理", description = "AI模型API配置增删改查列管理")
@MscPermDeclare(user = UserType.OPS)
public class AiModelApiController {

    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 分页列表AI模型API配置。
     *
     * @param queryParam 查询参数
     * @return API 配置分页列表
     */
    @GetMapping("/list")
    @Operation(summary = "列表AI模型API配置", description = "列表AI模型API配置")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.USER, log = ActionLog.REQUEST)
    public ResponseData<PageList<AiModelApi>> list(AiApiConfigQueryParam queryParam){
        AuthServiceHelper.logRef(AiModelApi.class);
        return dao.list(AiModelApi.class, queryParam);
    }

    /**
     * 轻量级列表AI模型API配置（仅关键列，apiKey 掩码），一般用于前端 select 控件。
     *
     * @param queryParam 查询参数
     * @return API 配置分页列表（精简字段，apiKey 已掩码）
     */
    @GetMapping("/listLite")
    @Operation(summary = "轻量级列表AI模型API配置", description = "轻量级列表AI模型API配置，一般用于select控件。")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.USER, log = ActionLog.NONE)
    public ResponseData<PageList<AiModelApi>> listLite(AiApiConfigQueryParam queryParam){
        queryParam.SELECT_SQL( "SELECT id,saas_id,mch_id,api_code,api_name,api_url,api_key,state,create_date,modify_date from ai_model_api " );
        return dao.list(AiModelApi.class, queryParam).onSuccess(dataList -> {
            for (AiModelApi item : dataList) {
                item.setApiKey(AiModelApi.maskApiKey(item.getApiKey()));
            }
        });
    }

    /**
     * 按主键加载单条AI模型API配置。
     *
     * @param id 主键ID
     * @return API 配置
     */
    @GetMapping("/load")
    @Operation(summary = "加载AI模型API配置", description = "加载AI模型API配置")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<AiModelApi> load(@Parameter(description = "主键ID", required = true) @RequestParam long id)  {
        AuthServiceHelper.logRef(AiModelApi.class,id);
        return dao.queryForObject(AiModelApi.class, new AuthIdQueryParam(id));
    }

    /**
     * 查询指定AI模型API配置的数据变更历史。
     *
     * @param queryParam 历史查询参数（按 entityId 过滤）
     * @return 数据历史分页列表
     */
    @GetMapping("/listDataHistory")
    @Operation(summary = "查询数据历史", description = "查询数据历史")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<SysDataHistory>> listDataHistory(SysDataHistoryQueryParam queryParam){
        AuthServiceHelper.logRef(AiModelApi.class, queryParam.getEntityId());
        queryParam.setEntityClass(AiModelApi.class);
        return dao.list(SysDataHistory.class, queryParam);
    }

    /**
     * 查询指定AI模型API配置的关键操作日志。
     *
     * @param queryParam 日志查询参数（按 bizId 过滤）
     * @return 操作日志分页列表
     */
    @GetMapping("/listCritLog")
    @Operation(summary = "查询操作日志", description = "查询操作日志")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<SysCritLog>> listCritLog(SysCritLogQueryParam queryParam)  {
        AuthServiceHelper.logRef(AiModelApi.class, queryParam.getBizId());
        queryParam.setBizTypeClass(AiModelApi.class);
        return dao.list(SysCritLog.class, queryParam);
    }

    /**
     * 新增AI模型API配置。
     * <p>apiCode 非空时做全局唯一性校验；saasId 绑定当前租户；保存后记录数据历史。
     *
     * @param aiModelApi API 配置（apiCode/apiName/apiUrl/apiKey 等）
     * @return 保存后的 API 配置
     */
    @PostMapping("/save")
    @Operation(summary = "新增AI模型API配置", description = "新增AI模型API配置")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiModelApi> save(@RequestBody AiModelApi aiModelApi){
        if (StringUtils.isNotBlank(aiModelApi.getApiCode())) {
            long count = dao.queryForValue(Long.class, "select count(*) from ai_model_api where api_code=? and state=?", new Object[]{aiModelApi.getApiCode(), CommonState.ENABLED.getValue()}).getData();
            if (count > 0) {
                return ResponseData.errorMsg("配置代码[" + aiModelApi.getApiCode() + "]已存在！");
            }
        }
        long id = dao.getSequenceId(AiModelApi.class);
        AuthServiceHelper.logRef(AiModelApi.class,id);
        aiModelApi.setId(id);
        aiModelApi.setSaasId(AuthServiceHelper.getSaasId());
        aiModelApi.setCreateDate(SystemClock.nowDate());
        aiModelApi.setModifyDate(null);
        aiModelApi.setState(CommonState.ENABLED.getValue());
        return dao.save( aiModelApi ).onSuccess(savedEntity -> {
            SysDataHistoryHelper.saveHistory(aiModelApi);
        });
    }

    /**
     * 修改AI模型API配置。
     * <p>apiCode 唯一性校验排除自身（id&lt;&gt;?）；更新后级联失效关联模型配置缓存并记录数据历史。
     *
     * @param aiModelApi 待更新的 API 配置
     * @param remark     操作备注（记入日志与历史）
     * @return 更新后的 API 配置
     */
    @PutMapping("/update")
    @Operation(summary = "修改AI模型API配置", description = "修改AI模型API配置")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiModelApi> update(@RequestBody AiModelApi aiModelApi, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelApi.class,aiModelApi.getId(),remark);
        // 判重：配置代码在未删除记录中必须全局唯一（排除自身，避免修改自身时误判为重复）
        if (StringUtils.isNotBlank(aiModelApi.getApiCode())) {
            long count = dao.queryForValue(Long.class, "select count(*) from ai_model_api where api_code=? and state=? and id<>?", new Object[]{aiModelApi.getApiCode(), CommonState.ENABLED.getValue(), aiModelApi.getId()}).getData();
            if (count > 0) {
                return ResponseData.errorMsg("配置代码[" + aiModelApi.getApiCode() + "]已存在！");
            }
        }
        return dao.queryForObject( AiModelApi.class,new AuthIdQueryParam(aiModelApi.getId()) ).onSuccess(aiModelApiDb-> {
            String previousApiCode = aiModelApiDb.getApiCode();
            aiModelApiDb.setMchId(aiModelApi.getMchId());
            aiModelApiDb.setApiCode(aiModelApi.getApiCode());
            aiModelApiDb.setApiName(aiModelApi.getApiName());
            aiModelApiDb.setApiDesc(aiModelApi.getApiDesc());
            aiModelApiDb.setApiUrl(aiModelApi.getApiUrl());
            aiModelApiDb.setApiKey(aiModelApi.getApiKey());
            aiModelApiDb.setModifyDate(SystemClock.nowDate());
            return dao.update( aiModelApiDb ).onSuccess(updatedEntity -> {
                AiVendorHelper.invalidateApiConfig(aiModelApiDb.getId(), previousApiCode);
                SysDataHistoryHelper.saveHistory( aiModelApiDb,remark );
            } );
        } );
    }

    /**
     * 启用AI模型API配置（状态：禁用 → 启用），并级联失效关联模型配置缓存。
     *
     * @param id     主键ID
     * @param remark 操作备注
     * @return 操作结果
     */
    @PutMapping("/enable")
    @Operation(summary = "启用AI模型API配置", description = "启用AI模型API配置")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelApi.class,id,remark);
        return dao.update(new AiModelApi().modifyDate(SystemClock.nowDate()).state(CommonState.ENABLED.getValue()), new AuthIdStateQueryParam(id, CommonState.DISABLED.getValue())).onSuccess(() -> AiVendorHelper.invalidateApiConfig(id));
    }

    /**
     * 禁用AI模型API配置（状态：启用 → 禁用），并级联失效关联模型配置缓存。
     *
     * @param id     主键ID
     * @param remark 操作备注
     * @return 操作结果
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用AI模型API配置", description = "禁用AI模型API配置")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelApi.class,id,remark);
        return dao.update(new AiModelApi().modifyDate(SystemClock.nowDate()).state(CommonState.DISABLED.getValue()), new AuthIdStateQueryParam(id, CommonState.ENABLED.getValue())).onSuccess(() -> AiVendorHelper.invalidateApiConfig(id));
    }

    /**
     * 删除AI模型API配置（软删除：状态 → 已删除），并级联失效关联模型配置缓存。
     *
     * @param id     主键ID
     * @param remark 操作备注
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除AI模型API配置", description = "删除AI模型API配置")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelApi.class,id,remark);
        return dao.update(new AiModelApi().modifyDate(SystemClock.nowDate()).state(CommonState.DELETED.getValue()), new AuthIdStateQueryParam(id, CommonState.DISABLED.getValue())).onSuccess(() -> AiVendorHelper.invalidateApiConfig(id));
    }
}
