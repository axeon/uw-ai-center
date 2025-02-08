package uw.ai.center.controller.ops.session;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiSessionInfoQueryParam;
import uw.ai.center.entity.AiSessionInfo;
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
 * session信息管理。
 */
@RestController
@RequestMapping("/ops/session/info")
@Tag(name = "session信息管理", description = "session信息增删改查列管理")
@MscPermDeclare(user = UserType.SAAS)
public class AiSessionInfoController {

    private final DaoFactory dao = DaoFactory.getInstance();

    /**
     * 列表session信息。
     *
     * @param queryParam
     * @return
     * @throws TransactionException
     */
    @GetMapping("/list")
    @Operation(summary = "列表session信息", description = "列表session信息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public DataList<AiSessionInfo> list(AiSessionInfoQueryParam queryParam) throws TransactionException {
        AuthServiceHelper.logRef(AiSessionInfo.class);
        return dao.list(AiSessionInfo.class, queryParam);
    }

    /**
     * 轻量级列表session信息，一般用于select控件。
     *
     * @return
     */
    @GetMapping("/liteList")
    @Operation(summary = "轻量级列表session信息", description = "轻量级列表session信息，一般用于select控件。")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.USER, log = ActionLog.NONE)
    public DataList<AiSessionInfo> liteList(AiSessionInfoQueryParam queryParam) throws TransactionException {
        queryParam.SELECT_SQL( "SELECT id,saas_id,mch_id,user_id,user_type,group_id,user_name,nick_name,real_name,session_name,create_date,modify_date,state from ai_session_info " );
        return dao.list(AiSessionInfo.class, queryParam);
    }

    /**
     * 加载session信息。
     *
     * @param id
     * @throws TransactionException
     */
    @GetMapping("/load")
    @Operation(summary = "加载session信息", description = "加载session信息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public AiSessionInfo load(@Parameter(description = "主键ID", required = true) @RequestParam long id) throws TransactionException {
        AuthServiceHelper.logRef(AiSessionInfo.class,id);
        return dao.queryForSingleObject(AiSessionInfo.class, new AuthIdQueryParam(id));
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
        AuthServiceHelper.logRef(AiSessionInfo.class, queryParam.getEntityId());
        queryParam.setEntityClass(AiSessionInfo.class);
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
        AuthServiceHelper.logRef(AiSessionInfo.class, queryParam.getRefId());
        queryParam.setRefTypeClass(AiSessionInfo.class);
        return dao.list(SysCritLog.class, queryParam);
    }

    /**
     * 新增session信息。
     *
     * @param aiSessionInfo
     * @return
     * @throws TransactionException
     */
    @PostMapping("/save")
    @Operation(summary = "新增session信息", description = "新增session信息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiSessionInfo> save(@RequestBody AiSessionInfo aiSessionInfo) throws TransactionException {
        long id = dao.getSequenceId(AiSessionInfo.class);
        AuthServiceHelper.logRef(AiSessionInfo.class,id);
        aiSessionInfo.setId(id);
        aiSessionInfo.setSaasId(AuthServiceHelper.getSaasId());
        aiSessionInfo.setCreateDate(new Date());
        aiSessionInfo.setModifyDate(null);
        aiSessionInfo.setState(1);
        dao.save(aiSessionInfo);
        //保存历史记录
        SysDataHistoryHelper.saveHistory(aiSessionInfo.getId(),aiSessionInfo,"session信息","新增session信息");
        return ResponseData.success(aiSessionInfo);
    }

    /**
     * 修改session信息。
     *
     * @param aiSessionInfo
     * @return
     * @throws TransactionException
     */
    @PutMapping("/update")
    @Operation(summary = "修改session信息", description = "修改session信息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiSessionInfo> update(@RequestBody AiSessionInfo aiSessionInfo, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiSessionInfo.class,aiSessionInfo.getId(),"修改session信息！操作备注："+remark);
        AiSessionInfo aiSessionInfoDb = dao.queryForSingleObject(AiSessionInfo.class, new AuthIdQueryParam(aiSessionInfo.getId()));
        if (aiSessionInfoDb == null) {
            return ResponseData.warnMsg("未找到指定ID的session信息！");
        }
        aiSessionInfoDb.setMchId(aiSessionInfo.getMchId());
        aiSessionInfoDb.setUserId(aiSessionInfo.getUserId());
        aiSessionInfoDb.setUserType(aiSessionInfo.getUserType());
        aiSessionInfoDb.setGroupId(aiSessionInfo.getGroupId());
        aiSessionInfoDb.setUserName(aiSessionInfo.getUserName());
        aiSessionInfoDb.setNickName(aiSessionInfo.getNickName());
        aiSessionInfoDb.setRealName(aiSessionInfo.getRealName());
        aiSessionInfoDb.setSessionName(aiSessionInfo.getSessionName());
        aiSessionInfoDb.setModifyDate(new Date());
        dao.update(aiSessionInfoDb);
        SysDataHistoryHelper.saveHistory(aiSessionInfoDb.getId(),aiSessionInfoDb,"session信息","修改session信息！操作备注："+remark);
        return ResponseData.success(aiSessionInfoDb);
    }
    
    /**
     * 启用session信息。
     *
     * @param id
     * @throws TransactionException
     */
    @PutMapping("/enable")
    @Operation(summary = "启用session信息", description = "启用session信息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiSessionInfo.class,id,"启用session信息！操作备注："+remark);
        AiSessionInfo aiSessionInfo = dao.queryForSingleObject(AiSessionInfo.class, new AuthIdQueryParam(id));
        if (aiSessionInfo == null) {
            return ResponseData.warnMsg("未找到指定id的session信息！");
        }
        if (aiSessionInfo.getState()!=StateCommon.DISABLED.getValue()){
            return ResponseData.warnMsg("启用session信息失败！当前状态不是禁用状态！");                
        }
        aiSessionInfo.setModifyDate(new Date());
        aiSessionInfo.setState(StateCommon.ENABLED.getValue());
        dao.update(aiSessionInfo);
        return ResponseData.success();
    }

    /**
     * 禁用session信息。
     *
     * @param id
     * @throws TransactionException
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用session信息", description = "禁用session信息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiSessionInfo.class,id,"禁用session信息！操作备注："+remark);
        AiSessionInfo aiSessionInfo = dao.queryForSingleObject(AiSessionInfo.class, new AuthIdQueryParam(id));
        if (aiSessionInfo == null) {
            return ResponseData.warnMsg("未找到指定id的session信息！");
        }			
        if (aiSessionInfo.getState()!=StateCommon.ENABLED.getValue()){
            return ResponseData.warnMsg("禁用session信息失败！当前状态不是启用状态！");                
        }            
        aiSessionInfo.setModifyDate(new Date());
        aiSessionInfo.setState(StateCommon.DISABLED.getValue());
        dao.update(aiSessionInfo);
        return ResponseData.success();
    }

    /**
     * 删除session信息。
     *
     * @param id
     * @throws TransactionException
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除session信息", description = "删除session信息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiSessionInfo.class,id,"删除session信息！操作备注："+remark);
        AiSessionInfo aiSessionInfo = dao.queryForSingleObject(AiSessionInfo.class, new AuthIdQueryParam(id));
        if (aiSessionInfo == null) {
            return ResponseData.warnMsg("未找到指定id的session信息！");
        }
        if (aiSessionInfo.getState()!=StateCommon.DISABLED.getValue()){
            return ResponseData.warnMsg("删除session信息失败！当前状态不是禁用状态！");
        }            
        aiSessionInfo.setModifyDate(new Date());
        aiSessionInfo.setState(StateCommon.DELETED.getValue());
        dao.update(aiSessionInfo);
        return ResponseData.success();
    }

}