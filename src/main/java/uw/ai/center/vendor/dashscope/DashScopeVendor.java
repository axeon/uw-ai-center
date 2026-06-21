package uw.ai.center.vendor.dashscope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uw.ai.center.constant.ModelType;
import uw.ai.center.vendor.dashscope.imageModel.DashScopeImageModel;
import uw.ai.center.vendor.dashscope.imageModel.DashScopeImageParam;
import uw.ai.center.vendor.dashscope.realtimeTranscriptionModel.DashScopeAudioParam;
import uw.ai.center.vendor.dashscope.realtimeTranscriptionModel.DashScopeRealtimeTranscriptionModel;
import uw.ai.center.vendor.dashscope.realtimeTranscriptionModel.RealtimeTranscriptionModel;
import uw.ai.center.vendor.dashscope.ttsModel.DashScopeTtsParam;
import uw.ai.center.vo.AiModelConfigData;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vendor.AiVendorClientWrapper;
import uw.common.app.vo.JsonConfigBox;
import uw.common.app.vo.JsonConfigParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DashScope 供应商实现。
 * 通过阿里云 DashScope 原生 HTTP API 接入图片生成、语音识别、语音合成等能力。
 */
@Service
public class DashScopeVendor implements AiVendor {

    private static final Logger logger = LoggerFactory.getLogger(DashScopeVendor.class);

    @Override
    public String vendorName() {
        return "DashScope";
    }

    @Override
    public String vendorDesc() {
        return "阿里云DashScope原生API";
    }

    @Override
    public String vendorVersion() {
        return "1.0.0";
    }

    @Override
    public String vendorIcon() {
        return "";
    }

    @Override
    public List<JsonConfigParam> configParam() {
        List<JsonConfigParam> params = new ArrayList<>();
        params.addAll(Arrays.asList(DashScopeImageParam.Config.values()));
        params.addAll(Arrays.asList(DashScopeAudioParam.Config.values()));
        params.addAll(Arrays.asList(DashScopeTtsParam.Config.values()));
        return params;
    }

    @Override
    public AiVendorClientWrapper buildClientWrapper(AiModelConfigData configData) {
        ModelType modelType = ModelType.of(configData.getModelType());
        if (modelType == null) {
            logger.warn("未知的模型类型: {}, configId={}", configData.getModelType(), configData.getId());
            return null;
        }
        return switch (modelType) {
            case IMAGE_GENERATION -> buildImageGeneration(configData);
            case AUDIO_TRANSCRIPTION -> buildAudioTranscription(configData);
            case TTS -> {
                logger.warn("DashScopeVendor暂不支持模型类型: {}，将在后续功能点中实现", modelType);
                yield null;
            }
            default -> {
                logger.warn("DashScopeVendor不支持模型类型: {}", modelType);
                yield null;
            }
        };
    }

    /**
     * 构建图片生成客户端。
     */
    /**
     * 构建图片生成客户端（通义万相）。
     * <p>从 configParam 读取 image.size / image.style / image.n 参数，封装为 {@link DashScopeImageModel}。
     *
     * @param configData 模型配置数据
     * @return 封装了图片生成模型的客户端
     */
    private AiVendorClientWrapper buildImageGeneration(AiModelConfigData configData) {
        JsonConfigBox configParamBox = configData.getConfigParamBox();
        Map<String, Object> params = new HashMap<>();
        if (configParamBox != null) {
            String size = configParamBox.getParam("image.size", null);
            String style = configParamBox.getParam("image.style", null);
            int n = configParamBox.getIntParam("image.n", 0);
            if (size != null) {
                params.put("size", size);
            }
            if (style != null) {
                params.put("style", style);
            }
            if (n > 0) {
                params.put("n", n);
            }
        }

        var imageModel = new DashScopeImageModel(
                configData.getApiUrl(),
                configData.getApiKeyRaw(),
                configData.getModelName(),
                params
        );

        return new AiVendorClientWrapper(configData, this, null, null, null, imageModel, null, null);
    }

    /**
     * 构建实时语音识别客户端（DashScope Fun-ASR 协议）。
     * <p>
     * 鉴权方式：使用 ai_model_api.api_key 作为 Bearer Token（请求头 Authorization）。
     * 模型名：从 ai_model_config.model_name 读取（如 fun-asr-realtime）。
     * 业务空间ID：可选，通过 configParam 的 dashscope.workspace_id 配置。
     * 其他识别参数（format/sample_rate/language_hints 等）通过 configParam 的 audio.* 配置。
     */
    private AiVendorClientWrapper buildAudioTranscription(AiModelConfigData configData) {
        return new AiVendorClientWrapper(configData, this, null, null, null, null,
                createAudioTranscriptionModel(configData), null);
    }

    /**
     * 按需创建独立的实时语音识别模型实例。
     * 模型实例基于不可变配置，start() 时才建立 WebSocket，因此每次请求新建实例成本极低，
     * 却可彻底避免多请求共享同一实例导致的会话冲突。
     */
    @Override
    public RealtimeTranscriptionModel createAudioTranscriptionModel(AiModelConfigData configData) {
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

    @Override
    public List<String> listModel(String apiUrl, String apiKey) {
        return List.of();
    }
}
