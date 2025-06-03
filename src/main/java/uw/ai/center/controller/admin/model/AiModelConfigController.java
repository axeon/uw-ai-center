package uw.ai.center.controller.admin.model;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiModelConfigQueryParam;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vendor.AiVendorHelper;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.common.app.constant.CommonState;
import uw.common.app.dto.AuthIdQueryParam;
import uw.common.app.dto.IdStateQueryParam;
import uw.common.app.dto.SysCritLogQueryParam;
import uw.common.app.dto.SysDataHistoryQueryParam;
import uw.common.app.entity.SysCritLog;
import uw.common.app.entity.SysDataHistory;
import uw.common.app.helper.SysDataHistoryHelper;
import uw.common.dto.ResponseData;
import uw.common.util.SystemClock;
import uw.dao.DaoManager;
import uw.dao.DataList;
import uw.dao.TransactionException;

import java.util.List;


/**
 * AI服务模型管理。
 */
@RestController
@RequestMapping("/admin/model/config")
@Tag(name = "AI服务模型管理", description = "AI服务模型增删改查列管理")
@MscPermDeclare(user = UserType.ADMIN)
public class AiModelConfigController {

    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 列表AI服务模型。
     *
     * @param queryParam
     * @return
     *
     */
    @GetMapping("/list")
    @Operation(summary = "列表AI服务模型", description = "列表AI服务模型")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<DataList<AiModelConfig>> list(AiModelConfigQueryParam queryParam){
        AuthServiceHelper.logRef(AiModelConfig.class);
        return dao.list(AiModelConfig.class, queryParam);
    }

    /**
     * 轻量级列表AI服务模型，一般用于select控件。
     *
     * @return
     */
    @GetMapping("/liteList")
    @Operation(summary = "轻量级列表AI服务模型", description = "轻量级列表AI服务模型，一般用于select控件。")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.USER, log = ActionLog.NONE)
    public ResponseData<DataList<AiModelConfig>> liteList(AiModelConfigQueryParam queryParam){
        queryParam.SELECT_SQL( "SELECT id,saas_id,mch_id,vendor_class,config_code,config_name,api_url,api_key,model_main,model_embed,create_date,modify_date,state from ai_model_config " );
        return dao.list(AiModelConfig.class, queryParam);
    }

    /**
     * 加载AI服务模型。
     *
     * @param id
     *
     */
    @GetMapping("/load")
    @Operation(summary = "加载AI服务模型", description = "加载AI服务模型")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<AiModelConfig> load(@Parameter(description = "主键ID", required = true) @RequestParam long id)  {
        AuthServiceHelper.logRef(AiModelConfig.class,id);
        return dao.queryForSingleObject(AiModelConfig.class, new AuthIdQueryParam(id));
    }

    /**
     * 查询数据历史。
     *
     * @param
     * @return
     */
    @GetMapping("/listDataHistory")
    @Operation(summary = "查询数据历史", description = "查询数据历史")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<DataList<SysDataHistory>> listDataHistory(SysDataHistoryQueryParam queryParam){
        AuthServiceHelper.logRef(AiModelConfig.class, queryParam.getEntityId());
        queryParam.setEntityClass(AiModelConfig.class);
        return dao.list(SysDataHistory.class, queryParam);
    }

    /**
     * 查询操作日志。
     *
     * @param
     * @return
     */
    @GetMapping("/listCritLog")
    @Operation(summary = "查询操作日志", description = "查询操作日志")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<DataList<SysCritLog>> listCritLog(SysCritLogQueryParam queryParam)  {
        AuthServiceHelper.logRef(AiModelConfig.class, queryParam.getBizId());
        queryParam.setBizTypeClass(AiModelConfig.class);
        return dao.list(SysCritLog.class, queryParam);
    }

    /**
     * 列表AI服务。
     *
     * @return
     * @throws TransactionException
     */
    @GetMapping("/listVendor")
    @Operation(summary = "列表AI服务", description = "列表AI服务")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.USER, log = ActionLog.NONE)
    public List<AiVendor> listVendor() {
        return AiVendorHelper.getVendorMap().values().stream().toList();
    }

    /**
     * 列表AI服务。
     *
     * @return
     * @throws TransactionException
     */
    @GetMapping("/listModel")
    @Operation(summary = "列表模型列表", description = "列表模型列表")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.USER, log = ActionLog.NONE)
    public List<String> listModel(@Parameter(description = "vendorClass", required = true) @RequestParam String vendorClass,
                                  @Parameter(description = "apiUrl", required = true) @RequestParam String apiUrl,
                                  @Parameter(description = "apiKey", required = false) @RequestParam(required = false) String apiKey) {
        return AiVendorHelper.listModel( vendorClass, apiUrl, apiKey );
    }

    /**
     * 新增AI服务模型。
     *
     * @param aiModelConfig
     * @return
     *
     */
    @PostMapping("/save")
    @Operation(summary = "新增AI服务模型", description = "新增AI服务模型")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiModelConfig> save(@RequestBody AiModelConfig aiModelConfig){
        long id = dao.getSequenceId(AiModelConfig.class);
        AuthServiceHelper.logRef(AiModelConfig.class,id);
        aiModelConfig.setId(id);
        aiModelConfig.setSaasId(AuthServiceHelper.getSaasId());
        aiModelConfig.setCreateDate(SystemClock.nowDate());
        aiModelConfig.setModifyDate(null);
        aiModelConfig.setState(CommonState.ENABLED.getValue());
        //保存历史记录
        return dao.save( aiModelConfig ).onSuccess(savedEntity -> {
            SysDataHistoryHelper.saveHistory(aiModelConfig);
        });
    }

    /**
     * 修改AI服务模型。
     *
     * @param aiModelConfig
     * @return
     *
     */
    @PutMapping("/update")
    @Operation(summary = "修改AI服务模型", description = "修改AI服务模型")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiModelConfig> update(@RequestBody AiModelConfig aiModelConfig, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelConfig.class,aiModelConfig.getId(),remark);
        return  dao.load( AiModelConfig.class, aiModelConfig.getId() ).onSuccess(aiModelConfigDb-> {
            aiModelConfigDb.setMchId(aiModelConfig.getMchId());
            aiModelConfigDb.setVendorClass(aiModelConfig.getVendorClass());
            aiModelConfigDb.setConfigCode(aiModelConfig.getConfigCode());
            aiModelConfigDb.setConfigName(aiModelConfig.getConfigName());
            aiModelConfigDb.setConfigDesc(aiModelConfig.getConfigDesc());
            aiModelConfigDb.setApiUrl(aiModelConfig.getApiUrl());
            aiModelConfigDb.setApiKey(aiModelConfig.getApiKey());
            aiModelConfigDb.setModelMain(aiModelConfig.getModelMain());
            aiModelConfigDb.setModelEmbed(aiModelConfig.getModelEmbed());
            aiModelConfigDb.setVendorData(aiModelConfig.getVendorData());
            aiModelConfigDb.setModelData(aiModelConfig.getModelData());
            aiModelConfigDb.setEmbedData(aiModelConfig.getEmbedData());
            aiModelConfigDb.setModifyDate(SystemClock.nowDate());
            return dao.update( aiModelConfigDb ).onSuccess(updatedEntity -> {
                SysDataHistoryHelper.saveHistory( aiModelConfigDb,remark );
            } );
        } );
    }



    /**
     * 启用AI服务模型。
     *
     * @param id
     *
     */
    @PutMapping("/enable")
    @Operation(summary = "启用AI服务模型", description = "启用AI服务模型")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelConfig.class,id,remark);
        return dao.update(new AiModelConfig().modifyDate(SystemClock.nowDate()).state(CommonState.ENABLED.getValue()), new IdStateQueryParam(id, CommonState.DISABLED.getValue()));
    }

    /**
     * 禁用AI服务模型。
     *
     * @param id
     *
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用AI服务模型", description = "禁用AI服务模型")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelConfig.class,id,remark);
        return dao.update(new AiModelConfig().modifyDate(SystemClock.nowDate()).state(CommonState.DISABLED.getValue()), new IdStateQueryParam(id, CommonState.ENABLED.getValue()));
    }

    /**
     * 删除AI服务模型。
     *
     * @param id
     *
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除AI服务模型", description = "删除AI服务模型")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiModelConfig.class,id,remark);
        return dao.update(new AiModelConfig().modifyDate(SystemClock.nowDate()).state(CommonState.DELETED.getValue()), new IdStateQueryParam(id, CommonState.DISABLED.getValue()));
    }
}