package uw.ai.center.vendor.dashscope;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.output.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * DashScope 图片生成模型实现（通义万相）。
 * 实现 LangChain4j 的 ImageModel 接口，通过 DashScope 原生 HTTP API 生成图片。
 */
public class DashScopeImageModel implements ImageModel {

    private static final Logger logger = LoggerFactory.getLogger(DashScopeImageModel.class);

    private final String baseUrl;
    private final String apiKey;
    private final String modelName;
    private final Map<String, Object> defaultParams;

    public DashScopeImageModel(String baseUrl, String apiKey, String modelName, Map<String, Object> defaultParams) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.defaultParams = defaultParams != null ? defaultParams : new HashMap<>();
    }

    @Override
    public Response<Image> generate(String prompt) {
        logger.info("DashScope图片生成: model={}, prompt={}", modelName, prompt);
        String imageUrl = DashScopeApiClient.generateImage(baseUrl, apiKey, modelName, prompt, defaultParams);
        Image image = Image.builder()
                .url(URI.create(imageUrl))
                .build();
        return Response.from(image);
    }
}
