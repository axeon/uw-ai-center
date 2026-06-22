package uw.ai.center.controller.saas.session;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiSessionMsgQueryParam;
import uw.ai.center.entity.AiSessionMsg;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.common.app.constant.CommonState;
import uw.common.app.dto.AuthIdQueryParam;
import uw.common.app.dto.AuthIdStateQueryParam;
import uw.common.response.ResponseData;
import uw.dao.DaoManager;
import uw.common.data.PageList;


/**
 * session消息管理。
 * <p>租户（SAAS）角色的会话消息查询/删除接口，路径前缀 {@code /saas/session/info/msg}。
 */
@RestController
@RequestMapping("/saas/session/info/msg")
@Tag(name = "session消息管理", description = "session消息增删改查列管理")
@MscPermDeclare(user = UserType.SAAS)
public class AiSessionMsgController {

    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 分页列表session消息。
     *
     * @param queryParam 查询参数（自动绑定当前租户 saasId）
     * @return 会话消息分页列表
     */
    @GetMapping("/list")
    @Operation(summary = "列表session消息", description = "列表session消息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<AiSessionMsg>> list(AiSessionMsgQueryParam queryParam) {
        AuthServiceHelper.logRef(AiSessionMsg.class);
        return dao.list(AiSessionMsg.class, queryParam);
    }

    /**
     * 轻量级列表session消息（仅关键列），一般用于前端 select 控件。
     *
     * @param queryParam 查询参数（自动绑定当前租户 saasId）
     * @return 会话消息分页列表（精简字段）
     */
    @GetMapping("/listLite")
    @Operation(summary = "轻量级列表session消息", description = "轻量级列表session消息，一般用于select控件。")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.USER, log = ActionLog.NONE)
    public ResponseData<PageList<AiSessionMsg>> listLite(AiSessionMsgQueryParam queryParam) {
        queryParam.SELECT_SQL( "SELECT id,saas_id,user_id,config_id,session_id,user_prompt,request_date,response_start_date,response_end_date,state from ai_session_msg " );
        return dao.list(AiSessionMsg.class, queryParam);
    }

    /**
     * 按主键加载单条session消息。
     *
     * @param id 主键ID
     * @return 会话消息
     */
    @GetMapping("/load")
    @Operation(summary = "加载session消息", description = "加载session消息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<AiSessionMsg> load(@Parameter(description = "主键ID", required = true) @RequestParam long id) {
        AuthServiceHelper.logRef(AiSessionMsg.class,id);
        return dao.queryForObject(AiSessionMsg.class, new AuthIdQueryParam(AuthServiceHelper.getSaasId(), id));
    }

    /**
     * 删除session消息（软删除：状态 → 已删除）。
     *
     * @param id     主键ID
     * @param remark 操作备注
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除session消息", description = "删除session消息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiSessionMsg.class,id,remark);
        return dao.update(new AiSessionMsg().state(CommonState.DELETED.getValue()), new AuthIdStateQueryParam(AuthServiceHelper.getSaasId(), id, CommonState.ENABLED.getValue()));
    }

}
