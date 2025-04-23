package uw.ai.center.controller.ops.tool;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiToolInfoQueryParam;
import uw.ai.center.entity.AiToolInfo;
import uw.ai.center.tool.AiToolHelper;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.common.app.constant.CommonState;
import uw.common.app.dto.IdStateQueryParam;
import uw.common.app.dto.SysCritLogQueryParam;
import uw.common.app.dto.SysDataHistoryQueryParam;
import uw.common.app.entity.SysCritLog;
import uw.common.app.entity.SysDataHistory;
import uw.common.app.helper.SysDataHistoryHelper;
import uw.common.dto.ResponseData;
import uw.dao.DaoManager;
import uw.dao.DataList;

import java.util.Date;


/**
 * AI工具信息管理。
 */
@RestController
@RequestMapping("/ops/tool/info")
@Tag(name = "AI工具信息管理", description = "AI工具信息增删改查列管理")
@MscPermDeclare(user = UserType.OPS)
public class AiToolInfoController {

    private final DaoManager dao = DaoManager.getInstance();


    /**
     * 列表AI工具信息。
     *
     * @param queryParam
     * @return
     *
     */
    @GetMapping("/list")
    @Operation(summary = "列表AI工具信息", description = "列表AI工具信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<DataList<AiToolInfo>> list(AiToolInfoQueryParam queryParam){
        AuthServiceHelper.logRef(AiToolInfo.class);
        return dao.list(AiToolInfo.class, queryParam);
    }

    /**
     * 轻量级列表AI工具信息，一般用于select控件。
     *
     * @return
     */
    @GetMapping("/liteList")
    @Operation(summary = "轻量级列表AI工具信息", description = "轻量级列表AI工具信息，一般用于select控件。")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.USER, log = ActionLog.NONE)
    public ResponseData<DataList<AiToolInfo>> liteList(AiToolInfoQueryParam queryParam){
        queryParam.SELECT_SQL( "SELECT id,app_name,tool_class,tool_version,tool_name,create_date,modify_date,state from ai_tool_info " );
        return dao.list(AiToolInfo.class, queryParam);
    }

    /**
     * 加载AI工具信息。
     *
     * @param id
     *
     */
    @GetMapping("/load")
    @Operation(summary = "加载AI工具信息", description = "加载AI工具信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<AiToolInfo> load(@Parameter(description = "主键ID", required = true) @RequestParam long id)  {
        AuthServiceHelper.logRef(AiToolInfo.class,id);
        return dao.load(AiToolInfo.class, id);
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
    public ResponseData<DataList<SysDataHistory>> listDataHistory(SysDataHistoryQueryParam queryParam){
        AuthServiceHelper.logRef(AiToolInfo.class, queryParam.getEntityId());
        queryParam.setEntityClass(AiToolInfo.class);
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
    public ResponseData<DataList<SysCritLog>> listCritLog(SysCritLogQueryParam queryParam)  {
        AuthServiceHelper.logRef(AiToolInfo.class, queryParam.getBizId());
        queryParam.setBizTypeClass(AiToolInfo.class);
        return dao.list(SysCritLog.class, queryParam);
    }
    /**
     * 修改AI工具信息。
     *
     * @param aiToolInfo
     * @return
     *
     */
    @PutMapping("/update")
    @Operation(summary = "修改AI工具信息", description = "修改AI工具信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiToolInfo> update(@RequestBody AiToolInfo aiToolInfo, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiToolInfo.class,aiToolInfo.getId(),remark);
        return  dao.load( AiToolInfo.class, aiToolInfo.getId() ).onSuccess(aiToolInfoDb-> {
            aiToolInfoDb.setAppName(aiToolInfo.getAppName());
            aiToolInfoDb.setToolClass(aiToolInfo.getToolClass());
            aiToolInfoDb.setToolVersion(aiToolInfo.getToolVersion());
            aiToolInfoDb.setToolName(aiToolInfo.getToolName());
            aiToolInfoDb.setToolDesc(aiToolInfo.getToolDesc());
            aiToolInfoDb.setToolInput(aiToolInfo.getToolInput());
            aiToolInfoDb.setToolOutput(aiToolInfo.getToolOutput());
            aiToolInfoDb.setModifyDate(new Date());
            return dao.update( aiToolInfoDb ).onSuccess(updatedEntity -> {
                AiToolHelper.invalidateToolCache();
                SysDataHistoryHelper.saveHistory( aiToolInfoDb,remark );
            } );
        } );
    }


    /**
     * 启用AI工具信息。
     *
     * @param id
     *
     */
    @PutMapping("/enable")
    @Operation(summary = "启用AI工具信息", description = "启用AI工具信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiToolInfo.class,id,remark);
        return dao.update(new AiToolInfo().modifyDate(new Date()).state(CommonState.ENABLED.getValue()), new IdStateQueryParam(id, CommonState.DISABLED.getValue())).onSuccess(updatedEntity -> {
            AiToolHelper.invalidateToolCache();
        });
    }

    /**
     * 禁用AI工具信息。
     *
     * @param id
     *
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用AI工具信息", description = "禁用AI工具信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiToolInfo.class,id,remark);
        return dao.update(new AiToolInfo().modifyDate(new Date()).state(CommonState.DISABLED.getValue()), new IdStateQueryParam(id, CommonState.ENABLED.getValue())).onSuccess(updatedEntity -> {
            AiToolHelper.invalidateToolCache();
        });
    }

    /**
     * 删除AI工具信息。
     *
     * @param id
     *
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除AI工具信息", description = "删除AI工具信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiToolInfo.class,id,remark);
        return dao.update(new AiToolInfo().modifyDate(new Date()).state(CommonState.DELETED.getValue()), new IdStateQueryParam(id, CommonState.DISABLED.getValue()));
    }
}