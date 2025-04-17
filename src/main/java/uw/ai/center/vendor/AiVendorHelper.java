package uw.ai.center.vendor;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.Nullable;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.center.vendor.ollama.OllamaVendor;
import uw.ai.center.vendor.openai.OpenAiVendor;
import uw.ai.center.vo.AiModelConfigData;
import uw.cache.CacheChangeNotifyListener;
import uw.cache.CacheDataLoader;
import uw.cache.FusionCache;
import uw.dao.DaoManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI供应商帮助类。
 */
public class AiVendorHelper {

    /**
     * 数据访问对象。
     */
    private static final DaoManager dao = DaoManager.getInstance();
    /**
     * AI供应商列表。
     */
    private final static Map<String, AiVendor> VENDOR_MAP = new LinkedHashMap<>() {{
        put( OllamaVendor.class.getName(), new OllamaVendor() );
        put( OpenAiVendor.class.getName(), new OpenAiVendor() );
    }};

    /**
     * 本地AisLinker实例缓存。
     */
    private static final LoadingCache<Long, AiVendorClientWrapper> vendorClientCache = Caffeine.newBuilder().maximumSize( 1000 ).build( new CacheLoader<Long,
            AiVendorClientWrapper>() {
        @Override
        public @Nullable AiVendorClientWrapper load(Long configId) {
            AiModelConfigData aiModelConfigData = FusionCache.get( AiModelConfigData.class, configId );
            if (aiModelConfigData == null) {
                return null;
            }
            return buildChatClient( aiModelConfigData );
        }
    } );

    static {
        // AI模型配置数据缓存。
        FusionCache.config( FusionCache.Config.builder().entityClass( AiModelConfigData.class ).localCacheMaxNum( 10000 ).globalCacheExpireMillis( 86400_000L ).nullProtectMillis( 86400_000L ).build(), new CacheDataLoader<Long, AiModelConfigData>() {
            @Override
            public AiModelConfigData load(Long configId) throws Exception {
                AiModelConfig aiModelConfig = dao.load( AiModelConfig.class, configId ).getData();
                if (aiModelConfig == null) {
                    return null;
                }
                return new AiModelConfigData( aiModelConfig );
            }
        }, (CacheChangeNotifyListener<Long, AiModelConfigData>) (key, oldValue, newValue) -> {
            //此处invalidate实例缓存。
            if (vendorClientCache.getIfPresent( key ) != null) {
                vendorClientCache.invalidate( key );
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
     *
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
    public static AiVendorClientWrapper getChatClient(long configId) {
        return vendorClientCache.get( configId );
    }


    /**
     * 根据配置ID获取AI模型配置。
     *
     * @param configId
     * @return
     */
    public static AiModelConfigData getModelConfigData(long configId) {
        return FusionCache.get( AiModelConfigData.class, configId );
    }

    /**
     * 获取模型列表。
     *
     * @param vendorClass
     * @param apiUrl
     * @param apiKey
     * @return
     */
    public static List<String> listModel(String vendorClass, String apiUrl, String apiKey) {
        return getVendor( vendorClass ).listModel( apiUrl, apiKey );
    }

    /**
     * 根据配置ID获取AI供应商。
     *
     * @param aiModelConfigData
     * @return
     */
    private static AiVendorClientWrapper buildChatClient(AiModelConfigData aiModelConfigData) {
        if (aiModelConfigData != null) {
            AiVendor aiVendor = VENDOR_MAP.get( aiModelConfigData.getVendorClass() );
            if (aiVendor != null) {
                return aiVendor.buildClientWrapper( aiModelConfigData );
            }
        }
        return null;
    }

}
