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
import uw.common.response.ResponseData;
import uw.dao.DaoManager;
import uw.common.data.PageList;
import uw.dao.TransactionException;


/**
 * 普通用户 RAG 知识库查询接口。
 * <p>面向 C 端用户，只读列出当前租户下启用的 RAG 库，供前端在对话时选择挂载的知识库。
 */
@RestController
@Tag(name = "RagUser接口")
@RequestMapping("/user/rag")
public class AiRagUserController {
    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 列表RAG库（只读，供前端在对话时选择挂载的知识库）。
     *
     * @param queryParam 查询参数（自动绑定当前租户 saasId）
     * @return RAG 库分页列表（精简字段）
     * @throws TransactionException 查询异常
     */
    @GetMapping("/list")
    @Operation(summary = "列表RAG库", description = "列表RAG库")
    @MscPermDeclare(auth = AuthType.NONE, log = ActionLog.BASE)
    public ResponseData<PageList<AiRagLib>> list(UserRagLibQueryParam queryParam) {
        AuthServiceHelper.logRef( AiModelConfig.class );
        queryParam.saasId(AuthServiceHelper.getSaasId());
        queryParam.SELECT_SQL( "select id, saas_id, lib_type, lib_name, lib_desc, embed_config_id, embed_model_name, create_date, modify_date, state FROM ai_rag_lib ");
        return dao.list( AiRagLib.class, queryParam );
    }
}
