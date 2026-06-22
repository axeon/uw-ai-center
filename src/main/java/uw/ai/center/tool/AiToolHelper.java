package uw.ai.center.tool;

import dev.langchain4j.agent.tool.ToolSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import uw.ai.center.entity.AiToolInfo;
import uw.ai.center.util.SecurityUtils;
import uw.ai.vo.AiToolCallInfo;
import uw.ai.vo.AiToolExecuteParam;
import uw.cache.CacheDataLoader;
import uw.cache.FusionCache;
import uw.common.app.constant.CommonState;
import uw.common.response.ResponseData;
import uw.common.util.JsonUtils;
import uw.dao.DaoManager;
import uw.common.data.PageList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * AiToolHelper。
 */
@Service
public class AiToolHelper {

    private static final Logger logger = LoggerFactory.getLogger(AiToolHelper.class);

    private static final DaoManager dao = DaoManager.getInstance();
    private static RestClient authRestClient;

    /**
     * 工具回调超时时间（毫秒）。下游微服务超过该时间未响应则视为失败，避免拖垮 AI 主调用链。
     */
    private static final long TOOL_CALLBACK_TIMEOUT_MILLIS = 15_000L;

    static {
        // 此缓存为全量工具缓存：cacheName 固定，CacheDataLoader 的 key 参数实际不参与查询，
        // 每次未命中或失效都会重新加载所有启用状态的工具。失效时整张表重载，工具数量大时会有性能尖刺。
        FusionCache.config(FusionCache.Config.builder()
                .cacheName(AiToolInfo.class.getSimpleName())
                .localCacheMaxNum(3)
                .cacheExpireMillis(86400_000L)
                .nullProtectMillis(10_000L)
                .build(), new CacheDataLoader<String, Map<String, AiToolInfo>>() {
            @Override
            public Map<String, AiToolInfo> load(String toolCode) throws Exception {
                PageList<AiToolInfo> dataList = dao.list(AiToolInfo.class,
                        "select * from ai_tool_info where state=?", new Object[]{CommonState.ENABLED.getValue()}).getData();
                if (dataList == null) {
                    return null;
                }
                return dataList.stream().collect(Collectors.toMap(x -> x.getAppName() + "/" + x.getToolClass(), x -> x, (existingValue, newValue) -> existingValue));
            }
        });
    }

    /**
     * 构造方法，注入用于 RPC 转发工具调用的 RestClient。
     *
     * @param authRestClient 鉴权 RestClient（由 Spring 容器注入，带服务发现与鉴权）
     */
    public AiToolHelper(RestClient authRestClient) {
        AiToolHelper.authRestClient = authRestClient;
    }

    /**
     * 获取 LangChain4j ToolSpecification 列表。
     * <p>按调用方传入的 toolCode（appName/toolClass）从缓存匹配 AiToolInfo，转换为 ToolSpecification。
     * 未匹配到的工具会被静默跳过。
     *
     * @param aiToolCallInfoList 工具调用信息列表
     * @return ToolSpecification 列表（可能为空）
     */
    public static List<ToolSpecification> getToolSpecifications(List<AiToolCallInfo> aiToolCallInfoList) {
        List<ToolSpecification> specs = new ArrayList<>();
        try {
            Map<String, AiToolInfo> map = FusionCache.get(AiToolInfo.class.getSimpleName(), AiToolInfo.class.getSimpleName());
            if (map != null) {
                for (AiToolCallInfo callInfo : aiToolCallInfoList) {
                    AiToolInfo toolInfo = map.get(callInfo.getToolCode());
                    if (toolInfo != null) {
                        specs.add(new AiToolCallback(toolInfo, callInfo.isReturnDirect()).toToolSpecification());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("获取ToolSpecification失败！{}", e.getMessage(), e);
        }
        return specs;
    }

    /**
     * 执行工具（通过 RPC 转发到外部微服务）。
     * <p>按 toolName（appName/toolClass）匹配 AiToolInfo，转发到对应微服务的 /rpc/ai/tool/execute。
     * 结果序列化为 JSON 字符串返回给 AI 模型作为工具执行结果。
     *
     * @param toolName  工具名（appName/toolClass）
     * @param toolInput 工具入参 JSON
     * @return 工具执行结果 JSON 字符串；工具未找到时返回错误信息
     */
    public static String executeTool(String toolName, String toolInput) {
        try {
            Map<String, AiToolInfo> map = FusionCache.get(AiToolInfo.class.getSimpleName(), AiToolInfo.class.getSimpleName());
            if (map != null) {
                AiToolInfo toolInfo = map.get(toolName);
                if (toolInfo != null) {
                    return JsonUtils.toString(toolCallback(toolInfo, toolInput));
                }
            }
        } catch (Exception e) {
            logger.error("执行工具[{}]失败！{}", toolName, e.getMessage(), e);
        }
        return ResponseData.errorMsg("工具[" + toolName + "]未找到").toString();
    }

    /**
     * 执行工具回调：通过 authRestClient 把工具调用转发到外部微服务。
     * <p>appName 经 {@link SecurityUtils#checkServiceName(String)} 校验为合法服务名（防 SSRF），
     * 非法 appName 直接返回错误，不会拼接到 URL。
     *
     * @param aiToolInfo 工具信息（含 appName / toolClass / id）
     * @param toolInput  工具入参 JSON
     * @return 外部微服务返回的响应数据
     */
    public static ResponseData toolCallback(AiToolInfo aiToolInfo, String toolInput) {
        // SSRF 防护：校验 appName 为合法服务名（仅字母/数字/连字符），阻止 appName 被篡改为内网 IP/域名
        if (!SecurityUtils.checkServiceName(aiToolInfo.getAppName())) {
            logger.error("工具回调被拒绝：非法 appName={}, toolId={}", aiToolInfo.getAppName(), aiToolInfo.getId());
            return ResponseData.errorMsg("工具[" + aiToolInfo.getAppName() + "/" + aiToolInfo.getToolClass() + "]配置异常");
        }
        AiToolExecuteParam param = new AiToolExecuteParam();
        param.setToolId(aiToolInfo.getId());
        param.setToolClass(aiToolInfo.getToolClass());
        param.setToolInput(toolInput);
        String url = "http://" + aiToolInfo.getAppName() + "/rpc/ai/tool/execute";
        // 超时保护：异步派发 + Future.get(timeout)，防止下游微服务挂起拖垮 AI 主流程
        CompletableFuture<ResponseData> future = CompletableFuture.supplyAsync(() ->
                authRestClient.post()
                        .uri(url)
                        .body(param)
                        .retrieve()
                        .body(new ParameterizedTypeReference<ResponseData>() {}));
        try {
            return future.get(TOOL_CALLBACK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            logger.error("工具回调超时: appName={}, toolClass={}, timeoutMs={}",
                    aiToolInfo.getAppName(), aiToolInfo.getToolClass(), TOOL_CALLBACK_TIMEOUT_MILLIS);
            return ResponseData.errorMsg("工具[" + aiToolInfo.getAppName() + "/" + aiToolInfo.getToolClass() + "]调用超时");
        } catch (ExecutionException e) {
            logger.error("工具回调失败: appName={}, toolClass={}, error={}",
                    aiToolInfo.getAppName(), aiToolInfo.getToolClass(), e.getMessage(), e.getCause());
            return ResponseData.errorMsg("工具[" + aiToolInfo.getAppName() + "/" + aiToolInfo.getToolClass() + "]调用失败");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("工具回调被中断: appName={}, toolClass={}", aiToolInfo.getAppName(), aiToolInfo.getToolClass());
            return ResponseData.errorMsg("工具[" + aiToolInfo.getAppName() + "/" + aiToolInfo.getToolClass() + "]调用被中断");
        }
    }

    /**
     * 刷新工具缓存（工具配置变更后调用，使下次请求重新加载）。
     *
     * @return true=失效成功
     */
    public static boolean invalidateToolCache() {
        return FusionCache.invalidate(AiToolInfo.class.getSimpleName(), AiToolInfo.class.getSimpleName());
    }
}
