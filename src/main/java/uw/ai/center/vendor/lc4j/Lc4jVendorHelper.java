package uw.ai.center.vendor.lc4j;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.center.vo.AiModelConfigData;
import uw.dao.DaoManager;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LangChain4j Vendor 帮助类，管理 Lc4jClientWrapper 缓存。
 */
@Component
public class Lc4jVendorHelper {

    private static final Logger logger = LoggerFactory.getLogger(Lc4jVendorHelper.class);
    private static final DaoManager dao = DaoManager.getInstance();

    private static final Map<String, OpenAiLc4jVendor> OPENAI_VENDOR_MAP = new LinkedHashMap<>();
    private static final Map<String, OllamaLc4jVendor> OLLAMA_VENDOR_MAP = new LinkedHashMap<>();

    /**
     * Lc4jClientWrapper 缓存（按配置ID）。
     */
    private static final LoadingCache<Long, Lc4jClientWrapper> lc4jClientCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .build(new CacheLoader<Long, Lc4jClientWrapper>() {
                @Override
                public Lc4jClientWrapper load(Long configId) {
                    return buildLc4jClientWrapper(configId);
                }
            });

    /**
     * 注册 OpenAiLc4jVendor 实例。
     */
    public static void registerOpenAiVendor(String className, OpenAiLc4jVendor vendor) {
        OPENAI_VENDOR_MAP.put(className, vendor);
    }

    /**
     * 注册 OllamaLc4jVendor 实例。
     */
    public static void registerOllamaVendor(String className, OllamaLc4jVendor vendor) {
        OLLAMA_VENDOR_MAP.put(className, vendor);
    }

    /**
     * 获取 Lc4jClientWrapper。
     */
    public static Lc4jClientWrapper getLc4jClientWrapper(long configId) {
        return lc4jClientCache.get(configId);
    }

    /**
     * 刷新指定配置的缓存。
     */
    public static void invalidateCache(long configId) {
        lc4jClientCache.invalidate(configId);
    }

    /**
     * 构建 Lc4jClientWrapper。
     */
    private static Lc4jClientWrapper buildLc4jClientWrapper(long configId) {
        AiModelConfig config = dao.load(AiModelConfig.class, configId).getData();
        if (config == null) {
            logger.error("AI模型配置[{}]不存在", configId);
            return null;
        }
        AiModelConfigData configData = new AiModelConfigData(config);
        if (configData == null) {
            logger.error("AI模型配置[{}]解析失败", configId);
            return null;
        }
        String vendorClass = configData.getVendorClass();
        if (vendorClass == null) {
            logger.error("AI模型配置[{}]未指定vendorClass", configId);
            return null;
        }

        // 尝试 OpenAiLc4jVendor
        OpenAiLc4jVendor openAiVendor = OPENAI_VENDOR_MAP.get(vendorClass);
        if (openAiVendor != null) {
            return openAiVendor.buildLc4jClientWrapper(configData);
        }

        // 尝试 OllamaLc4jVendor
        OllamaLc4jVendor ollamaVendor = OLLAMA_VENDOR_MAP.get(vendorClass);
        if (ollamaVendor != null) {
            return ollamaVendor.buildLc4jClientWrapper(configData);
        }

        logger.error("未找到LangChain4j Vendor: {}", vendorClass);
        return null;
    }
}
