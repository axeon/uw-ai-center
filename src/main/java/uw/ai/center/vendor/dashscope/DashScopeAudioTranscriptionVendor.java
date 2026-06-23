package uw.ai.center.vendor.dashscope;

import org.springframework.stereotype.Service;
import uw.ai.center.vendor.client.AudioTranscriptionClient;
import uw.ai.center.vendor.dashscope.realtimeTranscriptionModel.DashScopeRealtimeTranscriptionModel;
import uw.ai.center.vendor.dashscope.realtimeTranscriptionModel.RealtimeTranscriptionModel;
import uw.ai.center.vo.AiModelConfigData;
import uw.common.app.vo.JsonConfigBox;

import java.util.HashMap;
import java.util.Map;

/**
 * DashScope 协议 AUDIO_TRANSCRIPTION 能力实现。
 * <p>只承担实时语音识别客户端（DashScope Fun-ASR 协议）的构建逻辑；元信息由协议入口 {@link DashScopeVendor} 统一提供。
 * 作为 Spring bean 存在，由协议入口注入并按 model_type 委托调用。
 */
@Service
public class DashScopeAudioTranscriptionVendor {

    /**
     * 构建实时语音识别客户端（DashScope Fun-ASR 协议）。
     * <p>仅作为配置载体，不持有 RealtimeTranscriptionModel 实例。实例创建逻辑通过 {@link java.util.function.Supplier}
     * 工厂注入到客户端中，客户端在 {@link AudioTranscriptionClient#createRealtimeSession()} 时现场创建独立实例。
     */
    public AudioTranscriptionClient buildAudioTranscriptionClient(AiModelConfigData configData) {
        return new AudioTranscriptionClient(configData, () -> createRealtimeTranscriptionModel(configData));
    }

    /**
     * 创建独立的实时语音识别模型实例。
     * <p>模型实例基于不可变配置，start() 时才建立 WebSocket，因此每次请求新建实例成本极低，
     * 却可彻底避免多请求共享同一实例导致的会话冲突。
     */
    private RealtimeTranscriptionModel createRealtimeTranscriptionModel(AiModelConfigData configData) {
        JsonConfigBox configParamBox = configData.getConfigParamBox();
        Map<String, Object> params = new HashMap<>();
        String workspaceId = null;

        if (configParamBox != null) {
            // 业务空间ID（可选）
            workspaceId = configParamBox.getParam("dashscope.workspace_id", "");

            // 音频识别参数
            String format = configParamBox.getParam("audio.format", null);
            int sampleRate = configParamBox.getIntParam("audio.sample_rate", 0);
            String languageHints = configParamBox.getParam("audio.language_hints", null);
            boolean semanticPunctuation = configParamBox.getBooleanParam("audio.semantic_punctuation", false);
            int maxSentenceSilence = configParamBox.getIntParam("audio.max_sentence_silence", 0);

            if (format != null && !format.isEmpty()) params.put("format", format);
            if (sampleRate > 0) params.put("sample_rate", sampleRate);
            if (languageHints != null && !languageHints.isEmpty()) params.put("language_hints", languageHints);
            params.put("semantic_punctuation_enabled", semanticPunctuation);
            if (maxSentenceSilence > 0) params.put("max_sentence_silence", maxSentenceSilence);

            // speech_noise_threshold 可选 float
            String noiseThresholdStr = configParamBox.getParam("audio.speech_noise_threshold", null);
            if (noiseThresholdStr != null && !noiseThresholdStr.isEmpty()) {
                try {
                    params.put("speech_noise_threshold", Double.parseDouble(noiseThresholdStr));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return new DashScopeRealtimeTranscriptionModel(
                configData.getApiKeyRaw(),
                configData.getModelName(),
                workspaceId,
                params
        );
    }
}
