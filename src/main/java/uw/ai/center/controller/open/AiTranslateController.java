package uw.ai.center.controller.open;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.service.AiTranslateService;
import uw.ai.center.vo.TranslateListParam;
import uw.ai.center.vo.TranslateMapParam;
import uw.ai.center.vo.TranslateResultData;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.common.dto.ResponseData;

/**
 * Ai翻译接口。
 */
@RestController
@RequestMapping("/open/ai/translate")
@Tag(name = "Ai翻译接口")
@ResponseAdviceIgnore
public class AiTranslateController {
    private static final Logger logger = LoggerFactory.getLogger( AiTranslateController.class );


    /**
     * 翻译列表。
     */
    @PostMapping("/translateList")
    public ResponseData<TranslateResultData[]> translateList(@RequestBody TranslateListParam param) {
        return AiTranslateService.translateListEntity( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), "guest", param );
    }

    /**
     * 翻译Map。
     */
    @PostMapping("/translateMap")
    public ResponseData<TranslateResultData[]> translateMap(@RequestBody TranslateMapParam param) {
        return AiTranslateService.translateMapEntity( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), "guest", param );
    }

}


