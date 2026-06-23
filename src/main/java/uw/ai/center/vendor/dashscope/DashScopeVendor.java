package uw.ai.center.vendor.dashscope;

import org.springframework.stereotype.Service;
import uw.ai.center.constant.ModelType;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vendor.client.AiModelClient;
import uw.ai.center.vendor.dashscope.imageModel.DashScopeImageParam;
import uw.ai.center.vendor.dashscope.realtimeTranscriptionModel.DashScopeAudioParam;
import uw.ai.center.vendor.dashscope.ttsModel.DashScopeTtsParam;
import uw.ai.center.vo.AiModelConfigData;
import uw.common.app.vo.JsonConfigParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DashScope 协议入口。
 * <p>数据库 {@code ai_model_config.vendor_class} 存的是本类的全限定名，作为"协议标识"稳定不变。
 * <p>本类承担两类职责：
 * <ul>
 *   <li>提供协议元信息（vendorName/vendorDesc/configParam/listModel 等）—— 所有 DashScope 协议下的能力子类共享，避免在每个子类重复</li>
 *   <li>按 {@code modelType} 委托构建到独立的能力子类（{@link DashScopeImageVendor} / {@link DashScopeAudioTranscriptionVendor}），
 *       让 image 与 audio 的实际构建逻辑物理隔离到不同文件</li>
 * </ul>
 */
@Service
public class DashScopeVendor implements AiVendor {

    private final DashScopeImageVendor imageVendor;
    private final DashScopeAudioTranscriptionVendor audioTranscriptionVendor;

    public DashScopeVendor(DashScopeImageVendor imageVendor,
                           DashScopeAudioTranscriptionVendor audioTranscriptionVendor) {
        this.imageVendor = imageVendor;
        this.audioTranscriptionVendor = audioTranscriptionVendor;
    }

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
     * @return DashScope 配置参数集合（image/audio/tts 三类合并）
     */
    @Override
    public List<JsonConfigParam> configParam() {
        List<JsonConfigParam> params = new ArrayList<>();
        params.addAll(Arrays.asList(DashScopeImageParam.Config.values()));
        params.addAll(Arrays.asList(DashScopeAudioParam.Config.values()));
        params.addAll(Arrays.asList(DashScopeTtsParam.Config.values()));
        return params;
    }

    @Override
    public AiModelClient buildClient(AiModelConfigData configData) {
        ModelType modelType = ModelType.of(configData.getModelType());
        return switch (modelType) {
            case IMAGE_GENERATION -> imageVendor.buildImageClient(configData);
            case AUDIO_TRANSCRIPTION -> audioTranscriptionVendor.buildAudioTranscriptionClient(configData);
            default -> throw new IllegalStateException(
                    "DashScopeVendor 不支持模型类型[" + configData.getModelType() + "]");
        };
    }

    @Override
    public List<String> listModel(String apiUrl, String apiKey) {
        return List.of();
    }
}
