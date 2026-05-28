package uw.ai.center.controller.admin.model;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiApiConfigQueryParam;
import uw.ai.center.entity.AiModelApi;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.common.app.constant.CommonState;
import uw.common.app.dto.IdStateQueryParam;
import uw.common.dto.ResponseData;
import uw.dao.DaoManager;
import uw.dao.DataList;

/**
 * AI模型API连接配置管理（admin）。
 */
@RestController
@RequestMapping("/admin/model/api")
@Tag(name = "AI模型API连接配置管理", description = "AI模型API连接配置增删改查")
@MscPermDeclare(user = UserType.ADMIN)
public class AiModelApiController {

    private final DaoManager dao = DaoManager.getInstance();

    @GetMapping("/list")
    @Operation(summary = "列表API连接配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<DataList<AiModelApi>> list(AiApiConfigQueryParam queryParam) {
        return dao.list(AiModelApi.class, queryParam);
    }

    @GetMapping("/load")
    @Operation(summary = "加载API连接配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.USER, log = ActionLog.NONE)
    public ResponseData<AiModelApi> load(@RequestParam long id) {
        return dao.load(AiModelApi.class, id);
    }

    @PostMapping("/insert")
    @Operation(summary = "新增API连接配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiModelApi> insert(@RequestBody AiModelApi entity) {
        entity.setId(dao.getSequenceId(AiModelApi.class));
        entity.setState(CommonState.ENABLED.getValue());
        return dao.save(entity);
    }

    @PostMapping("/update")
    @Operation(summary = "更新API连接配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiModelApi> update(@RequestBody AiModelApi entity) {
        return dao.update(entity);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除API连接配置")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<Integer> delete(@RequestBody IdStateQueryParam param) {
        return dao.update(new AiModelApi().state(CommonState.DELETED.getValue()),
                new IdStateQueryParam(param.getId(), CommonState.DISABLED.getValue()));
    }
}