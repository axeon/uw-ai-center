package uw.ai.center.controller.admin.tool;

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
import uw.common.response.ResponseData;
import uw.common.util.SystemClock;
import uw.dao.DaoManager;
import uw.common.data.PageList;


/**
 * AI工具信息管理。
 * <p>平台管理（ADMIN）角色的 AI 工具配置增删改查接口，路径前缀 {@code /admin/tool/info}。
 */
@RestController
@RequestMapping("/admin/tool/info")
@Tag(name = "AI工具信息管理", description = "AI工具信息增删改查列管理")
@MscPermDeclare(user = UserType.ADMIN)
public class AiToolInfoController {

    private final DaoManager dao = DaoManager.getInstance();


    /**
     * 分页列表AI工具信息。
     *
     * @param queryParam 查询参数
     * @return 工具信息分页列表
     */
    @GetMapping("/list")
    @Operation(summary = "列表AI工具信息", description = "列表AI工具信息")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<AiToolInfo>> list(AiToolInfoQueryParam queryParam){
        AuthServiceHelper.logRef(AiToolInfo.class);
        return dao.list(AiToolInfo.class, queryParam);
    }

    /**
     * 轻量级列表AI工具信息（仅关键列），一般用于前端 select 控件。
     *
     * @param queryParam 查询参数
     * @return 工具信息分页列表（精简字段）
     */
    @GetMapping("/liteList")
    @Operation(summary = "轻量级列表AI工具信息", description = "轻量级列表AI工具信息，一般用于select控件。")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.USER, log = ActionLog.NONE)
    public ResponseData<PageList<AiToolInfo>> liteList(AiToolInfoQueryParam queryParam){
        queryParam.SELECT_SQL( "SELECT id,app_name,tool_class,tool_version,tool_name,tool_desc,create_date,modify_date,state from ai_tool_info " );
        return dao.list(AiToolInfo.class, queryParam);
    }

    /**
     * 按主键加载单条AI工具信息。
     *
     * @param id 主键ID
     * @return 工具信息
     */
    @GetMapping("/load")
    @Operation(summary = "加载AI工具信息", description = "加载AI工具信息")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<AiToolInfo> load(@Parameter(description = "主键ID", required = true) @RequestParam long id)  {
        AuthServiceHelper.logRef(AiToolInfo.class,id);
        return dao.load(AiToolInfo.class, id);
    }

    /**
     * 查询指定AI工具的数据变更历史。
     *
     * @param queryParam 历史查询参数（按 entityId 过滤）
     * @return 数据历史分页列表
     */
    @GetMapping("/listDataHistory")
    @Operation(summary = "查询数据历史", description = "查询数据历史")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<SysDataHistory>> listDataHistory(SysDataHistoryQueryParam queryParam){
        AuthServiceHelper.logRef(AiToolInfo.class, queryParam.getEntityId());
        queryParam.setEntityClass(AiToolInfo.class);
        return dao.list(SysDataHistory.class, queryParam);
    }

    /**
     * 查询指定AI工具的关键操作日志。
     *
     * @param queryParam 日志查询参数（按 bizId 过滤）
     * @return 操作日志分页列表
     */
    @GetMapping("/listCritLog")
    @Operation(summary = "查询操作日志", description = "查询操作日志")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<SysCritLog>> listCritLog(SysCritLogQueryParam queryParam)  {
        AuthServiceHelper.logRef(AiToolInfo.class, queryParam.getBizId());
        queryParam.setBizTypeClass(AiToolInfo.class);
        return dao.list(SysCritLog.class, queryParam);
    }
    /**
     * 修改AI工具信息。
     * <p>更新后失效工具缓存，使下次请求重新加载。
     *
     * @param aiToolInfo 待更新的工具信息
     * @param remark     操作备注（记入日志与历史）
     * @return 更新后的工具信息
     */
    @PutMapping("/update")
    @Operation(summary = "修改AI工具信息", description = "修改AI工具信息")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
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
            aiToolInfoDb.setModifyDate(SystemClock.nowDate());
            return dao.update( aiToolInfoDb ).onSuccess(updatedEntity -> {
                AiToolHelper.invalidateToolCache();
                SysDataHistoryHelper.saveHistory( aiToolInfoDb,remark );
            } );
        } );
    }


    /**
     * 启用AI工具（状态：禁用 → 启用），并失效工具缓存。
     *
     * @param id     主键ID
     * @param remark 操作备注
     * @return 操作结果
     */
    @PutMapping("/enable")
    @Operation(summary = "启用AI工具信息", description = "启用AI工具信息")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiToolInfo.class,id,remark);
        return dao.update(new AiToolInfo().modifyDate(SystemClock.nowDate()).state(CommonState.ENABLED.getValue()), new IdStateQueryParam(id, CommonState.DISABLED.getValue())).onSuccess(updatedEntity -> {
            AiToolHelper.invalidateToolCache();
        });
    }

    /**
     * 禁用AI工具（状态：启用 → 禁用），并失效工具缓存。
     *
     * @param id     主键ID
     * @param remark 操作备注
     * @return 操作结果
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用AI工具信息", description = "禁用AI工具信息")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiToolInfo.class,id,remark);
        return dao.update(new AiToolInfo().modifyDate(SystemClock.nowDate()).state(CommonState.DISABLED.getValue()), new IdStateQueryParam(id, CommonState.ENABLED.getValue())).onSuccess(updatedEntity -> {
            AiToolHelper.invalidateToolCache();
        });
    }

    /**
     * 删除AI工具（软删除：状态 → 已删除）。
     *
     * @param id     主键ID
     * @param remark 操作备注
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除AI工具信息", description = "删除AI工具信息")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiToolInfo.class,id,remark);
        return dao.update(new AiToolInfo().modifyDate(SystemClock.nowDate()).state(CommonState.DELETED.getValue()), new IdStateQueryParam(id, CommonState.DISABLED.getValue()));
    }
}
