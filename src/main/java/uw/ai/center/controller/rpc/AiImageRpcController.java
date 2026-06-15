package uw.ai.center.controller.rpc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.service.AiImageService;
import uw.ai.vo.AiImageGenerateParam;
import uw.ai.vo.AiImageResultData;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.common.response.ResponseData;

/**
 * AI图片生成RPC接口。
 * 供uw-ai-project等客户端通过RPC调用，执行图片生成操作。
 */
@RestController
@Tag(name = "ImageRPC接口")
@RequestMapping("/rpc/image")
@Primary
@ResponseAdviceIgnore
public class AiImageRpcController {

    @PostMapping("/generate")
    @Operation(summary = "生成图片", description = "根据文本提示词生成图片，返回图片URL列表及会话ID")
    @MscPermDeclare(user = UserType.RPC, auth = AuthType.NONE, log = ActionLog.BASE)
    public ResponseData<AiImageResultData> generate(@ModelAttribute AiImageGenerateParam param) {
        return AiImageService.generate(
                param.getSaasId(), param.getUserId(), param.getUserType(), param.getUserInfo(),
                param.getConfigId(), param.getSessionId(), param.getPrompt());
    }
}
