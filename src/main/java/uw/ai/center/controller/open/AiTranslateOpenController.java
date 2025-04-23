package uw.ai.center.controller.open;

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
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.common.dto.ResponseData;

/**
 * Ai翻译接口。
 */
@RestController
@RequestMapping("/open/translate")
@Tag(name = "Ai翻译接口")
@ResponseAdviceIgnore
public class AiTranslateOpenController {
    private static final Logger logger = LoggerFactory.getLogger( AiTranslateOpenController.class );


    /**
     * 翻译列表。
     */
    @PostMapping("/translateList")
    public ResponseData<AiTranslateResultData[]> translateList(@RequestBody AiTranslateListParam param) {
        return AiTranslateService.translateListEntity( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(),
                "anonymous", param );
    }

    /**
     * 翻译Map。
     */
    @PostMapping("/translateMap")
    public ResponseData<AiTranslateResultData[]> translateMap(@RequestBody AiTranslateMapParam param) {
        return AiTranslateService.translateMapEntity( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(),
                "anonymous", param );
    }

}


