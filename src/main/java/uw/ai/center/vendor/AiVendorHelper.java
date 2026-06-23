package uw.ai.center.vendor;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uw.ai.center.constant.ModelType;
import uw.ai.center.entity.AiModelApi;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.center.entity.AiRagLib;
import uw.ai.center.vo.AiApiConfigData;
import uw.ai.center.vo.AiModelConfigData;
import uw.ai.center.vendor.client.AiModelClient;
import uw.ai.center.vendor.client.AudioTranscriptionClient;
import uw.ai.center.vendor.client.ChatClient;
import uw.ai.center.vendor.client.EmbeddingClient;
import uw.ai.center.vendor.client.ImageGenerationClient;
import uw.ai.center.vendor.capability.AudioTranscriptionVendor;
import uw.ai.center.vendor.capability.ChatVendor;
import uw.ai.center.vendor.capability.EmbeddingVendor;
import uw.ai.center.vendor.capability.ImageGenerationVendor;
import uw.cache.CacheChangeNotifyListener;
import uw.cache.CacheDataLoader;
import uw.cache.FusionCache;
import uw.dao.DaoManager;
import uw.common.data.PageList;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI Vendor 帮助类，管理 Vendors、AiModelConfigData 聚合缓存和 AiModelClient 实例缓存。
 */
@Component
public class AiVendorHelper {

    private static final Logger logger = LoggerFactory.getLogger(AiVendorHelper.class);

    private static final DaoManager dao = DaoManager.getInstance();

    private static final Map<String, AiVendor> VENDOR_MAP = new ConcurrentHashMap<>();

    /**
     * AiModelClient 实例缓存
     */
    private static final LoadingCache<Long, AiModelClient> CLIENT_CACHE = Caffeine.newBuilder()
            .maximumSize(1000)
            .build(AiVendorHelper::buildClient);

    /**
     * FusionCache cacheName：configCode → configId 映射（高频 RPC 查询场景）。
     */
    private static final String CACHE_CONFIG_CODE_TO_ID = "AiConfigCodeToIdMapping";

    /**
     * FusionCache cacheName：apiCode → apiId 映射（高频 RPC 查询场景）。
     */
    private static final String CACHE_API_CODE_TO_ID = "AiApiCodeToIdMapping";

    static {
        FusionCache.config(FusionCache.Config.builder()
                .entityClass(AiModelConfigData.class)
                .localCacheMaxNum(10000)
                .cacheExpireMillis(86400_000L)
                .nullProtectMillis(60_000L)
                .build(), new CacheDataLoader<Long, AiModelConfigData>() {
            @Override
            public AiModelConfigData load(Long configId) throws Exception {
                // 只加载启用状态的配置
                AiModelConfig modelConfig = dao.queryForObject(AiModelConfig.class,
                        "select * from ai_model_config where id=? and state=1", new Object[]{configId}).getData();
                if (modelConfig == null) {
                    return null;
                }
                // 复用 AiModelApi.class 缓存，避免重复查 DB
                AiModelApi apiConfig = FusionCache.get(AiModelApi.class, modelConfig.getApiId());
                if (apiConfig == null) {
                    return null;
                }
                return new AiModelConfigData(modelConfig, new AiApiConfigData(apiConfig));
            }
        }, (CacheChangeNotifyListener<Long, AiModelConfigData>) (key, oldValue, newValue) -> {
            // 缓存变更时级联失效 Client
            invalidateClient(key);
        });

        FusionCache.config(FusionCache.Config.builder()
                .entityClass(AiModelApi.class)
                .localCacheMaxNum(10000)
                .cacheExpireMillis(86400_000L)
                .nullProtectMillis(60_000L)
                .build(), new CacheDataLoader<Long, AiModelApi>() {
            @Override
            public AiModelApi load(Long apiId) throws Exception {
                return dao.queryForObject(AiModelApi.class,
                        "select * from ai_model_api where id=? and state=1", new Object[]{apiId}).getData();
            }
        });

        // configCode → configId 映射缓存（高频 RPC 查询场景，失效时由 invalidateConfig 双清）
        FusionCache.config(FusionCache.Config.builder()
                .cacheName(CACHE_CONFIG_CODE_TO_ID)
                .localCacheMaxNum(1000)
                .cacheExpireMillis(86400_000L)
                .nullProtectMillis(60_000L)
                .build(), new CacheDataLoader<String, Long>() {
            @Override
            public Long load(String configCode) throws Exception {
                return dao.queryForObject(Long.class,
                        "select id from ai_model_config where config_code=? and state=1 limit 1",
                        new Object[]{configCode}).getData();
            }
        });

        // apiCode → apiId 映射缓存（高频 RPC 查询场景，失效时由 invalidateApiConfig 双清）
        FusionCache.config(FusionCache.Config.builder()
                .cacheName(CACHE_API_CODE_TO_ID)
                .localCacheMaxNum(1000)
                .cacheExpireMillis(86400_000L)
                .nullProtectMillis(60_000L)
                .build(), new CacheDataLoader<String, Long>() {
            @Override
            public Long load(String apiCode) throws Exception {
                return dao.queryForObject(Long.class,
                        "select id from ai_model_api where api_code=? and state=1 limit 1",
                        new Object[]{apiCode}).getData();
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
     * 获取 CHAT 类型客户端。类型不符时抛 IllegalStateException。
     */
    public static ChatClient getChatClient(long configId) {
        return asType(getClient(configId), ModelType.CHAT, ChatClient.class, configId);
    }

    /**
     * 获取 EMBEDDING 类型客户端。类型不符时抛 IllegalStateException。
     */
    public static EmbeddingClient getEmbeddingClient(long configId) {
        return asType(getClient(configId), ModelType.EMBEDDING, EmbeddingClient.class, configId);
    }

    /**
     * 获取 IMAGE_GENERATION 类型客户端。类型不符时抛 IllegalStateException。
     */
    public static ImageGenerationClient getImageClient(long configId) {
        return asType(getClient(configId), ModelType.IMAGE_GENERATION, ImageGenerationClient.class, configId);
    }

    /**
     * 获取 AUDIO_TRANSCRIPTION 类型客户端。类型不符时抛 IllegalStateException。
     */
    public static AudioTranscriptionClient getAudioTranscriptionClient(long configId) {
        return asType(getClient(configId), ModelType.AUDIO_TRANSCRIPTION, AudioTranscriptionClient.class, configId);
    }

    /**
     * 根据配置ID获取AI模型配置数据。
     */
    public static AiModelConfigData getModelConfigData(long configId) {
        return FusionCache.get(AiModelConfigData.class, configId);
    }

    /**
     * 根据API配置ID获取AI API配置数据（启用状态）。
     */
    public static AiModelApi getApiConfig(long apiId) {
        return FusionCache.get(AiModelApi.class, apiId);
    }

    /**
     * 解析 configId：优先使用入参 configId，否则按 configCode 走 FusionCache 映射（命中 uk_config_code 索引）。
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
        return FusionCache.get(CACHE_CONFIG_CODE_TO_ID, configCode);
    }

    /**
     * 解析 apiId：优先使用入参 apiId，否则按 apiCode 走 FusionCache 映射（命中 uk_api_code 索引）。
     *
     * @param apiId   API配置ID（<=0 表示未传）
     * @param apiCode API配置代码
     * @return 解析出的 apiId；若两者都为空或 apiCode 未匹配到，返回 null
     */
    public static Long resolveApiId(long apiId, String apiCode) {
        if (apiId > 0) {
            return apiId;
        }
        if (StringUtils.isBlank(apiCode)) {
            return null;
        }
        return FusionCache.get(CACHE_API_CODE_TO_ID, apiCode);
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
     * FusionCache 失效时自动触发 CacheChangeNotifyListener → invalidateClient。
     * <p>configCode 双清使用调用方传入的 {@code previousConfigCode}（推荐为 update 前读到的旧值），
     * 避免"失效前再读缓存"导致的 TOCTOU：失效期间若缓存已被其他线程重建为新 code，
     * 再读到的 code 就不是需要失效的那一份。
     * <p>此外，级联失效所有以该 configId 作为 embedConfigId 的 RAG 库客户端缓存，
     * 避免 RAG 侧 EmbeddingModel 引用脱节（换 Key/换维度后产生脏向量）。
     * <p><b>失效顺序</b>：先级联失效 RAG 客户端缓存（停止新请求拿到旧 wrapper），
     * 再失效主缓存触发 client.close()，缩小"已拿到旧 wrapper 的线程继续使用已关闭 EmbeddingModel"
     * 的竞态窗口。极端并发场景下仍可能出现连接已关闭异常，调用方 RAG 查询/入库需具备重试或
     * 失败回退能力。
     *
     * @param configId           要失效的模型配置ID
     * @param previousConfigCode 调用方在 update 前读到的旧 configCode（可为 null，将退化为读缓存）
     */
    public static void invalidateConfig(long configId, String previousConfigCode) {
        String oldCode = previousConfigCode;
        if (oldCode == null) {
            // 调用方未传旧 code：退化为失效前从主缓存拿
            AiModelConfigData data = FusionCache.get(AiModelConfigData.class, configId);
            if (data != null && data.getAiModelConfig() != null) {
                oldCode = data.getAiModelConfig().getConfigCode();
            }
        }
        // 1. 先级联失效引用该 configId 的 RAG 库客户端缓存，避免新请求拿到旧 wrapper
        cascadeInvalidateRagClients(configId);
        // 2. 再失效主缓存，触发 CacheChangeNotifyListener → invalidateClient（close 旧 client）
        FusionCache.invalidate(AiModelConfigData.class, configId);
        if (StringUtils.isNotBlank(oldCode)) {
            FusionCache.invalidate(CACHE_CONFIG_CODE_TO_ID, oldCode);
        }
    }

    /**
     * 刷新指定模型配置的缓存（向后兼容）。
     * 未传 previousConfigCode 时退化为失效前读缓存，存在 TOCTOU 风险，建议调用方传旧 code 版本。
     */
    public static void invalidateConfig(long configId) {
        invalidateConfig(configId, null);
    }

    /**
     * 查询以指定 configId 作为 embed_config_id 的所有 RAG 库，逐个失效 RAG 客户端缓存。
     * 查询失败仅记录日志，不阻断主缓存失效流程。
     *
     * @param configId 模型配置ID（可能是 chat/embedding 等任意类型，仅 EMBEDDING 关联的库会命中）
     */
    private static void cascadeInvalidateRagClients(long configId) {
        try {
            PageList<AiRagLib> ragLibs = dao.list(AiRagLib.class,
                    "select id from ai_rag_lib where embed_config_id=? and state<>?",
                    new Object[]{configId, uw.common.app.constant.CommonState.DELETED.getValue()}).getData();
            if (ragLibs == null || ragLibs.isEmpty()) {
                return;
            }
            for (AiRagLib lib : ragLibs) {
                try {
                    uw.ai.center.service.AiRagService.invalidateRagClientCache(lib.getId());
                } catch (Exception e) {
                    logger.warn("级联失效RAG客户端缓存失败, libId={}, configId={}", lib.getId(), configId, e);
                }
            }
        } catch (Exception e) {
            logger.warn("查询RAG库关联失败，跳过级联失效, configId={}", configId, e);
        }
    }

    /**
     * API配置变更时，级联失效关联的模型配置缓存。
     * <p>apiCode 双清使用调用方传入的 {@code previousApiCode}（推荐为 update 前读到的旧值），
     * 避免"失效前再读缓存"导致的 TOCTOU。
     * <p><b>失效顺序</b>：先失效 {@code AiModelApi}，再级联失效关联的 {@code AiModelConfigData}。
     * 反序会导致 CacheDataLoader 重新加载 ModelConfig 时复用尚未刷新的 AiModelApi 缓存，
     * 把旧 apiKey 重新打包进 AiModelConfigData，直到下一次主缓存过期才能自愈。
     *
     * @param apiId           要失效的 API 配置ID
     * @param previousApiCode 调用方在 update 前读到的旧 apiCode（可为 null，将退化为读缓存）
     */
    public static void invalidateApiConfig(long apiId, String previousApiCode) {
        String oldCode = previousApiCode;
        if (oldCode == null) {
            AiModelApi api = FusionCache.get(AiModelApi.class, apiId);
            if (api != null) {
                oldCode = api.getApiCode();
            }
        }
        // 1. 先失效 AiModelApi + apiCode 映射，确保后续 CacheDataLoader 重载 ModelConfig 时拿到新的 apiKey
        FusionCache.invalidate(AiModelApi.class, apiId);
        if (StringUtils.isNotBlank(oldCode)) {
            FusionCache.invalidate(CACHE_API_CODE_TO_ID, oldCode);
        }
        // 2. 再级联失效引用该 apiId 的 ModelConfig，CacheDataLoader 会复用上一步已刷新的 AiModelApi 缓存
        PageList<AiModelConfig> configs = dao.list(AiModelConfig.class,
                "select id from ai_model_config where api_id=? and state<>?",
                new Object[]{apiId, uw.common.app.constant.CommonState.DELETED.getValue()}).getData();
        if (configs != null) {
            for (AiModelConfig config : configs) {
                invalidateConfig(config.getId());
            }
        }
    }

    /**
     * API配置变更时失效关联缓存（向后兼容）。未传 previousApiCode 时退化为读缓存，存在 TOCTOU 风险。
     */
    public static void invalidateApiConfig(long apiId) {
        invalidateApiConfig(apiId, null);
    }

    /**
     * 级联失效 Client 实例缓存。
     * 使用 asMap().remove() 原子移除，避免 getIfPresent → close → invalidate 期间其他线程重新加载新 client 而被误删。
     * 由 CacheChangeNotifyListener 自动调用。
     */
    public static void invalidateClient(Long configId) {
        AiModelClient client = CLIENT_CACHE.asMap().remove(configId);
        if (client != null) {
            client.close();
        }
    }

    /**
     * 内部统一获取 Client 实例。配置不存在或未启用时抛 IllegalStateException。
     */
    private static AiModelClient getClient(long configId) {
        AiModelClient client = CLIENT_CACHE.get(configId);
        if (client == null) {
            throw new IllegalStateException("AI模型配置[" + configId + "]不存在或未启用");
        }
        return client;
    }

    /**
     * 类型校验 + 强转，统一错误提示。
     */
    private static <T extends AiModelClient> T asType(AiModelClient client, ModelType expected,
                                                     Class<T> clazz, long configId) {
        if (!clazz.isInstance(client)) {
            throw new IllegalStateException("configId[" + configId + "]不是" + expected.getDesc()
                    + "类型，实际类型=" + client.getModelType());
        }
        return clazz.cast(client);
    }

    /**
     * 构建 AiModelClient。
     * <p>唯一的 ModelType 分发点：按 {@link ModelType} 找到对应的能力接口，
     * 校验 vendor 是否 implements 该能力接口，cast 调用对应的 buildXxxClient 方法。
     * <p>配置不存在、Vendor 未注册、或不支持该能力时抛出 IllegalStateException。
     * <p>新增能力只需新增一个 case + 一个能力接口，老 vendor 与老调用方零改动。
     */
    private static AiModelClient buildClient(long configId) {
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

        ModelType modelType = ModelType.of(configData.getModelType());
        if (modelType == null) {
            throw new IllegalStateException("未支持的模型类型[" + configData.getModelType()
                    + "], configId=" + configId);
        }

        return switch (modelType) {
            case CHAT -> {
                if (!(vendor instanceof ChatVendor cv)) {
                    throw unsupported(vendor, modelType, configId);
                }
                yield cv.buildChatClient(configData);
            }
            case EMBEDDING -> {
                if (!(vendor instanceof EmbeddingVendor ev)) {
                    throw unsupported(vendor, modelType, configId);
                }
                yield ev.buildEmbeddingClient(configData);
            }
            case IMAGE_GENERATION -> {
                if (!(vendor instanceof ImageGenerationVendor iv)) {
                    throw unsupported(vendor, modelType, configId);
                }
                yield iv.buildImageClient(configData);
            }
            case AUDIO_TRANSCRIPTION -> {
                if (!(vendor instanceof AudioTranscriptionVendor av)) {
                    throw unsupported(vendor, modelType, configId);
                }
                yield av.buildAudioTranscriptionClient(configData);
            }
            default -> throw new IllegalStateException("模型类型[" + modelType + "]暂未接入能力接口, configId=" + configId);
        };
    }

    /**
     * 构造"vendor 不支持某能力"的统一异常。
     */
    private static IllegalStateException unsupported(AiVendor vendor, ModelType modelType, long configId) {
        return new IllegalStateException("Vendor[" + vendor.vendorName() + "]不支持模型类型["
                + modelType.getDesc() + "], configId=" + configId);
    }
}
