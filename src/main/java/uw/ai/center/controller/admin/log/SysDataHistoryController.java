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
import uw.common.app.dto.SysDataHistoryQueryParam;
import uw.common.app.entity.SysDataHistory;
import uw.common.response.ResponseData;
import uw.dao.DaoManager;
import uw.common.data.PageList;
import uw.dao.TransactionException;


/**
 * 数据历史管理。
 * <p>平台管理（ADMIN）角色的数据变更历史查询接口，路径前缀 {@code /admin/log/dataHistory}。
 */
@RestController
@RequestMapping("/admin/log/dataHistory")
@Tag(name = "数据历史", description = "数据历史")
@MscPermDeclare(user = UserType.ADMIN)
public class SysDataHistoryController {

    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 分页列表数据历史。
     *
     * @param queryParam 历史查询参数（按 entityClass / entityId 过滤）
     * @return 数据历史分页列表
     */
    @GetMapping("/list")
    @Operation(summary = "数据历史查询", description = "列表数据历史")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<SysDataHistory>> list(SysDataHistoryQueryParam queryParam){
        AuthServiceHelper.logRef( SysDataHistory.class );
        return dao.list( SysDataHistory.class, queryParam );
    }

}