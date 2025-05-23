package uw.ai.center.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uw.ai.center.dto.UserModelConfigQueryParam;
import uw.ai.center.entity.AiModelConfig;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.common.dto.ResponseData;
import uw.dao.DaoManager;
import uw.dao.DataList;
import uw.dao.TransactionException;

/**
 * ModelUserController.
 */
@RestController
@Tag(name = "ModelUser接口")
@RequestMapping("/user/model")
public class AiModelUserController {
    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 列表AI服务模型。
     *
     * @param queryParam
     * @return
     * @throws TransactionException
     */
    @GetMapping("/list")
    @Operation(summary = "列表AI服务模型", description = "列表AI服务模型")
    @MscPermDeclare(auth = AuthType.NONE, log = ActionLog.BASE)
    public ResponseData<DataList<AiModelConfig>> list(UserModelConfigQueryParam queryParam) {
        queryParam.SELECT_SQL("SELECT id, saas_id, mch_id, vendor_class, config_code, config_name, config_desc, api_url, model_main, model_embed, create_date, modify_date, state from ai_model_config ");
        return dao.list(AiModelConfig.class, queryParam);
    }
}
