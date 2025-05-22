package uw.ai.center.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uw.ai.center.dto.UserRagLibQueryParam;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.center.entity.AiRagLib;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.common.dto.ResponseData;
import uw.dao.DaoManager;
import uw.dao.DataList;
import uw.dao.TransactionException;


/**
 * RagUserController
 */
@RestController
@Tag(name = "RagUser接口")
@RequestMapping("/user/rag")
public class AiRagUserController {
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
    public ResponseData<DataList<AiRagLib>> list(UserRagLibQueryParam queryParam) {
        AuthServiceHelper.logRef( AiModelConfig.class );
        queryParam.SELECT_SQL( "select id, saas_id, lib_type, lib_name, lib_desc, embed_config_id, embed_model_name, create_date, modify_date, state FROM ai_rag_lib ");
        return dao.list( AiRagLib.class, queryParam );
    }
}
