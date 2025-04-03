package uw.ai.center.controller.ops.tool;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiToolInfoQueryParam;
import uw.ai.center.entity.AiToolInfo;
import uw.ai.center.tool.AiToolHelper;
import uw.app.common.dto.SysCritLogQueryParam;
import uw.app.common.dto.SysDataHistoryQueryParam;
import uw.app.common.entity.SysCritLog;
import uw.app.common.entity.SysDataHistory;
import uw.app.common.helper.SysDataHistoryHelper;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.app.common.constant.CommonState;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.common.dto.ResponseData;
import uw.dao.DaoFactory;
import uw.dao.DataList;
import uw.dao.TransactionException;

import java.util.Date;


/**
 * AI工具信息管理。
 */
@RestController
@RequestMapping("/ops/tool/info")
@Tag(name = "AI工具信息管理", description = "AI工具信息增删改查列管理")
@MscPermDeclare(user = UserType.OPS)
public class AiToolInfoController {

    private final DaoFactory dao = DaoFactory.getInstance();

    /**
     * 列表AI工具信息。
     *
     * @param queryParam
     * @return
     * @throws TransactionException
     */
    @GetMapping("/list")
    @Operation(summary = "列表AI工具信息", description = "列表AI工具信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public DataList<AiToolInfo> list(AiToolInfoQueryParam queryParam) throws TransactionException {
        AuthServiceHelper.logRef( AiToolInfo.class);
        return dao.list( AiToolInfo.class, queryParam);
    }

    /**
     * 轻量级列表AI工具信息，一般用于select控件。
     *
     * @return
     */
    @GetMapping("/liteList")
    @Operation(summary = "轻量级列表AI工具信息", description = "轻量级列表AI工具信息，一般用于select控件。")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.USER, log = ActionLog.NONE)
    public DataList<AiToolInfo> liteList(AiToolInfoQueryParam queryParam) throws TransactionException {
        queryParam.SELECT_SQL( "SELECT id,app_name,tool_code,tool_version,tool_name,create_date,modify_date,state from ai_tool_info " );
        return dao.list( AiToolInfo.class, queryParam);
    }

    /**
     * 加载AI工具信息。
     *
     * @param id
     * @throws TransactionException
     */
    @GetMapping("/load")
    @Operation(summary = "加载AI工具信息", description = "加载AI工具信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public AiToolInfo load(@Parameter(description = "主键ID", required = true) @RequestParam long id) throws TransactionException {
        AuthServiceHelper.logRef( AiToolInfo.class,id);
        return dao.load( AiToolInfo.class, id);
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
        AuthServiceHelper.logRef( AiToolInfo.class, queryParam.getEntityId());
        queryParam.setEntityClass( AiToolInfo.class);
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
        AuthServiceHelper.logRef( AiToolInfo.class, queryParam.getRefId());
        queryParam.setRefTypeClass( AiToolInfo.class);
        return dao.list(SysCritLog.class, queryParam);
    }

    /**
     * 修改AI工具信息。
     *
     * @param aiToolConfig
     * @return
     * @throws TransactionException
     */
    @PutMapping("/update")
    @Operation(summary = "修改AI工具信息", description = "修改AI工具信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiToolInfo> update(@RequestBody AiToolInfo aiToolConfig, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo( AiToolInfo.class,aiToolConfig.getId(),"修改AI工具信息！操作备注："+remark);
        AiToolInfo aiToolConfigDb = dao.load( AiToolInfo.class, aiToolConfig.getId());
        if (aiToolConfigDb == null) {
            return ResponseData.warnMsg("未找到指定ID的AI工具信息！");
        }
        aiToolConfigDb.setAppName(aiToolConfig.getAppName());
        aiToolConfigDb.setToolClass(aiToolConfig.getToolClass());
        aiToolConfigDb.setToolVersion(aiToolConfig.getToolVersion());
        aiToolConfigDb.setToolName(aiToolConfig.getToolName());
        aiToolConfigDb.setToolDesc(aiToolConfig.getToolDesc());
        aiToolConfigDb.setToolInput(aiToolConfig.getToolInput());
        aiToolConfigDb.setToolOutput(aiToolConfig.getToolOutput());
        aiToolConfigDb.setModifyDate(new Date());
        dao.update(aiToolConfigDb);
        AiToolHelper.invalidateToolCache();
        SysDataHistoryHelper.saveHistory(aiToolConfigDb.getId(),aiToolConfigDb,"AI工具信息","修改AI工具信息！操作备注："+remark);
        return ResponseData.success(aiToolConfigDb);
    }
    
    /**
     * 启用AI工具信息。
     *
     * @param id
     * @throws TransactionException
     */
    @PutMapping("/enable")
    @Operation(summary = "启用AI工具信息", description = "启用AI工具信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo( AiToolInfo.class,id,"启用AI工具信息！操作备注："+remark);
        AiToolInfo aiToolConfig = dao.load( AiToolInfo.class, id);
        if (aiToolConfig == null) {
            return ResponseData.warnMsg("未找到指定id的AI工具信息！");
        }
        if (aiToolConfig.getState()!= CommonState.DISABLED.getValue()){
            return ResponseData.warnMsg("启用AI工具信息失败！当前状态不是禁用状态！");                
        }
        aiToolConfig.setModifyDate(new Date());
        aiToolConfig.setState( CommonState.ENABLED.getValue());
        dao.update(aiToolConfig);
        AiToolHelper.invalidateToolCache();
        return ResponseData.success();
    }

    /**
     * 禁用AI工具信息。
     *
     * @param id
     * @throws TransactionException
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用AI工具信息", description = "禁用AI工具信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo( AiToolInfo.class,id,"禁用AI工具信息！操作备注："+remark);
        AiToolInfo aiToolConfig = dao.load( AiToolInfo.class, id);
        if (aiToolConfig == null) {
            return ResponseData.warnMsg("未找到指定id的AI工具信息！");
        }			
        if (aiToolConfig.getState()!= CommonState.ENABLED.getValue()){
            return ResponseData.warnMsg("禁用AI工具信息失败！当前状态不是启用状态！");                
        }            
        aiToolConfig.setModifyDate(new Date());
        aiToolConfig.setState( CommonState.DISABLED.getValue());
        dao.update(aiToolConfig);
        AiToolHelper.invalidateToolCache();
        return ResponseData.success();
    }

    /**
     * 删除AI工具信息。
     *
     * @param id
     * @throws TransactionException
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除AI工具信息", description = "删除AI工具信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo( AiToolInfo.class,id,"删除AI工具信息！操作备注："+remark);
        AiToolInfo aiToolConfig = dao.load( AiToolInfo.class, id);
        if (aiToolConfig == null) {
            return ResponseData.warnMsg("未找到指定id的AI工具信息！");
        }
        if (aiToolConfig.getState()!= CommonState.DISABLED.getValue()){
            return ResponseData.warnMsg("删除AI工具信息失败！当前状态不是禁用状态！");
        }            
        aiToolConfig.setModifyDate(new Date());
        aiToolConfig.setState( CommonState.DELETED.getValue());
        dao.update(aiToolConfig);
        return ResponseData.success();
    }

}