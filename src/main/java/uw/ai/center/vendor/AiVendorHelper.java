package uw.ai.center.vendor;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uw.ai.center.entity.AiModelApi;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.center.vo.AiApiConfigData;
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

    /**
     * AiVendorClientWrapper 缓存（按配置ID）。
     */
    private static final LoadingCache<Long, AiVendorClientWrapper> clientCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .build(AiVendorHelper::buildClientWrapper);

    /**
     * 注册 AiVendor 实例。
     */
    public static void registerVendor(String className, AiVendor vendor) {
        VENDOR_MAP.put(className, vendor);
    }

    /**
     * 注册 OpenAiVendor 实例（向后兼容）。
     */
    public static void registerOpenAiVendor(String className, OpenAiVendor vendor) {
        VENDOR_MAP.put(className, vendor);
    }

    /**
     * 注册 OllamaVendor 实例（向后兼容）。
     */
    public static void registerOllamaVendor(String className, OllamaVendor vendor) {
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
     * 两步查询：modelConfig → apiConfig，委托给 AiVendor.buildClientWrapper。
     */
    private static AiVendorClientWrapper buildClientWrapper(long configId) {
        // Step 1: 查模型配置
        AiModelConfig modelConfig = dao.queryForSingleObject(AiModelConfig.class,
                "select * from ai_model_config where id=?", new Object[]{configId}).getData();
        if (modelConfig == null) {
            logger.error("AI模型配置[{}]不存在", configId);
            return null;
        }

        // Step 2: 查 API 配置
        AiModelApi apiConfig = dao.queryForSingleObject(AiModelApi.class,
                "select * from ai_model_api where id=?", new Object[]{modelConfig.getApiId()}).getData();
        if (apiConfig == null) {
            logger.error("AI模型配置[{}]关联的API配置[{}]不存在", configId, modelConfig.getApiId());
            return null;
        }

        AiApiConfigData apiConfigData = new AiApiConfigData(apiConfig);
        AiModelConfigData configData = new AiModelConfigData(modelConfig, apiConfigData);

        logger.info("加载AI模型配置: id={}, configName={}, apiUrl={}, modelName={}, modelType={}, vendorClass={}",
                modelConfig.getId(), modelConfig.getConfigName(),
                apiConfig.getApiUrl(), modelConfig.getModelName(),
                modelConfig.getModelType(), modelConfig.getVendorClass());

        // Step 3: 委托给 Vendor 构建
        AiVendor vendor = VENDOR_MAP.get(modelConfig.getVendorClass());
        if (vendor == null) {
            logger.error("未找到AI Vendor: {}", modelConfig.getVendorClass());
            return null;
        }

        AiVendorClientWrapper wrapper = vendor.buildClientWrapper(configData);
        if (wrapper == null) {
            logger.error("Vendor[{}]不支持模型类型[{}], configId={}",
                    vendor.vendorName(), modelConfig.getModelType(), configId);
        }
        return wrapper;
    }
}
