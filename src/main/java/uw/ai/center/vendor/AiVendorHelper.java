package uw.ai.center.vendor;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
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
import uw.common.app.constant.CommonState;
import uw.dao.DaoManager;
import uw.common.data.PageList;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * AI Vendor 帮助类，管理 Vendors、AiModelConfigData 聚合缓存和 AiVendorClientWrapper 实例缓存。
 */
@Component
public class AiVendorHelper {

    private static final Logger logger = LoggerFactory.getLogger(AiVendorHelper.class);

    private static final DaoManager dao = DaoManager.getInstance();

    private static final Map<String, AiVendor> VENDOR_MAP = new ConcurrentHashMap<>();

    /**
     * AiVendorClientWrapper 实例缓存
     */
    private static final LoadingCache<Long, AiVendorClientWrapper> CLIENT_WRAPPER_CACHE = Caffeine.newBuilder()
            .maximumSize(1000)
            .build(AiVendorHelper::buildClientWrapper);

    /**
     * 启用状态的模型配置全量列表缓存名
     */
    private static final String MODEL_CONFIG_CACHE = AiModelConfig.class.getSimpleName() + "List";
    /**
     * 启用状态的API配置全量列表缓存名
     */
    private static final String MODEL_API_CACHE = AiModelApi.class.getSimpleName() + "List";

    static {
        FusionCache.config(FusionCache.Config.builder()
                .entityClass(AiModelConfigData.class)
                .localCacheMaxNum(10000)
                .cacheExpireMillis(86400_000L)
                .nullProtectMillis(86400_000L)
                .build(), new CacheDataLoader<Long, AiModelConfigData>() {
            @Override
            public AiModelConfigData load(Long configId) throws Exception {
                // 只加载启用状态的配置
                AiModelConfig modelConfig = dao.queryForObject(AiModelConfig.class,
                        "select * from ai_model_config where id=? and state=1", new Object[]{configId}).getData();
                if (modelConfig == null) {
                    return null;
                }
                AiModelApi apiConfig = dao.queryForObject(AiModelApi.class,
                        "select * from ai_model_api where id=? and state=1", new Object[]{modelConfig.getApiId()}).getData();
                if (apiConfig == null) {
                    return null;
                }
                return new AiModelConfigData(modelConfig, new AiApiConfigData(apiConfig));
            }
        }, (CacheChangeNotifyListener<Long, AiModelConfigData>) (key, oldValue, newValue) -> {
            // 缓存变更时级联失效 ClientWrapper
            invalidateClientWrapper(key);
        });

        FusionCache.config(FusionCache.Config.builder()
                .cacheName(MODEL_CONFIG_CACHE)
                .localCacheMaxNum(3)
                .cacheExpireMillis(86400_000L)
                .nullProtectMillis(10_000L)
                .build(), new CacheDataLoader<String, Map<Long, AiModelConfig>>() {
            @Override
            public Map<Long, AiModelConfig> load(String key) throws Exception {
                PageList<AiModelConfig> dataList = dao.list(AiModelConfig.class,
                        "select * from ai_model_config where state=?", new Object[]{CommonState.ENABLED.getValue()}).getData();
                if (dataList == null) {
                    return null;
                }
                return dataList.
                        stream().
                        collect(Collectors.
                                toMap(AiModelConfig::getId,
                                        config -> config, (existingValue, newValue) -> existingValue));
            }
        });

        FusionCache.config(FusionCache.Config.builder()
                .cacheName(MODEL_API_CACHE)
                .localCacheMaxNum(3)
                .cacheExpireMillis(86400_000L)
                .nullProtectMillis(10_000L)
                .build(), new CacheDataLoader<String, Map<Long, AiModelApi>>() {
            @Override
            public Map<Long, AiModelApi> load(String key) throws Exception {
                PageList<AiModelApi> dataList = dao.list(AiModelApi.class,
                        "select * from ai_model_api where state=?", new Object[]{CommonState.ENABLED.getValue()}).getData();
                if (dataList == null) {
                    return null;
                }
                return dataList.
                        stream().
                        collect(Collectors.
                                toMap(AiModelApi::getId,
                                        api -> api, (existingValue, newValue) -> existingValue));
            }
        });

        logger.info("AI模块FusionCache缓存配置初始化完成");
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
     * 获取所有AI供应商列表（不可变视图，防止外部修改）。
     */
    public static Map<String, AiVendor> getVendorMap() {
        return Collections.unmodifiableMap(VENDOR_MAP);
    }

    /**
     * 根据供应商类获取AI供应商。
     */
    public static AiVendor getVendor(String vendorClass) {
        return VENDOR_MAP.get(vendorClass);
    }

    /**
     * 获取 AiVendorClientWrapper。
     */
    public static AiVendorClientWrapper getClientWrapper(long configId) {
        AiVendorClientWrapper wrapper = CLIENT_WRAPPER_CACHE.get(configId);
        if (wrapper == null) {
            throw new IllegalStateException("AI模型配置[" + configId + "]不存在或未启用");
        }
        return wrapper;
    }

    /**
     * 获取 AiVendorClientWrapper（向后兼容旧API）。
     */
    public static AiVendorClientWrapper getChatClient(long configId) {
        return getClientWrapper(configId);
    }

    /**
     * 根据配置ID获取AI模型配置数据。
     */
    public static AiModelConfigData getModelConfigData(long configId) {
        return FusionCache.get(AiModelConfigData.class, configId);
    }

    /**
     * 获取启用状态的模型配置全量Map
     */
    public static Map<Long, AiModelConfig> getEnabledModelConfigMap() {
        return FusionCache.get(MODEL_CONFIG_CACHE, MODEL_CONFIG_CACHE);
    }

    /**
     * 获取启用状态的API配置全量Map
     */
    public static Map<Long, AiModelApi> getEnabledModelApiMap() {
        return FusionCache.get(MODEL_API_CACHE, MODEL_API_CACHE);
    }

    /**
     *
     * @param configId   配置ID（<=0 表示未传）
     * @param configCode 配置代码
     * @return 解析出的 configId；若两者都为空或 configCode 未匹配到，返回 null
     */
    public static Long resolveConfigId(long configId, String configCode) {
        if (configId > 0) {
            return configId;
        }
        if (StringUtils.isBlank(configCode)) {
            return null;
        }
        Map<Long, AiModelConfig> map = getEnabledModelConfigMap();
        if (map == null) {
            return null;
        }
        return map.values().stream()
                .filter(c -> configCode.equals(c.getConfigCode()))
                .map(AiModelConfig::getId)
                .findFirst()
                .orElse(null);
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
     * 刷新指定模型配置的缓存。
     * FusionCache 失效时自动触发 CacheChangeNotifyListener → invalidateClientWrapper。
     */
    public static void invalidateConfig(long configId) {
        FusionCache.invalidate(AiModelConfigData.class, configId);
        invalidateModelConfigListCache();
    }

    /**
     * API配置变更时，级联失效关联的模型配置缓存。
     */
    public static void invalidateApiConfig(long apiId) {
        PageList<AiModelConfig> configs = dao.list(AiModelConfig.class,
                "select id from ai_model_config where api_id=?", new Object[]{apiId}).getData();
        if (configs != null) {
            for (AiModelConfig config : configs) {
                invalidateConfig(config.getId());
            }
        }
        invalidateModelApiListCache();
    }

    /**
     * 失效模型配置列表缓存（全量）
     */
    public static void invalidateModelConfigListCache() {
        FusionCache.invalidate(MODEL_CONFIG_CACHE, MODEL_CONFIG_CACHE);
    }

    /**
     * 失效API配置列表缓存（全量）。
     */
    public static void invalidateModelApiListCache() {
        FusionCache.invalidate(MODEL_API_CACHE, MODEL_API_CACHE);
    }

    /**
     * 级联失效ClientWrapper实例缓存。
     * 失效前先调用close()释放底层HTTP连接池等资源。
     * 由CacheChangeNotifyListener自动调用。
     */
    public static void invalidateClientWrapper(Long configId) {
        AiVendorClientWrapper wrapper = CLIENT_WRAPPER_CACHE.getIfPresent(configId);
        if (wrapper != null) {
            wrapper.close();
            CLIENT_WRAPPER_CACHE.invalidate(configId);
        }
    }

    /**
     * 构建 AiVendorClientWrapper。
     * 从FusionCache获取聚合数据，委托给 AiVendor.buildClientWrapper。
     * 配置不存在、Vendor未注册或模型类型不支持时抛出IllegalStateException。
     */
    private static AiVendorClientWrapper buildClientWrapper(long configId) {
        AiModelConfigData configData = FusionCache.get(AiModelConfigData.class, configId);
        if (configData == null) {
            throw new IllegalStateException("AI模型配置[" + configId + "]不存在或未启用");
        }

        logger.info("加载AI模型配置: id={}, configName={}, apiUrl={}, modelName={}, modelType={}, vendorClass={}",
                configData.getId(), configData.getConfigName(),
                configData.getApiUrl(), configData.getModelName(),
                configData.getModelType(), configData.getVendorClass());

        AiVendor vendor = VENDOR_MAP.get(configData.getVendorClass());
        if (vendor == null) {
            throw new IllegalStateException("未找到AI Vendor: " + configData.getVendorClass());
        }

        AiVendorClientWrapper wrapper = vendor.buildClientWrapper(configData);
        if (wrapper == null) {
            throw new IllegalStateException("Vendor[" + vendor.vendorName() + "]不支持模型类型[" + configData.getModelType() + "], configId=" + configId);
        }
        return wrapper;
    }
}
