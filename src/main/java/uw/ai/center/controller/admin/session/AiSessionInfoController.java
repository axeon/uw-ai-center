package uw.ai.center.controller.admin.session;

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
import uw.common.app.dto.IdStateQueryParam;
import uw.common.dto.ResponseData;
import uw.dao.DaoManager;
import uw.dao.DataList;
import uw.dao.TransactionException;


/**
 * session会话管理。
 */
@RestController
@RequestMapping("/admin/session/info")
@Tag(name = "session会话", description = "session会话")
@MscPermDeclare(user = UserType.ADMIN)
public class AiSessionInfoController {

    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 列表session会话。
     *
     * @param queryParam
     * @return
     * @throws TransactionException
     */
    @GetMapping("/list")
    @Operation(summary = "列表session会话", description = "列表session会话")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<DataList<AiSessionInfo>> list(AiSessionInfoQueryParam queryParam) {
        AuthServiceHelper.logRef(AiSessionInfo.class);
        return dao.list(AiSessionInfo.class, queryParam);
    }

    /**
     * 轻量级列表session会话，一般用于select控件。
     *
     * @return
     */
    @GetMapping("/liteList")
    @Operation(summary = "轻量级列表session会话", description = "轻量级列表session会话，一般用于select控件。")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.USER, log = ActionLog.NONE)
    public ResponseData<DataList<AiSessionInfo>> liteList(AiSessionInfoQueryParam queryParam) {
        queryParam.SELECT_SQL("SELECT id,saas_id,mch_id,user_id,user_type,group_id,user_name,nick_name,real_name,session_name,create_date,modify_date,state from ai_session_info ");
        return dao.list(AiSessionInfo.class, queryParam);
    }

    /**
     * 加载session会话。
     *
     * @param id
     * @throws TransactionException
     */
    @GetMapping("/load")
    @Operation(summary = "加载session会话", description = "加载session会话")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<AiSessionInfo> load(@Parameter(description = "主键ID", required = true) @RequestParam long id) {
        AuthServiceHelper.logRef(AiSessionInfo.class, id);
        return dao.queryForSingleObject(AiSessionInfo.class, new AuthIdQueryParam(id));
    }

    /**
     * 删除session会话。
     *
     * @param id
     * @throws TransactionException
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除session会话", description = "删除session会话")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) {
        AuthServiceHelper.logInfo(AiSessionInfo.class, id, remark);
        return dao.update(new AiSessionInfo().state(CommonState.DELETED.getValue()), new IdStateQueryParam(id, CommonState.ENABLED.getValue()));
    }

}