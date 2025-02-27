package uw.ai.center.controller.ops.tool;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiToolConfigQueryParam;
import uw.ai.center.entity.AiToolConfig;
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
 * AI工具配置管理。
 */
@RestController
@RequestMapping("/ops/tool/config")
@Tag(name = "AI工具配置管理", description = "AI工具配置增删改查列管理")
@MscPermDeclare(user = UserType.OPS)
public class AiToolConfigController {

    private final DaoFactory dao = DaoFactory.getInstance();

    /**
     * 列表AI工具配置。
     *
     * @param queryParam
     * @return
     * @throws TransactionException
     */
    @GetMapping("/list")
    @Operation(summary = "列表AI工具配置", description = "列表AI工具配置")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public DataList<AiToolConfig> list(AiToolConfigQueryParam queryParam) throws TransactionException {
        AuthServiceHelper.logRef(AiToolConfig.class);
        return dao.list(AiToolConfig.class, queryParam);
    }

    /**
     * 轻量级列表AI工具配置，一般用于select控件。
     *
     * @return
     */
    @GetMapping("/liteList")
    @Operation(summary = "轻量级列表AI工具配置", description = "轻量级列表AI工具配置，一般用于select控件。")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.USER, log = ActionLog.NONE)
    public DataList<AiToolConfig> liteList(AiToolConfigQueryParam queryParam) throws TransactionException {
        queryParam.SELECT_SQL( "SELECT id,app_name,tool_code,tool_version,tool_name,create_date,modify_date,state from ai_tool_config " );
        return dao.list(AiToolConfig.class, queryParam);
    }

    /**
     * 加载AI工具配置。
     *
     * @param id
     * @throws TransactionException
     */
    @GetMapping("/load")
    @Operation(summary = "加载AI工具配置", description = "加载AI工具配置")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public AiToolConfig load(@Parameter(description = "主键ID", required = true) @RequestParam long id) throws TransactionException {
        AuthServiceHelper.logRef(AiToolConfig.class,id);
        return dao.load(AiToolConfig.class, id);
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
        AuthServiceHelper.logRef(AiToolConfig.class, queryParam.getEntityId());
        queryParam.setEntityClass(AiToolConfig.class);
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
        AuthServiceHelper.logRef(AiToolConfig.class, queryParam.getRefId());
        queryParam.setRefTypeClass(AiToolConfig.class);
        return dao.list(SysCritLog.class, queryParam);
    }

    /**
     * 新增AI工具配置。
     *
     * @param aiToolConfig
     * @return
     * @throws TransactionException
     */
    @PostMapping("/save")
    @Operation(summary = "新增AI工具配置", description = "新增AI工具配置")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiToolConfig> save(@RequestBody AiToolConfig aiToolConfig) throws TransactionException {
        long id = dao.getSequenceId(AiToolConfig.class);
        AuthServiceHelper.logRef(AiToolConfig.class,id);
        aiToolConfig.setId(id);
        aiToolConfig.setCreateDate(new Date());
        aiToolConfig.setModifyDate(null);
        aiToolConfig.setState(1);
        dao.save(aiToolConfig);
        //保存历史记录
        SysDataHistoryHelper.saveHistory(aiToolConfig.getId(),aiToolConfig,"AI工具配置","新增AI工具配置");
        return ResponseData.success(aiToolConfig);
    }

    /**
     * 修改AI工具配置。
     *
     * @param aiToolConfig
     * @return
     * @throws TransactionException
     */
    @PutMapping("/update")
    @Operation(summary = "修改AI工具配置", description = "修改AI工具配置")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiToolConfig> update(@RequestBody AiToolConfig aiToolConfig, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiToolConfig.class,aiToolConfig.getId(),"修改AI工具配置！操作备注："+remark);
        AiToolConfig aiToolConfigDb = dao.load(AiToolConfig.class, aiToolConfig.getId());
        if (aiToolConfigDb == null) {
            return ResponseData.warnMsg("未找到指定ID的AI工具配置！");
        }
        aiToolConfigDb.setAppName(aiToolConfig.getAppName());
        aiToolConfigDb.setToolCode(aiToolConfig.getToolCode());
        aiToolConfigDb.setToolVersion(aiToolConfig.getToolVersion());
        aiToolConfigDb.setToolName(aiToolConfig.getToolName());
        aiToolConfigDb.setToolDesc(aiToolConfig.getToolDesc());
        aiToolConfigDb.setToolParam(aiToolConfig.getToolParam());
        aiToolConfigDb.setToolReturn(aiToolConfig.getToolReturn());
        aiToolConfigDb.setModifyDate(new Date());
        dao.update(aiToolConfigDb);
        SysDataHistoryHelper.saveHistory(aiToolConfigDb.getId(),aiToolConfigDb,"AI工具配置","修改AI工具配置！操作备注："+remark);
        return ResponseData.success(aiToolConfigDb);
    }
    
    /**
     * 启用AI工具配置。
     *
     * @param id
     * @throws TransactionException
     */
    @PutMapping("/enable")
    @Operation(summary = "启用AI工具配置", description = "启用AI工具配置")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiToolConfig.class,id,"启用AI工具配置！操作备注："+remark);
        AiToolConfig aiToolConfig = dao.load(AiToolConfig.class, id);
        if (aiToolConfig == null) {
            return ResponseData.warnMsg("未找到指定id的AI工具配置！");
        }
        if (aiToolConfig.getState()!=StateCommon.DISABLED.getValue()){
            return ResponseData.warnMsg("启用AI工具配置失败！当前状态不是禁用状态！");                
        }
        aiToolConfig.setModifyDate(new Date());
        aiToolConfig.setState(StateCommon.ENABLED.getValue());
        dao.update(aiToolConfig);
        return ResponseData.success();
    }

    /**
     * 禁用AI工具配置。
     *
     * @param id
     * @throws TransactionException
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用AI工具配置", description = "禁用AI工具配置")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiToolConfig.class,id,"禁用AI工具配置！操作备注："+remark);
        AiToolConfig aiToolConfig = dao.load(AiToolConfig.class, id);
        if (aiToolConfig == null) {
            return ResponseData.warnMsg("未找到指定id的AI工具配置！");
        }			
        if (aiToolConfig.getState()!=StateCommon.ENABLED.getValue()){
            return ResponseData.warnMsg("禁用AI工具配置失败！当前状态不是启用状态！");                
        }            
        aiToolConfig.setModifyDate(new Date());
        aiToolConfig.setState(StateCommon.DISABLED.getValue());
        dao.update(aiToolConfig);
        return ResponseData.success();
    }

    /**
     * 删除AI工具配置。
     *
     * @param id
     * @throws TransactionException
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除AI工具配置", description = "删除AI工具配置")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiToolConfig.class,id,"删除AI工具配置！操作备注："+remark);
        AiToolConfig aiToolConfig = dao.load(AiToolConfig.class, id);
        if (aiToolConfig == null) {
            return ResponseData.warnMsg("未找到指定id的AI工具配置！");
        }
        if (aiToolConfig.getState()!=StateCommon.DISABLED.getValue()){
            return ResponseData.warnMsg("删除AI工具配置失败！当前状态不是禁用状态！");
        }            
        aiToolConfig.setModifyDate(new Date());
        aiToolConfig.setState(StateCommon.DELETED.getValue());
        dao.update(aiToolConfig);
        return ResponseData.success();
    }

}