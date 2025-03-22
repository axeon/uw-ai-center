package uw.ai.center.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uw.ai.center.service.AiTranslateService;
import uw.ai.vo.AiTranslateListParam;
import uw.ai.vo.AiTranslateMapParam;
import uw.ai.vo.AiTranslateResultData;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.common.dto.ResponseData;

/**
 * Ai翻译接口。
 */
@RestController
@RequestMapping("/user/translate")
@Tag(name = "Ai翻译接口")
@ResponseAdviceIgnore
public class AiTranslateUserController {
    private static final Logger logger = LoggerFactory.getLogger( AiTranslateUserController.class );

    /**
     * 翻译列表。
     */
    @PostMapping("/translateList")
    @Operation(summary = "翻译列表", description = "翻译列表")
    @MscPermDeclare(auth = AuthType.NONE, log = ActionLog.BASE)
    public ResponseData<AiTranslateResultData[]> translateList(@RequestBody AiTranslateListParam param) {
        return AiTranslateService.translateListEntity( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), "guest", param );
    }

    /**
     * 翻译Map。
     */
    @PostMapping("/translateMap")
    @Operation(summary = "翻译Map", description = "翻译Map")
    @MscPermDeclare(auth = AuthType.NONE, log = ActionLog.BASE)
    public ResponseData<AiTranslateResultData[]> translateMap(@RequestBody AiTranslateMapParam param) {
        return AiTranslateService.translateMapEntity( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), "guest", param );
    }

}


