package uw.ai.center.controller.ops.vendor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiVendorInfoQueryParam;
import uw.ai.center.entity.AiVendorInfo;
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
 * AI服务商信息管理。
 */
@RestController
@RequestMapping("/ops/vendor/info")
@Tag(name = "AI服务商信息管理", description = "AI服务商信息增删改查列管理")
@MscPermDeclare(user = UserType.SAAS)
public class AiVendorInfoController {

    private final DaoFactory dao = DaoFactory.getInstance();

    /**
     * 列表AI服务商信息。
     *
     * @param queryParam
     * @return
     * @throws TransactionException
     */
    @GetMapping("/list")
    @Operation(summary = "列表AI服务商信息", description = "列表AI服务商信息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public DataList<AiVendorInfo> list(AiVendorInfoQueryParam queryParam) throws TransactionException {
        AuthServiceHelper.logRef(AiVendorInfo.class);
        return dao.list(AiVendorInfo.class, queryParam);
    }

    /**
     * 轻量级列表AI服务商信息，一般用于select控件。
     *
     * @return
     */
    @GetMapping("/liteList")
    @Operation(summary = "轻量级列表AI服务商信息", description = "轻量级列表AI服务商信息，一般用于select控件。")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.USER, log = ActionLog.NONE)
    public DataList<AiVendorInfo> liteList(AiVendorInfoQueryParam queryParam) throws TransactionException {
        queryParam.SELECT_SQL( "SELECT id,vendor_code,vendor_name,create_date,modify_date,state from ai_vendor_info " );
        return dao.list(AiVendorInfo.class, queryParam);
    }

    /**
     * 加载AI服务商信息。
     *
     * @param id
     * @throws TransactionException
     */
    @GetMapping("/load")
    @Operation(summary = "加载AI服务商信息", description = "加载AI服务商信息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public AiVendorInfo load(@Parameter(description = "主键ID", required = true) @RequestParam long id) throws TransactionException {
        AuthServiceHelper.logRef(AiVendorInfo.class,id);
        return dao.load(AiVendorInfo.class, id);
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
        AuthServiceHelper.logRef(AiVendorInfo.class, queryParam.getEntityId());
        queryParam.setEntityClass(AiVendorInfo.class);
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
        AuthServiceHelper.logRef(AiVendorInfo.class, queryParam.getRefId());
        queryParam.setRefTypeClass(AiVendorInfo.class);
        return dao.list(SysCritLog.class, queryParam);
    }

    /**
     * 新增AI服务商信息。
     *
     * @param aiVendorInfo
     * @return
     * @throws TransactionException
     */
    @PostMapping("/save")
    @Operation(summary = "新增AI服务商信息", description = "新增AI服务商信息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiVendorInfo> save(@RequestBody AiVendorInfo aiVendorInfo) throws TransactionException {
        long id = dao.getSequenceId(AiVendorInfo.class);
        AuthServiceHelper.logRef(AiVendorInfo.class,id);
        aiVendorInfo.setId(id);
        aiVendorInfo.setCreateDate(new Date());
        aiVendorInfo.setModifyDate(null);
        aiVendorInfo.setState(1);
        dao.save(aiVendorInfo);
        //保存历史记录
        SysDataHistoryHelper.saveHistory(aiVendorInfo.getId(),aiVendorInfo,"AI服务商信息","新增AI服务商信息");
        return ResponseData.success(aiVendorInfo);
    }

    /**
     * 修改AI服务商信息。
     *
     * @param aiVendorInfo
     * @return
     * @throws TransactionException
     */
    @PutMapping("/update")
    @Operation(summary = "修改AI服务商信息", description = "修改AI服务商信息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiVendorInfo> update(@RequestBody AiVendorInfo aiVendorInfo, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiVendorInfo.class,aiVendorInfo.getId(),"修改AI服务商信息！操作备注："+remark);
        AiVendorInfo aiVendorInfoDb = dao.load(AiVendorInfo.class, aiVendorInfo.getId());
        if (aiVendorInfoDb == null) {
            return ResponseData.warnMsg("未找到指定ID的AI服务商信息！");
        }
        aiVendorInfoDb.setVendorCode(aiVendorInfo.getVendorCode());
        aiVendorInfoDb.setVendorName(aiVendorInfo.getVendorName());
        aiVendorInfoDb.setVendorDesc(aiVendorInfo.getVendorDesc());
        aiVendorInfoDb.setModifyDate(new Date());
        dao.update(aiVendorInfoDb);
        SysDataHistoryHelper.saveHistory(aiVendorInfoDb.getId(),aiVendorInfoDb,"AI服务商信息","修改AI服务商信息！操作备注："+remark);
        return ResponseData.success(aiVendorInfoDb);
    }
    
    /**
     * 启用AI服务商信息。
     *
     * @param id
     * @throws TransactionException
     */
    @PutMapping("/enable")
    @Operation(summary = "启用AI服务商信息", description = "启用AI服务商信息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiVendorInfo.class,id,"启用AI服务商信息！操作备注："+remark);
        AiVendorInfo aiVendorInfo = dao.load(AiVendorInfo.class, id);
        if (aiVendorInfo == null) {
            return ResponseData.warnMsg("未找到指定id的AI服务商信息！");
        }
        if (aiVendorInfo.getState()!=StateCommon.DISABLED.getValue()){
            return ResponseData.warnMsg("启用AI服务商信息失败！当前状态不是禁用状态！");                
        }
        aiVendorInfo.setModifyDate(new Date());
        aiVendorInfo.setState(StateCommon.ENABLED.getValue());
        dao.update(aiVendorInfo);
        return ResponseData.success();
    }

    /**
     * 禁用AI服务商信息。
     *
     * @param id
     * @throws TransactionException
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用AI服务商信息", description = "禁用AI服务商信息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiVendorInfo.class,id,"禁用AI服务商信息！操作备注："+remark);
        AiVendorInfo aiVendorInfo = dao.load(AiVendorInfo.class, id);
        if (aiVendorInfo == null) {
            return ResponseData.warnMsg("未找到指定id的AI服务商信息！");
        }			
        if (aiVendorInfo.getState()!=StateCommon.ENABLED.getValue()){
            return ResponseData.warnMsg("禁用AI服务商信息失败！当前状态不是启用状态！");                
        }            
        aiVendorInfo.setModifyDate(new Date());
        aiVendorInfo.setState(StateCommon.DISABLED.getValue());
        dao.update(aiVendorInfo);
        return ResponseData.success();
    }

    /**
     * 删除AI服务商信息。
     *
     * @param id
     * @throws TransactionException
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除AI服务商信息", description = "删除AI服务商信息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiVendorInfo.class,id,"删除AI服务商信息！操作备注："+remark);
        AiVendorInfo aiVendorInfo = dao.load(AiVendorInfo.class, id);
        if (aiVendorInfo == null) {
            return ResponseData.warnMsg("未找到指定id的AI服务商信息！");
        }
        if (aiVendorInfo.getState()!=StateCommon.DISABLED.getValue()){
            return ResponseData.warnMsg("删除AI服务商信息失败！当前状态不是禁用状态！");
        }            
        aiVendorInfo.setModifyDate(new Date());
        aiVendorInfo.setState(StateCommon.DELETED.getValue());
        dao.update(aiVendorInfo);
        return ResponseData.success();
    }

}