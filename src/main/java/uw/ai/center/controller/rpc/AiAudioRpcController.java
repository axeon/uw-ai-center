package uw.ai.center.controller.rpc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uw.ai.center.service.AiAudioService;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.common.response.ResponseData;

/**
 * AI语音 RPC 接口。
 * <p>供其他微服务通过 RPC 调用，上传音频文件进行实时语音识别（ASR），返回识别文本。
 */
@RestController
@Tag(name = "AudioRPC接口")
@RequestMapping("/rpc/audio")
@Primary
@ResponseAdviceIgnore
public class AiAudioRpcController {

    /**
     * 语音识别：上传音频文件，转为文本（内部走实时识别 API）。
     *
     * @param saasId    租户ID
     * @param userId    用户ID
     * @param userType  用户类型
     * @param userInfo  用户信息
     * @param configId  AI 模型配置ID（须为 AUDIO_TRANSCRIPTION 类型）
     * @param audioFile 音频文件（单次约 60 秒上限）
     * @return 识别出的文本
     */
    @PostMapping("/transcribe")
    @Operation(summary = "语音识别", description = "上传音频文件，转为文本（内部使用实时识别API）")
    @MscPermDeclare(user = UserType.RPC, auth = AuthType.NONE, log = ActionLog.BASE)
    public ResponseData<String> transcribe(
            @Parameter(description = "租户ID", required = true) @RequestParam long saasId,
            @Parameter(description = "用户ID", required = true) @RequestParam long userId,
            @Parameter(description = "用户类型", required = true) @RequestParam int userType,
            @Parameter(description = "用户信息") @RequestParam String userInfo,
            @Parameter(description = "AI模型配置ID", required = true) @RequestParam long configId,
            @Parameter(description = "音频文件", required = true) @RequestParam MultipartFile audioFile) {
        return AiAudioService.transcribeFile(saasId, userId, userType, userInfo, configId, audioFile);
    }
}
