package uw.ai.center.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import uw.ai.center.entity.AiModelApi;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.center.vendor.AiVendorHelper;
import uw.cache.CacheChangeNotifyListener;
import uw.cache.CacheDataLoader;
import uw.cache.FusionCache;
import uw.dao.DaoManager;
import uw.dao.DataList;

/**
 * AI模块FusionCache缓存配置。
 * 集中管理CacheDataLoader和CacheChangeNotifyListener。
 */
@Configuration
public class AiCacheConfig {

    private static final Logger logger = LoggerFactory.getLogger(AiCacheConfig.class);
    private static final DaoManager dao = DaoManager.getInstance();

    static {
        // AiModelApi 自动加载缓存（带CacheChangeNotifyListener，API变更时级联失效关联的实例缓存）
        FusionCache.config(FusionCache.Config.builder()
                .entityClass(AiModelApi.class)
                .localCacheMaxNum(200)
                .cacheExpireMillis(300_000L)
                .nullProtectMillis(300_000L)
                .build(), new CacheDataLoader<Long, AiModelApi>() {
            @Override
            public AiModelApi load(Long apiId) throws Exception {
                return dao.load(AiModelApi.class, apiId).getData();
            }
        }, (CacheChangeNotifyListener<Long, AiModelApi>) (key, oldValue, newValue) -> {
            // AiModelApi变更时，级联失效关联的ClientWrapper缓存
            DataList<AiModelConfig> configs = dao.list(AiModelConfig.class,
                    "select id from ai_model_config where api_id=?", new Object[]{key}).getData();
            if (configs != null) {
                for (AiModelConfig config : configs) {
                    AiVendorHelper.invalidateClientWrapper(config.getId());
                }
            }
        });

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
            AiVendorHelper.invalidateClientWrapper(key);
        });

        logger.info("AI模块FusionCache缓存配置初始化完成");
    }
}
