package uw.ai.center.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.ai.chat.model.ChatModel;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.center.service.deepseek.DeepSeekVendor;
import uw.ai.center.service.ollama.OllamaVendor;
import uw.ai.center.service.openai.OpenAiVendor;
import uw.ai.center.vo.AiModelConfigData;
import uw.cache.CacheChangeNotifyListener;
import uw.cache.CacheDataLoader;
import uw.cache.FusionCache;
import uw.dao.DaoFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AI帮助类。
 */
public class AiVendorHelper {

    /**
     * 数据访问对象。
     */
    private static final DaoFactory dao = DaoFactory.getInstance();

    /**
     * 本地AisLinker实例缓存。
     */
    private static final LoadingCache<Long, ChatModel> modelInstanceCache = Caffeine.newBuilder().maximumSize( 1000 ).build( new CacheLoader<Long, ChatModel>() {
        @Override
        public @Nullable ChatModel load(Long configId) {
            AiModelConfigData aiModelConfigData = FusionCache.get( AiModelConfigData.class, configId );
            if (aiModelConfigData == null) {
                return null;
            }
            return buildModel( aiModelConfigData );
        }
    } );
    /**
     * AI供应商列表。
     */
    private final static Map<String, AiVendor> VENDOR_MAP = new LinkedHashMap<>() {{
        put( OllamaVendor.class.getName(), new OllamaVendor() );
        put( DeepSeekVendor.class.getName(), new DeepSeekVendor() );
        put( OpenAiVendor.class.getName(), new OpenAiVendor() );
    }};

    static {

        FusionCache.config( FusionCache.Config.builder().entityClass( AiModelConfigData.class ).localCacheMaxNum( 10000 ).globalCacheExpireMillis( 86400_000L ).nullProtectMillis( 86400_000L ).build(), new CacheDataLoader<Long, AiModelConfigData>() {
            @Override
            public AiModelConfigData load(Long configId) throws Exception {
                AiModelConfig aiModelConfig = dao.load( AiModelConfig.class, configId );
                return new AiModelConfigData( aiModelConfig );
            }
        }, (CacheChangeNotifyListener<Long, AiModelConfigData>) (key, oldValue, newValue) -> {
            //此处invalidate实例缓存。
            if (modelInstanceCache.getIfPresent( key ) != null) {
                modelInstanceCache.invalidate( key );
            }
        } );
    }

    /**
     * 获取所有AI供应商列表。
     *
     * @return
     */
    public static Map<String, AiVendor> getVendorMap() {
        return VENDOR_MAP;
    }

    /**
     * 根据供应商类获取AI供应商。
     * @param vendorClass
     * @return
     */
    public static AiVendor getVendor(String vendorClass) {
        return VENDOR_MAP.get( vendorClass );
    }

    /**
     * 根据配置ID获取AI供应商。
     *
     * @param configId
     * @return
     */
    public static ChatModel buildModel(long configId) {
        return null;
    }

    /**
     * 根据配置ID获取AI供应商。
     *
     * @param aiModelConfigData
     * @return
     */
    public static ChatModel buildModel(AiModelConfigData aiModelConfigData) {
        return null;
    }

}
