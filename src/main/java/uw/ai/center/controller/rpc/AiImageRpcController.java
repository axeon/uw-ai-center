package uw.ai.center.controller.rpc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.service.AiImageService;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.common.dto.ResponseData;

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
    @Operation(summary = "生成图片", description = "根据文本提示词生成图片，返回图片URL")
    @MscPermDeclare(user = UserType.RPC, auth = AuthType.NONE, log = ActionLog.BASE)
    public ResponseData<String> generate(
            @Parameter(description = "租户ID", required = true) @RequestParam long saasId,
            @Parameter(description = "用户ID", required = true) @RequestParam long userId,
            @Parameter(description = "用户类型", required = true) @RequestParam int userType,
            @Parameter(description = "用户信息") @RequestParam String userInfo,
            @Parameter(description = "AI模型配置ID", required = true) @RequestParam long configId,
            @Parameter(description = "图片提示词", required = true) @RequestParam String prompt) {
        return AiImageService.generate(saasId, userId, userType, userInfo, configId, prompt);
    }
}
