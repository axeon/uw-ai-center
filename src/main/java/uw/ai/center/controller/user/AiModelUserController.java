package uw.ai.center.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uw.ai.center.dto.UserModelConfigQueryParam;
import uw.ai.center.entity.AiModelConfig;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.common.response.ResponseData;
import uw.dao.DaoManager;
import uw.common.data.PageList;
import uw.dao.TransactionException;

/**
 * 普通用户模型配置查询接口。
 * <p>面向 C 端用户，只读列出当前租户下启用的 AI 模型配置（脱敏，不含密钥），供前端选择模型。
 */
@RestController
@Tag(name = "AI模型接口")
@RequestMapping("/user/model")
public class AiModelUserController {
    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 列表AI服务模型（只读，脱敏不含密钥，供前端选择模型）。
     *
     * @param queryParam 查询参数（自动绑定当前租户 saasId）
     * @return 模型配置分页列表（精简字段）
     * @throws TransactionException 查询异常
     */
    @GetMapping("/list")
    @Operation(summary = "列表AI服务模型", description = "列表AI服务模型")
    @MscPermDeclare(auth = AuthType.NONE, log = ActionLog.BASE)
    public ResponseData<PageList<AiModelConfig>> list(UserModelConfigQueryParam queryParam) {
        queryParam.saasId(AuthServiceHelper.getSaasId());
        queryParam.SELECT_SQL( "SELECT id,saas_id,mch_id,api_id,vendor_class,model_type,model_tag,config_code,config_name,model_name,state,create_date,modify_date from ai_model_config " );
        return dao.list(AiModelConfig.class, queryParam);
    }
}
