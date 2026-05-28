package uw.ai.center.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import uw.ai.center.entity.AiModelApi;
import uw.ai.center.entity.AiModelConfig;
import uw.cache.FusionCache;

/**
 * AI模块FusionCache初始化配置。
 */
@Configuration
public class AiCacheAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(AiCacheAutoConfiguration.class);

    static {
        // AiModelApi 缓存配置
        FusionCache.config(FusionCache.Config.builder()
                .cacheName(AiModelApi.class.getSimpleName())
                .localCacheMaxNum(200)
                .cacheExpireMillis(300_000L)
                .build());
        logger.info("FusionCache[{}] 已初始化", AiModelApi.class.getSimpleName());

        // AiModelConfig 缓存配置
        FusionCache.config(FusionCache.Config.builder()
                .cacheName(AiModelConfig.class.getSimpleName())
                .localCacheMaxNum(200)
                .cacheExpireMillis(300_000L)
                .build());
        logger.info("FusionCache[{}] 已初始化", AiModelConfig.class.getSimpleName());
    }
}