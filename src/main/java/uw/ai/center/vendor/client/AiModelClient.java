package uw.ai.center.vendor.client;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.ai.center.constant.ModelType;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vo.AiModelConfigData;

/**
 * AI 模型客户端基类。
 * <p>按 ModelType 派生具体子类（ChatClient/EmbeddingClient/ImageGenerationClient/AudioTranscriptionClient），
 * 每个子类只持有自己那一类 LangChain4j 模型实例。
 * <p>实现 AutoCloseable，在缓存失效时由 AiVendorHelper.invalidateClient 调用 close()。
 */
@Schema(title = "AI模型客户端基类", description = "AI模型客户端基类")
public abstract class AiModelClient implements AutoCloseable {

    @Schema(title = "配置数据", description = "配置数据")
    protected final AiModelConfigData configData;

    /**
     * 持有构建此实例的 vendor。
     * 仅用于需要按需创建独立有状态实例的子类（如 AudioTranscriptionClient.createRealtimeSession）；
     * ChatClient/EmbeddingClient/ImageGenerationClient 不使用此字段，构造时仍传入以保持类型层次一致性。
     */
    protected final AiVendor vendor;

    /**
     * 本客户端对应的模型类型，由子类构造时传入。
     */
    protected final ModelType modelType;

    protected AiModelClient(AiModelConfigData configData, AiVendor vendor, ModelType modelType) {
        this.configData = configData;
        this.vendor = vendor;
        this.modelType = modelType;
    }

    public AiModelConfigData getConfigData() {
        return configData;
    }

    public ModelType getModelType() {
        return modelType;
    }

    /**
     * 模板方法：委派子类关闭具体资源。
     */
    @Override
    public final void close() {
        doClose();
    }

    /**
     * 子类实现：关闭自己持有的模型实例（HTTP 连接池等）。
     */
    protected abstract void doClose();

    /**
     * 关闭资源工具方法：若资源实现 AutoCloseable 则安静关闭，异常被忽略。
     * 供各子类 doClose 复用。
     */
    protected static void closeQuietly(Object resource) {
        if (resource instanceof AutoCloseable closeable) {
            try {
                closeable.close();
            } catch (Exception ignored) {
                // 关闭失败不影响整体流程
            }
        }
    }
}
