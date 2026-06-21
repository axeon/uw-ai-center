package uw.ai.center.controller.admin.log;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.common.app.dto.SysCritLogQueryParam;
import uw.common.app.entity.SysCritLog;
import uw.common.response.ResponseData;
import uw.dao.DaoManager;
import uw.common.data.PageList;
import uw.dao.TransactionException;


/**
 * 关键日志管理。
 * <p>平台管理（ADMIN）角色的关键操作日志查询接口，路径前缀 {@code /admin/log/critLog}。
 */
@RestController
@RequestMapping("/admin/log/critLog")
@Tag(name = "关键日志", description = "关键日志")
@MscPermDeclare(user = UserType.ADMIN)
public class SysCritLogController {

    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 分页列表关键操作日志。
     *
     * @param queryParam 日志查询参数（按 bizType / bizId 过滤）
     * @return 关键日志分页列表
     */
    @GetMapping("/list")
    @Operation(summary = "关键日志查询", description = "列表关键日志")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<SysCritLog>> list(SysCritLogQueryParam queryParam){
        AuthServiceHelper.logRef( SysCritLog.class );
        return dao.list( SysCritLog.class, queryParam );
    }

}