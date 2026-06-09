package uw.ai.center.vendor.dashscope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uw.ai.center.constant.ModelType;
import uw.ai.center.vo.AiModelConfigData;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vendor.AiVendorClientWrapper;
import uw.common.app.vo.JsonConfigBox;
import uw.common.app.vo.JsonConfigParam;

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
        return Arrays.asList(DashScopeParam.Config.values());
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
            case AUDIO_TRANSCRIPTION -> {
                logger.warn("DashScopeVendor暂不支持模型类型: {}，将在后续功能点中实现", modelType);
                yield null;
            }
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
    private AiVendorClientWrapper buildImageGeneration(AiModelConfigData configData) {
        JsonConfigBox configParamBox = configData.getConfigParamBox();
        Map<String, Object> params = new HashMap<>();
        if (configParamBox != null) {
            String size = configParamBox.getParam("image.size", null);
            String style = configParamBox.getParam("image.style", null);
            Integer n = configParamBox.getIntParam("image.n", 0);
            if (size != null) params.put("size", size);
            if (style != null) params.put("style", style);
            if (n > 0) params.put("n", n);
        }

        var imageModel = new DashScopeImageModel(
                configData.getApiUrl(),
                configData.getApiKeyRaw(),
                configData.getModelName(),
                params
        );

        return new AiVendorClientWrapper(configData, null, null, null, imageModel, null, null);
    }

    @Override
    public List<String> listModel(String apiUrl, String apiKey) {
        return List.of();
    }
}
