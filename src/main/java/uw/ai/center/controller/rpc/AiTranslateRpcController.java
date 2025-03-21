package uw.ai.center.controller.rpc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uw.ai.center.service.AiTranslateService;
import uw.ai.rpc.AiTranslateRpc;
import uw.ai.vo.AiTranslateListParam;
import uw.ai.vo.AiTranslateMapParam;
import uw.ai.vo.AiTranslateResultData;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.auth.service.constant.UserType;
import uw.common.dto.ResponseData;

@RestController
@Tag(name = "TranslateRPC接口")
@RequestMapping("/rpc/translate")
@Primary
@ResponseAdviceIgnore
public class AiTranslateRpcController implements AiTranslateRpc {
    private static final Logger logger = LoggerFactory.getLogger( AiTranslateRpcController.class );


    /**
     * 翻译列表。
     */
    @Override
    @PostMapping("/translateList")
    @Operation(summary = "翻译列表", description = "翻译列表")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<AiTranslateResultData[]> translateList(@RequestBody AiTranslateListParam param) {
        return AiTranslateService.translateListEntity( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), "guest", param );
    }

    /**
     * 翻译Map。
     */
    @Override
    @PostMapping("/translateMap")
    @Operation(summary = "翻译Map", description = "翻译Map")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<AiTranslateResultData[]> translateMap(@RequestBody AiTranslateMapParam param) {
        return AiTranslateService.translateMapEntity( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), "guest", param );
    }

}


