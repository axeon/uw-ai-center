package uw.ai.center.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.service.AiImageService;
import uw.ai.vo.AiImageResultData;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.common.response.ResponseData;

/**
 * AI图片生成用户接口。
 * <p>面向 C 端用户，根据文本提示词生成图片，返回图片 URL 列表及会话 ID。
 * 用户身份由 AuthServiceHelper 自动注入。
 */
@RestController
@RequestMapping("/user/image")
@Tag(name = "AI图片生成", description = "AI图片生成")
@ResponseAdviceIgnore
public class AiImageUserController {

    /**
     * 生成图片：根据文本提示词生成图片，返回图片 URL 列表及会话 ID。
     * <p>用户身份由 AuthServiceHelper 自动注入。
     *
     * @param configId  AI 模型配置ID（须为 IMAGE_GENERATION 类型）
     * @param sessionId 会话ID（大于 0 则保存到指定会话，否则自动创建）
     * @param prompt    图片提示词
     * @return 图片生成结果（URL 列表 + 会话ID）
     */
    @PostMapping("/generate")
    @Operation(summary = "生成图片", description = "根据文本提示词生成图片，返回图片URL列表及会话ID")
    @MscPermDeclare(auth = AuthType.NONE, log = ActionLog.BASE)
    public ResponseData<AiImageResultData> generate(
            @Parameter(description = "AI模型配置ID", required = true) @RequestParam long configId,
            @Parameter(description = "会话ID，若大于0则保存到指定会话，否则自动创建新会话") @RequestParam(defaultValue = "0") long sessionId,
            @Parameter(description = "图片提示词", required = true) @RequestParam String prompt) {
        return AiImageService.generate(
                AuthServiceHelper.getSaasId(),
                AuthServiceHelper.getUserId(),
                AuthServiceHelper.getUserType(),
                AuthServiceHelper.getUserName(),
                configId, sessionId, prompt);
    }
}
