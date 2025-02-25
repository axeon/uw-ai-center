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
//        aiSessionInfo.setModifyDate(new Date());
        aiSessionInfo.setState(StateCommon.DELETED.getValue());
        dao.update(aiSessionInfo);
        return ResponseData.success();
    }

}