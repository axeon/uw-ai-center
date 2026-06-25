package uw.ai.center.vendor.client;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.output.Response;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.ai.center.constant.ModelType;
import uw.ai.center.vendor.dashscope.image.DashScopeImageModel;
import uw.ai.center.vo.AiModelConfigData;

import java.util.List;

/**
 * IMAGE_GENERATION 类型客户端。
 * <p>提供统一的 {@link #generateImages(String)} 入口，把"是否多图"的 vendor 差异封装在 Client 层，
 * 调用方（AiImageService）无需关心具体 ImageModel 实现。
 */
@Schema(title = "图片生成客户端", description = "图片生成客户端")
public class ImageGenerationClient extends AiModelClient {

    @Schema(title = "图片生成模型", description = "图片生成模型")
    private final ImageModel imageModel;

    public ImageGenerationClient(AiModelConfigData configData, ImageModel imageModel) {
        super(configData, ModelType.IMAGE_GENERATION);
        this.imageModel = imageModel;
    }

    public ImageModel getImageModel() {
        return imageModel;
    }

    /**
     * 统一的多图生成入口。
     * <p>LangChain4j 的 {@link ImageModel} 接口本身只支持单图（{@code generate(prompt)} 返回
     * {@code Response<Image>}），但部分 vendor（如 DashScope 通义万相）的原生 API 支持一次返回多图。
     * <p>当前策略：
     * <ul>
     *   <li>DashScopeImageModel：走原生多图 API</li>
     *   <li>其他实现：回退到单图 generate，包装为 List</li>
     * </ul>
     *
     * @param prompt 图片提示词
     * @return 图片 URL 列表，可能为空
     */
    public List<String> generateImages(String prompt) {
        if (imageModel instanceof DashScopeImageModel dashScopeImageModel) {
            return dashScopeImageModel.generateMultiple(prompt);
        }
        Response<Image> response = imageModel.generate(prompt);
        if (response == null || response.content() == null) {
            return List.of();
        }
        if (response.content().url() == null) {
            return List.of();
        }
        String urlStr = response.content().url().toString();
        return urlStr.isEmpty() ? List.of() : List.of(urlStr);
    }

    @Override
    protected void doClose() {
        closeQuietly(imageModel);
    }
}
