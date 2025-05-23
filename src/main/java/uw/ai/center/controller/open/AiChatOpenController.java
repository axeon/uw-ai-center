package uw.ai.center.controller.open;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uw.ai.center.service.AiChatService;
import uw.ai.vo.AiChatGenerateParam;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.common.dto.ResponseData;

/**
 * AiChat接口。
 */
@RestController
@RequestMapping("/open/chat")
@Tag(name = "AiChat接口")
@ResponseAdviceIgnore
public class AiChatOpenController {
    private static final Logger logger = LoggerFactory.getLogger( AiChatOpenController.class );

    /**
     * ChatClient 简单调用
     */
    @PostMapping("/generate")
    public ResponseData<String> generate(@ModelAttribute AiChatGenerateParam param) {
        return AiChatService.generate( AuthServiceHelper.getSaasId(), AuthServiceHelper.getUserId(), AuthServiceHelper.getUserType(), "anonymous",
                param.getConfigId(), param.getSystemPrompt(), param.getUserPrompt(), param.getToolList(), param.getToolContext(), param.getFileList(),param.getRagLibIds());
    }

}


