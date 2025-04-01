package uw.ai.center.controller.ops.session;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiSessionMsgQueryParam;
import uw.ai.center.entity.AiSessionMsg;
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
 * session消息管理。
 */
@RestController
@RequestMapping("/ops/session/info/msg")
@Tag(name = "session消息管理", description = "session消息增删改查列管理")
@MscPermDeclare(user = UserType.OPS)
public class AiSessionMsgController {

    private final DaoFactory dao = DaoFactory.getInstance();

    /**
     * 列表session消息。
     *
     * @param queryParam
     * @return
     * @throws TransactionException
     */
    @GetMapping("/list")
    @Operation(summary = "列表session消息", description = "列表session消息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public DataList<AiSessionMsg> list(AiSessionMsgQueryParam queryParam) throws TransactionException {
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
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.USER, log = ActionLog.NONE)
    public DataList<AiSessionMsg> liteList(AiSessionMsgQueryParam queryParam) throws TransactionException {
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
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public AiSessionMsg load(@Parameter(description = "主键ID", required = true) @RequestParam long id) throws TransactionException {
        AuthServiceHelper.logRef(AiSessionMsg.class,id);
        return dao.load(AiSessionMsg.class, id);
    }

    /**
     * 删除session消息。
     *
     * @param id
     * @throws TransactionException
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除session消息", description = "删除session消息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiSessionMsg.class,id,"删除session消息！操作备注："+remark);
        AiSessionMsg aiSessionMsg = dao.load(AiSessionMsg.class, id);
        if (aiSessionMsg == null) {
            return ResponseData.warnMsg("未找到指定id的session消息！");
        }
        if (aiSessionMsg.getState()!=StateCommon.ENABLED.getValue()){
            return ResponseData.warnMsg("删除session消息失败！当前状态不是正常状态！");
        }            
        aiSessionMsg.setState(StateCommon.DELETED.getValue());
        dao.update(aiSessionMsg);
        return ResponseData.success();
    }

}