package uw.ai.center.controller.ops.model;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiModelConfigQueryParam;
import uw.ai.center.entity.AiModelConfig;
import uw.app.common.dto.AuthIdQueryParam;
import uw.app.common.dto.SysCritLogQueryParam;
import uw.app.common.dto.SysDataHistoryQueryParam;
import uw.app.common.entity.SysCritLog;
import uw.app.common.entity.SysDataHistory;
import uw.app.common.helper.SysDataHistoryHelper;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.common.constant.StateCommon;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.common.dto.ResponseData;
import uw.dao.DaoFactory;
import uw.dao.DataList;
import uw.dao.TransactionException;

import java.util.Date;


/**
 * AI服务模型管理。
 */
@RestController
@RequestMapping("/ops/model/config")
@Tag(name = "AI服务模型管理", description = "AI服务模型增删改查列管理")
@MscPermDeclare(user = UserType.OPS)
public class AiModelConfigController {

    private final DaoFactory dao = DaoFactory.getInstance();

    /**
     * 列表AI服务模型。
     *
     * @param queryParam
     * @return
     * @throws TransactionException
     */
    @GetMapping("/list")
    @Operation(summary = "列表AI服务模型", description = "列表AI服务模型")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public DataList<AiModelConfig> list(AiModelConfigQueryParam queryParam) throws TransactionException {
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
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.USER, log = ActionLog.NONE)
    public DataList<AiModelConfig> liteList(AiModelConfigQueryParam queryParam) throws TransactionException {
        queryParam.SELECT_SQL( "SELECT id,saas_id,mch_id,vendor_class,model_code,model_name,create_date,modify_date,state from ai_model_config " );
        return dao.list(AiModelConfig.class, queryParam);
    }

    /**
     * 加载AI服务模型。
     *
     * @param id
     * @throws TransactionException
     */
    @GetMapping("/load")
    @Operation(summary = "加载AI服务模型", description = "加载AI服务模型")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public AiModelConfig load(@Parameter(description = "主键ID", required = true) @RequestParam long id) throws TransactionException {
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
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public DataList<SysDataHistory> listDataHistory(SysDataHistoryQueryParam queryParam) throws TransactionException {
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
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public DataList<SysCritLog> listCritLog(SysCritLogQueryParam queryParam) throws TransactionException {
        AuthServiceHelper.logRef(AiModelConfig.class, queryParam.getRefId());
        queryParam.setRefTypeClass(AiModelConfig.class);
        return dao.list(SysCritLog.class, queryParam);
    }

    /**
     * 新增AI服务模型。
     *
     * @param aiModelConfig
     * @return
     * @throws TransactionException
     */
    @PostMapping("/save")
    @Operation(summary = "新增AI服务模型", description = "新增AI服务模型")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiModelConfig> save(@RequestBody AiModelConfig aiModelConfig) throws TransactionException {
        long id = dao.getSequenceId(AiModelConfig.class);
        AuthServiceHelper.logRef(AiModelConfig.class,id);
        aiModelConfig.setId(id);
        aiModelConfig.setSaasId(AuthServiceHelper.getSaasId());
        aiModelConfig.setCreateDate(new Date());
        aiModelConfig.setModifyDate(null);
        aiModelConfig.setState(1);
        dao.save(aiModelConfig);
        //保存历史记录
        SysDataHistoryHelper.saveHistory(aiModelConfig.getId(),aiModelConfig,"AI服务模型","新增AI服务模型");
        return ResponseData.success(aiModelConfig);
    }

    /**
     * 修改AI服务模型。
     *
     * @param aiModelConfig
     * @return
     * @throws TransactionException
     */
    @PutMapping("/update")
    @Operation(summary = "修改AI服务模型", description = "修改AI服务模型")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiModelConfig> update(@RequestBody AiModelConfig aiModelConfig, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiModelConfig.class,aiModelConfig.getId(),"修改AI服务模型！操作备注："+remark);
        AiModelConfig aiModelConfigDb = dao.queryForSingleObject(AiModelConfig.class, new AuthIdQueryParam(aiModelConfig.getId()));
        if (aiModelConfigDb == null) {
            return ResponseData.warnMsg("未找到指定ID的AI服务模型！");
        }
        aiModelConfigDb.setMchId(aiModelConfig.getMchId());
        aiModelConfigDb.setVendorClass(aiModelConfig.getVendorClass());
        aiModelConfigDb.setModelCode(aiModelConfig.getModelCode());
        aiModelConfigDb.setModelName(aiModelConfig.getModelName());
        aiModelConfigDb.setModelDesc(aiModelConfig.getModelDesc());
        aiModelConfigDb.setPublicData(aiModelConfig.getPublicData());
        aiModelConfigDb.setModelData(aiModelConfig.getModelData());
        aiModelConfigDb.setLogData(aiModelConfig.getLogData());
        aiModelConfigDb.setModifyDate(new Date());
        dao.update(aiModelConfigDb);
        SysDataHistoryHelper.saveHistory(aiModelConfigDb.getId(),aiModelConfigDb,"AI服务模型","修改AI服务模型！操作备注："+remark);
        return ResponseData.success(aiModelConfigDb);
    }

    /**
     * 启用AI服务模型。
     *
     * @param id
     * @throws TransactionException
     */
    @PutMapping("/enable")
    @Operation(summary = "启用AI服务模型", description = "启用AI服务模型")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiModelConfig.class,id,"启用AI服务模型！操作备注："+remark);
        AiModelConfig aiModelConfig = dao.queryForSingleObject(AiModelConfig.class, new AuthIdQueryParam(id));
        if (aiModelConfig == null) {
            return ResponseData.warnMsg("未找到指定id的AI服务模型！");
        }
        if (aiModelConfig.getState()!=StateCommon.DISABLED.getValue()){
            return ResponseData.warnMsg("启用AI服务模型失败！当前状态不是禁用状态！");
        }
        aiModelConfig.setModifyDate(new Date());
        aiModelConfig.setState(StateCommon.ENABLED.getValue());
        dao.update(aiModelConfig);
        return ResponseData.success();
    }

    /**
     * 禁用AI服务模型。
     *
     * @param id
     * @throws TransactionException
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用AI服务模型", description = "禁用AI服务模型")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiModelConfig.class,id,"禁用AI服务模型！操作备注："+remark);
        AiModelConfig aiModelConfig = dao.queryForSingleObject(AiModelConfig.class, new AuthIdQueryParam(id));
        if (aiModelConfig == null) {
            return ResponseData.warnMsg("未找到指定id的AI服务模型！");
        }
        if (aiModelConfig.getState()!=StateCommon.ENABLED.getValue()){
            return ResponseData.warnMsg("禁用AI服务模型失败！当前状态不是启用状态！");
        }
        aiModelConfig.setModifyDate(new Date());
        aiModelConfig.setState(StateCommon.DISABLED.getValue());
        dao.update(aiModelConfig);
        return ResponseData.success();
    }

    /**
     * 删除AI服务模型。
     *
     * @param id
     * @throws TransactionException
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除AI服务模型", description = "删除AI服务模型")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiModelConfig.class,id,"删除AI服务模型！操作备注："+remark);
        AiModelConfig aiModelConfig = dao.queryForSingleObject(AiModelConfig.class, new AuthIdQueryParam(id));
        if (aiModelConfig == null) {
            return ResponseData.warnMsg("未找到指定id的AI服务模型！");
        }
        if (aiModelConfig.getState()!=StateCommon.DISABLED.getValue()){
            return ResponseData.warnMsg("删除AI服务模型失败！当前状态不是禁用状态！");
        }            
        aiModelConfig.setModifyDate(new Date());
        aiModelConfig.setState(StateCommon.DELETED.getValue());
        dao.update(aiModelConfig);
        return ResponseData.success();
    }

}