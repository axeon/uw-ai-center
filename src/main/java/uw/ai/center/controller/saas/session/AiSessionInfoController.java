package uw.ai.center.controller.saas.session;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiSessionInfoQueryParam;
import uw.ai.center.entity.AiSessionInfo;
import uw.app.common.dto.AuthIdQueryParam;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.common.constant.StateCommon;
import uw.common.dto.ResponseData;
import uw.dao.DaoFactory;
import uw.dao.DataList;
import uw.dao.TransactionException;


/**
 * session会话管理。
 */
@RestController
@RequestMapping("/saas/session/info")
@Tag(name = "session会话管理", description = "session会话增删改查列管理")
@MscPermDeclare(user = UserType.SAAS)
public class AiSessionInfoController {

    private final DaoFactory dao = DaoFactory.getInstance();

    /**
     * 列表session会话。
     *
     * @param queryParam
     * @return
     * @throws TransactionException
     */
    @GetMapping("/list")
    @Operation(summary = "列表session会话", description = "列表session会话")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public DataList<AiSessionInfo> list(AiSessionInfoQueryParam queryParam) throws TransactionException {
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
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.USER, log = ActionLog.NONE)
    public DataList<AiSessionInfo> liteList(AiSessionInfoQueryParam queryParam) throws TransactionException {
        queryParam.SELECT_SQL( "SELECT id,saas_id,mch_id,user_id,user_type,group_id,user_name,nick_name,real_name,session_name,create_date,modify_date,state from ai_session_info " );
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
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public AiSessionInfo load(@Parameter(description = "主键ID", required = true) @RequestParam long id) throws TransactionException {
        AuthServiceHelper.logRef(AiSessionInfo.class,id);
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
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiSessionInfo.class,id,"删除session会话！操作备注："+remark);
        AiSessionInfo aiSessionInfo = dao.queryForSingleObject(AiSessionInfo.class, new AuthIdQueryParam(id));
        if (aiSessionInfo == null) {
            return ResponseData.warnMsg("未找到指定id的session会话！");
        }
        if (aiSessionInfo.getState()!=StateCommon.DISABLED.getValue()){
            return ResponseData.warnMsg("删除session会话失败！当前状态不是禁用状态！");
        }            
//        aiSessionInfo.setModifyDate(new Date());
        aiSessionInfo.setState(StateCommon.DELETED.getValue());
        dao.update(aiSessionInfo);
        return ResponseData.success();
    }

}