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
import uw.common.response.ResponseData;

/**
 * Ai翻译接口。
 * <p>面向 C 端用户的翻译接口，路径前缀 {@code /user/translate}，身份由 AuthServiceHelper 注入。
 */
@RestController
@RequestMapping("/user/translate")
@Tag(name = "AI翻译接口")
public class AiTranslateUserController {
    private static final Logger log = LoggerFactory.getLogger( AiTranslateUserController.class );

    /**
     * 翻译列表：将 JSON 数组中的文本分别翻译为目标语言，结果转为结构化对象。
     *
     * @param param 翻译参数（含 configId、目标语言列表、待译文本列表）
     * @return 翻译结果数组
     */
    @PostMapping("/translateList")
    @Operation(summary = "翻译列表", description = "翻译列表")
    @MscPermDeclare(auth = AuthType.NONE, log = ActionLog.BASE)
    public ResponseData<AiTranslateResultData[]> translateList(@RequestBody AiTranslateListParam param) {
        return AiTranslateService.translateListEntity( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), "guest", param );
    }

    /**
     * 翻译 Map：将 JSON Map 的 value 分别翻译为目标语言，结果转为结构化对象。
     *
     * @param param 翻译参数（含 configId、目标语言列表、待译文本 Map）
     * @return 翻译结果数组
     */
    @PostMapping("/translateMap")
    @Operation(summary = "翻译Map", description = "翻译Map")
    @MscPermDeclare(auth = AuthType.NONE, log = ActionLog.BASE)
    public ResponseData<AiTranslateResultData[]> translateMap(@RequestBody AiTranslateMapParam param) {
        return AiTranslateService.translateMapEntity( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), "guest", param );
    }

}


