package uw.ai.center.controller.ops.vendor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiVendorModelQueryParam;
import uw.ai.center.entity.AiVendorModel;
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
@RequestMapping("/ops/vendor/model")
@Tag(name = "AI服务模型管理", description = "AI服务模型增删改查列管理")
@MscPermDeclare(user = UserType.SAAS)
public class AiVendorModelController {

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
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public DataList<AiVendorModel> list(AiVendorModelQueryParam queryParam) throws TransactionException {
        AuthServiceHelper.logRef(AiVendorModel.class);
        return dao.list(AiVendorModel.class, queryParam);
    }

    /**
     * 轻量级列表AI服务模型，一般用于select控件。
     *
     * @return
     */
    @GetMapping("/liteList")
    @Operation(summary = "轻量级列表AI服务模型", description = "轻量级列表AI服务模型，一般用于select控件。")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.USER, log = ActionLog.NONE)
    public DataList<AiVendorModel> liteList(AiVendorModelQueryParam queryParam) throws TransactionException {
        queryParam.SELECT_SQL( "SELECT id,vendor_id,model_code,model_name,create_date,modify_date,state from ai_vendor_model " );
        return dao.list(AiVendorModel.class, queryParam);
    }

    /**
     * 加载AI服务模型。
     *
     * @param id
     * @throws TransactionException
     */
    @GetMapping("/load")
    @Operation(summary = "加载AI服务模型", description = "加载AI服务模型")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public AiVendorModel load(@Parameter(description = "主键ID", required = true) @RequestParam long id) throws TransactionException {
        AuthServiceHelper.logRef(AiVendorModel.class,id);
        return dao.load(AiVendorModel.class, id);
    }

    /**
     * 查询数据历史。
     *
     * @param
     * @return
     */
    @GetMapping("/listDataHistory")
    @Operation(summary = "查询数据历史", description = "查询数据历史")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public DataList<SysDataHistory> listDataHistory(SysDataHistoryQueryParam queryParam) throws TransactionException {
        AuthServiceHelper.logRef(AiVendorModel.class, queryParam.getEntityId());
        queryParam.setEntityClass(AiVendorModel.class);
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
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public DataList<SysCritLog> listCritLog(SysCritLogQueryParam queryParam) throws TransactionException {
        AuthServiceHelper.logRef(AiVendorModel.class, queryParam.getRefId());
        queryParam.setRefTypeClass(AiVendorModel.class);
        return dao.list(SysCritLog.class, queryParam);
    }

    /**
     * 新增AI服务模型。
     *
     * @param aiVendorModel
     * @return
     * @throws TransactionException
     */
    @PostMapping("/save")
    @Operation(summary = "新增AI服务模型", description = "新增AI服务模型")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiVendorModel> save(@RequestBody AiVendorModel aiVendorModel) throws TransactionException {
        long id = dao.getSequenceId(AiVendorModel.class);
        AuthServiceHelper.logRef(AiVendorModel.class,id);
        aiVendorModel.setId(id);
        aiVendorModel.setCreateDate(new Date());
        aiVendorModel.setModifyDate(null);
        aiVendorModel.setState(1);
        dao.save(aiVendorModel);
        //保存历史记录
        SysDataHistoryHelper.saveHistory(aiVendorModel.getId(),aiVendorModel,"AI服务模型","新增AI服务模型");
        return ResponseData.success(aiVendorModel);
    }

    /**
     * 修改AI服务模型。
     *
     * @param aiVendorModel
     * @return
     * @throws TransactionException
     */
    @PutMapping("/update")
    @Operation(summary = "修改AI服务模型", description = "修改AI服务模型")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiVendorModel> update(@RequestBody AiVendorModel aiVendorModel, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiVendorModel.class,aiVendorModel.getId(),"修改AI服务模型！操作备注："+remark);
        AiVendorModel aiVendorModelDb = dao.load(AiVendorModel.class, aiVendorModel.getId());
        if (aiVendorModelDb == null) {
            return ResponseData.warnMsg("未找到指定ID的AI服务模型！");
        }
        aiVendorModelDb.setVendorId(aiVendorModel.getVendorId());
        aiVendorModelDb.setModelCode(aiVendorModel.getModelCode());
        aiVendorModelDb.setModelName(aiVendorModel.getModelName());
        aiVendorModelDb.setModelDesc(aiVendorModel.getModelDesc());
        aiVendorModelDb.setModifyDate(new Date());
        dao.update(aiVendorModelDb);
        SysDataHistoryHelper.saveHistory(aiVendorModelDb.getId(),aiVendorModelDb,"AI服务模型","修改AI服务模型！操作备注："+remark);
        return ResponseData.success(aiVendorModelDb);
    }
    
    /**
     * 启用AI服务模型。
     *
     * @param id
     * @throws TransactionException
     */
    @PutMapping("/enable")
    @Operation(summary = "启用AI服务模型", description = "启用AI服务模型")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiVendorModel.class,id,"启用AI服务模型！操作备注："+remark);
        AiVendorModel aiVendorModel = dao.load(AiVendorModel.class, id);
        if (aiVendorModel == null) {
            return ResponseData.warnMsg("未找到指定id的AI服务模型！");
        }
        if (aiVendorModel.getState()!=StateCommon.DISABLED.getValue()){
            return ResponseData.warnMsg("启用AI服务模型失败！当前状态不是禁用状态！");                
        }
        aiVendorModel.setModifyDate(new Date());
        aiVendorModel.setState(StateCommon.ENABLED.getValue());
        dao.update(aiVendorModel);
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
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiVendorModel.class,id,"禁用AI服务模型！操作备注："+remark);
        AiVendorModel aiVendorModel = dao.load(AiVendorModel.class, id);
        if (aiVendorModel == null) {
            return ResponseData.warnMsg("未找到指定id的AI服务模型！");
        }			
        if (aiVendorModel.getState()!=StateCommon.ENABLED.getValue()){
            return ResponseData.warnMsg("禁用AI服务模型失败！当前状态不是启用状态！");                
        }            
        aiVendorModel.setModifyDate(new Date());
        aiVendorModel.setState(StateCommon.DISABLED.getValue());
        dao.update(aiVendorModel);
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
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiVendorModel.class,id,"删除AI服务模型！操作备注："+remark);
        AiVendorModel aiVendorModel = dao.load(AiVendorModel.class, id);
        if (aiVendorModel == null) {
            return ResponseData.warnMsg("未找到指定id的AI服务模型！");
        }
        if (aiVendorModel.getState()!=StateCommon.DISABLED.getValue()){
            return ResponseData.warnMsg("删除AI服务模型失败！当前状态不是禁用状态！");
        }            
        aiVendorModel.setModifyDate(new Date());
        aiVendorModel.setState(StateCommon.DELETED.getValue());
        dao.update(aiVendorModel);
        return ResponseData.success();
    }

}