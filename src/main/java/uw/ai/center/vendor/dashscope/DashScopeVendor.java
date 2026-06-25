package uw.ai.center.vendor.dashscope;

import org.springframework.stereotype.Service;
import uw.ai.center.vendor.AiAudioTranscriptionVendor;
import uw.ai.center.vendor.AiImageGenerationVendor;
import uw.ai.center.vendor.AiRerankVendor;
import uw.ai.center.vendor.client.AudioTranscriptionClient;
import uw.ai.center.vendor.client.ImageGenerationClient;
import uw.ai.center.vendor.client.RerankClient;
import uw.ai.center.vendor.dashscope.image.DashScopeImageModel;
import uw.ai.center.vendor.dashscope.image.DashScopeImageParam;
import uw.ai.center.vendor.dashscope.rerank.DashScopeRerankModel;
import uw.ai.center.vendor.dashscope.rerank.DashScopeRerankParam;
import uw.ai.center.vendor.dashscope.transcription.DashScopeAudioParam;
import uw.ai.center.vendor.dashscope.transcription.DashScopeRealtimeTranscriptionModel;
import uw.ai.center.vendor.dashscope.transcription.RealtimeTranscriptionModel;
import uw.ai.center.vendor.dashscope.tts.DashScopeTtsParam;
import uw.ai.center.vo.AiModelConfigData;
import uw.common.app.vo.JsonConfigBox;
import uw.common.app.vo.JsonConfigParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DashScope 协议 vendor 实现（阿里云原生 API）。
 * <p>implements 列表即能力清单：{@link AiImageGenerationVendor} + {@link AiAudioTranscriptionVendor}
 * + {@link AiRerankVendor}，类型系统直接表达"本 vendor 支持哪些能力"，无需点开源码看哪些方法被覆写。
 */
@Service
public class DashScopeVendor implements AiImageGenerationVendor, AiAudioTranscriptionVendor, AiRerankVendor {

    /**
     * {@inheritDoc}
     * @return "DashScope"
     */
    @Override
    public String vendorName() {
        return "DashScope";
    }

    /**
     * {@inheritDoc}
     * @return "阿里云DashScope原生API"
     */
    @Override
    public String vendorDesc() {
        return "阿里云DashScope原生API";
    }

    /**
     * {@inheritDoc}
     * @return "1.0.0"
     */
    @Override
    public String vendorVersion() {
        return "1.0.0";
    }

    /**
     * {@inheritDoc}
     * @return 空字符串（未配置图标）
     */
    @Override
    public String vendorIcon() {
        return "";
    }

    /**
     * {@inheritDoc}
     * @return DashScope 配置参数集合（image/audio/tts/rerank 四类合并）
     */
    @Override
    public List<JsonConfigParam> configParam() {
        List<JsonConfigParam> params = new ArrayList<>();
        params.addAll(Arrays.asList(DashScopeImageParam.Config.values()));
        params.addAll(Arrays.asList(DashScopeAudioParam.Config.values()));
        params.addAll(Arrays.asList(DashScopeTtsParam.Config.values()));
        params.addAll(Arrays.asList(DashScopeRerankParam.Config.values()));
        return params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageGenerationClient buildImageClient(AiModelConfigData configData) {
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

        return new ImageGenerationClient(configData, imageModel);
    }

    /**
     * {@inheritDoc}
     * <p>仅作为配置载体，不持有 RealtimeTranscriptionModel 实例。实例创建逻辑通过
     * {@link java.util.function.Supplier} 工厂注入到客户端中，
     * 客户端在 {@link AudioTranscriptionClient#createRealtimeSession()} 时现场创建独立实例。
     */
    @Override
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

    /**
     * {@inheritDoc}
     * <p>从 {@link AiModelConfigData} 读 apiUrl/apiKey/modelName 和 model_data 中的 rerank 默认参数，
     * 构造 {@link DashScopeRerankModel} 注入到 {@link RerankClient}。配置变更后通过
     * {@code invalidateConfig} 触发 CacheChangeNotifyListener → 重建 client。
     */
    @Override
    public RerankClient buildRerankClient(AiModelConfigData configData) {
        JsonConfigBox configParamBox = configData.getConfigParamBox();
        Map<String, Object> params = new HashMap<>();
        if (configParamBox != null) {
            int topN = configParamBox.getIntParam("rerank.top.n", 0);
            if (topN > 0) {
                params.put("top_n", topN);
            }
            String instruct = configParamBox.getParam("rerank.instruct", "");
            if (instruct != null && !instruct.isEmpty()) {
                params.put("instruct", instruct);
            }
            params.put("return_documents", configParamBox.getBooleanParam("rerank.return.documents", true));
        } else {
            // configParamBox 为 null 时使用枚举默认值兜底（returnDocuments=true）
            params.put("return_documents", true);
        }
        DashScopeRerankModel model = new DashScopeRerankModel(
                configData.getApiUrl(),
                configData.getApiKeyRaw(),
                configData.getModelName(),
                params);
        return new RerankClient(configData, model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> listModel(String apiUrl, String apiKey) {
        return List.of();
    }
}
