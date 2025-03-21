package uw.ai.center.controller.open;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.service.AiChatService;
import uw.ai.center.service.AiTranslateService;
import uw.ai.center.vo.TranslateListParam;
import uw.ai.center.vo.TranslateMapParam;
import uw.ai.center.vo.TranslateResultData;
import uw.ai.util.BeanOutputConverter;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.common.dto.ResponseData;
import uw.httpclient.json.JsonInterfaceHelper;

/**
 * 测试接口。
 */
@RestController
@RequestMapping("/open/ai")
@Tag(name = "AI接口")
@ResponseAdviceIgnore
public class AiChatController {
    private static final Logger logger = LoggerFactory.getLogger( AiChatController.class );

    /**
     * ChatClient 简单调用
     */
    @GetMapping("/generate")
    public ResponseData<String> generate(@RequestParam(defaultValue = "1") long configId, @RequestParam(defaultValue = "你是谁？") String userPrompt,
                                         @RequestParam(defaultValue = "") String systemPrompt, @RequestParam(defaultValue = "") String toolList) {
        return AiChatService.generate( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), "guest", configId, userPrompt, systemPrompt
                , null, null );
    }

    /**
     * 翻译列表。
     */
    @PostMapping("/translateList")
    public ResponseData<String> translateList(@RequestBody TranslateListParam param) {
        return AiTranslateService.translateList( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), "guest", param );
    }

    /**
     * 翻译Map。
     */
    @PostMapping("/translateMap")
    public ResponseData<String> translateMap(@RequestBody TranslateMapParam param) {
        return AiTranslateService.translateMap( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), "guest", param );
    }


}


