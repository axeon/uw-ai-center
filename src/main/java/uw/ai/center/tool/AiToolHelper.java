package uw.ai.center.tool;

import dev.langchain4j.agent.tool.ToolSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uw.ai.center.entity.AiToolInfo;
import uw.ai.vo.AiToolCallInfo;
import uw.ai.vo.AiToolExecuteParam;
import uw.cache.CacheDataLoader;
import uw.cache.FusionCache;
import uw.common.app.constant.CommonState;
import uw.common.dto.ResponseData;
import uw.common.util.JsonUtils;
import uw.dao.DaoManager;
import uw.dao.DataList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AiToolHelper。
 */
@Service
public class AiToolHelper {

    private static final Logger logger = LoggerFactory.getLogger(AiToolHelper.class);

    private static final DaoManager dao = DaoManager.getInstance();
    private static RestTemplate authRestTemplate;

    static {
        FusionCache.config(FusionCache.Config.builder()
                .cacheName(AiToolInfo.class.getSimpleName())
                .localCacheMaxNum(3)
                .cacheExpireMillis(86400_000L)
                .nullProtectMillis(10_000L)
                .build(), new CacheDataLoader<String, Map<String, AiToolInfo>>() {
            @Override
            public Map<String, AiToolInfo> load(String toolCode) throws Exception {
                DataList<AiToolInfo> dataList = dao.list(AiToolInfo.class,
                        "select * from ai_tool_info where state=?", new Object[]{CommonState.ENABLED.getValue()}).getData();
                if (dataList == null) {
                    return null;
                }
                return dataList.stream().collect(Collectors.toMap(x -> x.getAppName() + "/" + x.getToolClass(), x -> x, (existingValue, newValue) -> existingValue));
            }
        });
    }

    public AiToolHelper(RestTemplate authRestTemplate) {
        AiToolHelper.authRestTemplate = authRestTemplate;
    }

    /**
     * 获取 LangChain4j ToolSpecification 列表。
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
     * 执行工具回调。
     */
    public static ResponseData toolCallback(AiToolInfo aiToolInfo, String toolInput) {
        AiToolExecuteParam param = new AiToolExecuteParam();
        param.setToolId(aiToolInfo.getId());
        param.setToolClass(aiToolInfo.getToolClass());
        param.setToolInput(toolInput);
        String url = "http://" + aiToolInfo.getAppName() + "/rpc/ai/tool/execute";
        ResponseData responseData = authRestTemplate.postForEntity(url, param, ResponseData.class).getBody();
        return responseData;
    }

    /**
     * 刷新工具缓存。
     */
    public static boolean invalidateToolCache() {
        return FusionCache.invalidate(AiToolInfo.class.getSimpleName(), AiToolInfo.class.getSimpleName());
    }
}
