package uw.ai.center.controller.saas.session;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiSessionInfoQueryParam;
import uw.ai.center.entity.AiSessionInfo;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.common.app.constant.CommonState;
import uw.common.app.dto.AuthIdQueryParam;
import uw.common.app.dto.AuthIdStateQueryParam;
import uw.common.response.ResponseData;
import uw.common.util.SystemClock;
import uw.dao.DaoManager;
import uw.common.data.PageList;


/**
 * session会话管理。
 * <p>租户（SAAS）角色的会话查询/删除接口，路径前缀 {@code /saas/session/info}。
 */
@RestController
@RequestMapping("/saas/session/info")
@Tag(name = "session会话", description = "session会话")
@MscPermDeclare(user = UserType.SAAS)
public class AiSessionInfoController {

    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 分页列表session会话。
     *
     * @param queryParam 查询参数（自动绑定当前租户 saasId）
     * @return 会话分页列表
     */
    @GetMapping("/list")
    @Operation(summary = "列表session会话", description = "列表session会话")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<AiSessionInfo>> list(AiSessionInfoQueryParam queryParam) {
        AuthServiceHelper.logRef(AiSessionInfo.class);
        return dao.list(AiSessionInfo.class, queryParam);
    }

    /**
     * 轻量级列表session会话（仅关键列），一般用于前端 select 控件。
     *
     * @param queryParam 查询参数（自动绑定当前租户 saasId）
     * @return 会话分页列表（精简字段）
     */
    @GetMapping("/liteList")
    @Operation(summary = "轻量级列表session会话", description = "轻量级列表session会话，一般用于select控件。")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.USER, log = ActionLog.NONE)
    public ResponseData<PageList<AiSessionInfo>> liteList(AiSessionInfoQueryParam queryParam) {
        queryParam.SELECT_SQL("SELECT id,saas_id,user_id,user_type,user_info,config_id,session_type,session_name,msg_num,window_size,create_date,modify_date,last_update,state from ai_session_info ");
        return dao.list(AiSessionInfo.class, queryParam);
    }

    /**
     * 按主键加载单条session会话。
     *
     * @param id 主键ID
     * @return 会话信息
     */
    @GetMapping("/load")
    @Operation(summary = "加载session会话", description = "加载session会话")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<AiSessionInfo> load(@Parameter(description = "主键ID", required = true) @RequestParam long id) {
        AuthServiceHelper.logRef(AiSessionInfo.class, id);
        return dao.queryForObject(AiSessionInfo.class, new AuthIdQueryParam(AuthServiceHelper.getSaasId(), id));
    }

    /**
     * 删除session会话（软删除：状态 → 已删除）。
     *
     * @param id     主键ID
     * @param remark 操作备注
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除session会话", description = "删除session会话")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) {
        AuthServiceHelper.logInfo(AiSessionInfo.class, id, remark);
        return dao.update(new AiSessionInfo().modifyDate(SystemClock.nowDate()).state(CommonState.DELETED.getValue()), new AuthIdStateQueryParam(AuthServiceHelper.getSaasId(), id, CommonState.DISABLED.getValue()));
    }

}
