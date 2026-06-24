package uw.ai.center.controller.rpc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.service.AiImageService;
import uw.ai.center.vendor.AiVendorHelper;
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
 * <p>供其他微服务通过 RPC 调用，根据文本提示词生成图片，返回图片 URL 列表及会话 ID。
 */
@RestController
@Tag(name = "AI图片生成接口")
@RequestMapping("/rpc/image")
@Primary
@ResponseAdviceIgnore
public class AiImageRpcController {

    /**
     * 生成图片：根据文本提示词生成图片，返回图片 URL 列表及会话 ID。
     * <p>configId 与 configCode 至少传一个，优先 configId；都为空时返回错误。
     *
     * @param param 图片生成参数（含身份、configId/configCode、sessionId、提示词）
     * @return 图片生成结果（URL 列表 + 会话ID）
     */
    @PostMapping("/generate")
    @Operation(summary = "生成图片", description = "根据文本提示词生成图片，返回图片URL列表及会话ID")
    @MscPermDeclare(user = UserType.RPC, auth = AuthType.NONE, log = ActionLog.BASE)
    public ResponseData<AiImageResultData> generate(@ModelAttribute AiImageGenerateParam param) {
        Long configId = AiVendorHelper.resolveConfigId(param.getConfigId(), param.getConfigCode());
        if (configId == null) {
            return ResponseData.errorMsg("configId 和 configCode 不能同时为空，或 configCode 无效");
        }
        return AiImageService.generate(
                param.getSaasId(), param.getUserId(), param.getUserType(), param.getUserInfo(),
                configId, param.getSessionId(), param.getUserPrompt());
    }
}
