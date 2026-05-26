package uw.ai.center.vendor;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.center.vo.AiModelConfigData;
import uw.ai.center.vendor.ollama.OllamaVendor;
import uw.ai.center.vendor.openai.OpenAiVendor;
import uw.dao.DaoManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI Vendor 帮助类，管理 Vendors 和 AiVendorClientWrapper 缓存。
 */
@Component
public class AiVendorHelper {

    private static final Logger logger = LoggerFactory.getLogger(AiVendorHelper.class);
    private static final DaoManager dao = DaoManager.getInstance();

    /**
     * AI供应商统一映射。
     */
    private static final Map<String, AiVendor> VENDOR_MAP = new LinkedHashMap<>();

    private static final Map<String, OpenAiVendor> OPENAI_VENDOR_MAP = new LinkedHashMap<>();
    private static final Map<String, OllamaVendor> OLLAMA_VENDOR_MAP = new LinkedHashMap<>();

    /**
     * AiVendorClientWrapper 缓存（按配置ID）。
     */
    private static final LoadingCache<Long, AiVendorClientWrapper> clientCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .build(AiVendorHelper::buildClientWrapper);

    /**
     * 注册 OpenAiVendor 实例。
     */
    public static void registerOpenAiVendor(String className, OpenAiVendor vendor) {
        OPENAI_VENDOR_MAP.put(className, vendor);
        VENDOR_MAP.put(className, vendor);
    }

    /**
     * 注册 OllamaVendor 实例。
     */
    public static void registerOllamaVendor(String className, OllamaVendor vendor) {
        OLLAMA_VENDOR_MAP.put(className, vendor);
        VENDOR_MAP.put(className, vendor);
    }

    /**
     * 获取所有AI供应商列表。
     */
    public static Map<String, AiVendor> getVendorMap() {
        return VENDOR_MAP;
    }

    /**
     * 根据供应商类获取AI供应商。
     */
    public static AiVendor getVendor(String vendorClass) {
        return VENDOR_MAP.get(vendorClass);
    }

    /**
     * 获取 AiVendorClientWrapper（LangChain4j客户端封装）。
     */
    public static AiVendorClientWrapper getClientWrapper(long configId) {
        return clientCache.get(configId);
    }

    /**
     * 获取 AiVendorClientWrapper（向后兼容旧API）。
     */
    public static AiVendorClientWrapper getChatClient(long configId) {
        return getClientWrapper(configId);
    }

    /**
     * 获取模型列表。
     */
    public static List<String> listModel(String vendorClass, String apiUrl, String apiKey) {
        AiVendor vendor = getVendor(vendorClass);
        if (vendor != null) {
            return vendor.listModel(apiUrl, apiKey);
        }
        return List.of();
    }

    /**
     * 刷新指定配置的缓存。
     */
    public static void invalidateConfig(long configId) {
        clientCache.invalidate(configId);
    }

    /**
     * 构建 AiVendorClientWrapper。
     */
    private static AiVendorClientWrapper buildClientWrapper(long configId) {
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

        // 尝试 OpenAiVendor
        OpenAiVendor openAiVendor = OPENAI_VENDOR_MAP.get(vendorClass);
        if (openAiVendor != null) {
            return openAiVendor.buildClientWrapper(configData);
        }

        // 尝试 OllamaVendor
        OllamaVendor ollamaVendor = OLLAMA_VENDOR_MAP.get(vendorClass);
        if (ollamaVendor != null) {
            return ollamaVendor.buildClientWrapper(configData);
        }

        logger.error("未找到AI Vendor: {}", vendorClass);
        return null;
    }
}
