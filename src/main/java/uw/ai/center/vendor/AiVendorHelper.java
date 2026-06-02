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
import uw.cache.CacheChangeNotifyListener;
import uw.cache.CacheDataLoader;
import uw.cache.FusionCache;
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

    private static final Map<String, AiVendor> VENDOR_MAP = new LinkedHashMap<>();

    /**
     * AiVendorClientWrapper 实例缓存（Caffeine本地缓存）。
     */
    private static final LoadingCache<Long, AiVendorClientWrapper> CLIENT_WRAPPER_CACHE = Caffeine.newBuilder()
            .maximumSize(1000)
            .build(AiVendorHelper::buildClientWrapper);

    static {
        // AiModelConfig 自动加载缓存（带CacheChangeNotifyListener，配置变更时级联失效实例缓存）
        FusionCache.config(FusionCache.Config.builder()
                .entityClass(AiModelConfig.class)
                .localCacheMaxNum(10000)
                .cacheExpireMillis(86400_000L)
                .nullProtectMillis(86400_000L)
                .build(), new CacheDataLoader<Long, AiModelConfig>() {
            @Override
            public AiModelConfig load(Long configId) throws Exception {
                return dao.load(AiModelConfig.class, configId).getData();
            }
        }, (CacheChangeNotifyListener<Long, AiModelConfig>) (key, oldValue, newValue) -> {
            if (CLIENT_WRAPPER_CACHE.getIfPresent(key) != null) {
                CLIENT_WRAPPER_CACHE.invalidate(key);
            }
        });
    }

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
        return CLIENT_WRAPPER_CACHE.get(configId);
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
        FusionCache.invalidate(AiModelConfig.class, configId);
    }

    /**
     * 构建 AiVendorClientWrapper。
     * 从FusionCache自动加载模型配置，查询API配置，委托给 AiVendor.buildClientWrapper。
     */
    private static AiVendorClientWrapper buildClientWrapper(long configId) {
        AiModelConfig modelConfig = FusionCache.get(AiModelConfig.class, configId);
        if (modelConfig == null) {
            logger.error("AI模型配置[{}]不存在", configId);
            return null;
        }

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
