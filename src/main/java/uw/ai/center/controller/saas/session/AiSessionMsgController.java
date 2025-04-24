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
import uw.common.app.dto.AuthIdStateQueryParam;
import uw.common.app.dto.IdStateQueryParam;
import uw.common.dto.ResponseData;
import uw.dao.DaoManager;
import uw.dao.DataList;
import uw.dao.TransactionException;


/**
 * session消息管理。
 */
@RestController
@RequestMapping("/saas/session/info/msg")
@Tag(name = "session消息管理", description = "session消息增删改查列管理")
@MscPermDeclare(user = UserType.SAAS)
public class AiSessionMsgController {

    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 列表session消息。
     *
     * @param queryParam
     * @return
     * @throws TransactionException
     */
    @GetMapping("/list")
    @Operation(summary = "列表session消息", description = "列表session消息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<DataList<AiSessionMsg>> list(AiSessionMsgQueryParam queryParam) {
        AuthServiceHelper.logRef(AiSessionMsg.class);
        return dao.list(AiSessionMsg.class, queryParam);
    }

    /**
     * 轻量级列表session消息，一般用于select控件。
     *
     * @return
     */
    @GetMapping("/liteList")
    @Operation(summary = "轻量级列表session消息", description = "轻量级列表session消息，一般用于select控件。")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.USER, log = ActionLog.NONE)
    public ResponseData<DataList<AiSessionMsg>> liteList(AiSessionMsgQueryParam queryParam) {
        queryParam.SELECT_SQL( "SELECT id,session_id,create_date,state from ai_session_msg " );
        return dao.list(AiSessionMsg.class, queryParam);
    }

    /**
     * 加载session消息。
     *
     * @param id
     * @throws TransactionException
     */
    @GetMapping("/load")
    @Operation(summary = "加载session消息", description = "加载session消息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<AiSessionMsg> load(@Parameter(description = "主键ID", required = true) @RequestParam long id) {
        AuthServiceHelper.logRef(AiSessionMsg.class,id);
        return dao.load(AiSessionMsg.class, id);
    }

    /**
     * 删除session消息。
     *
     * @param id
     * @throws TransactionException
     */
    /**
     * 删除session消息。
     *
     * @param id
     *
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除session消息", description = "删除session消息")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiSessionMsg.class,id,remark);
        return dao.update(new AiSessionMsg().state(CommonState.DELETED.getValue()), new AuthIdStateQueryParam(id, CommonState.ENABLED.getValue()));
    }

}