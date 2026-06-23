package uw.ai.center.vendor.dashscope;

import org.springframework.stereotype.Service;
import uw.ai.center.vendor.client.ImageGenerationClient;
import uw.ai.center.vendor.dashscope.imageModel.DashScopeImageModel;
import uw.ai.center.vo.AiModelConfigData;
import uw.common.app.vo.JsonConfigBox;

import java.util.HashMap;
import java.util.Map;

/**
 * DashScope 协议 IMAGE_GENERATION 能力实现。
 * <p>只承担图片生成客户端（通义万相）的构建逻辑；元信息由协议入口 {@link DashScopeVendor} 统一提供。
 * 作为 Spring bean 存在，由协议入口注入并按 model_type 委托调用。
 */
@Service
public class DashScopeImageVendor {

    /**
     * 构建图片生成客户端（通义万相）。
     * <p>从 configParam 读取 image.size / image.style / image.n 参数，封装为 {@link DashScopeImageModel}。
     */
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
}
