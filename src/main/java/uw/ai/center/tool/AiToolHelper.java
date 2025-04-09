package uw.ai.center.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uw.ai.center.entity.AiToolInfo;
import uw.ai.vo.AiToolCallInfo;
import uw.ai.vo.AiToolExecuteParam;
import uw.cache.CacheDataLoader;
import uw.cache.FusionCache;
import uw.common.app.constant.CommonState;
import uw.common.dto.ResponseData;
import uw.dao.DaoFactory;
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


    private static final Logger logger = LoggerFactory.getLogger( AiToolHelper.class );

    /**
     * 工具列表缓存名称。
     */
    private static final String TOOL_CACHE_NAME = "AiToolInfoList";
    /**
     * DaoFactory。
     */
    private static final DaoFactory dao = DaoFactory.getInstance();
    /**
     * Rest模板类
     */
    private static RestTemplate authRestTemplate;

    static {

        // AI模型配置数据缓存。
        FusionCache.config( FusionCache.Config.builder().cacheName( TOOL_CACHE_NAME ).localCacheMaxNum( 3 ).globalCacheExpireMillis( 86400_000L ).nullProtectMillis( 10_000L ).build(), new CacheDataLoader<String, Map<String, AiToolInfo>>() {
            @Override
            public Map<String, AiToolInfo> load(String toolCode) throws Exception {
                DataList<AiToolInfo> dataList = dao.list( AiToolInfo.class, "select * from ai_tool_info where state=?", new Object[]{CommonState.ENABLED.getValue()} );
                return dataList.results().stream().collect( Collectors.toMap( x -> x.getAppName() + "/" + x.getToolClass(), x -> x, (existingValue, newValue) -> existingValue ) );
            }
        } );
    }

    public AiToolHelper(RestTemplate authRestTemplate) {
        AiToolHelper.authRestTemplate = authRestTemplate;
    }

    /**
     * 获取工具回调列表。
     *
     * @return
     */
    public static ToolCallback[] getAllToolCallbacks() {
        ToolCallback[] toolCallbacks = new ToolCallback[0];
        try {
            Map<String, AiToolInfo> map = FusionCache.get( TOOL_CACHE_NAME, TOOL_CACHE_NAME );
            if (map != null) {
                toolCallbacks = map.values().stream().map( x -> new AiToolCallback( x, false ) ).toArray( ToolCallback[]::new );
            }
        } catch (Exception e) {
            logger.error( "获取ToolCallback失败！{}", e.getMessage(), e );
        }
        return toolCallbacks;
    }

    /**
     * 获取工具回调列表。
     *
     * @return
     */
    public static ToolCallback[] getToolCallbacks(List<AiToolCallInfo> aiToolCallInfoList) {
        ToolCallback[] toolCallbacks = new ToolCallback[0];
        try {
            Map<String, AiToolInfo> map = FusionCache.get( TOOL_CACHE_NAME, TOOL_CACHE_NAME );
            List<ToolCallback> list = new ArrayList<ToolCallback>( aiToolCallInfoList.size() );
            for (AiToolCallInfo aiToolCallInfo : aiToolCallInfoList) {
                AiToolInfo aiToolInfo = map.get( aiToolCallInfo.getToolCode() );
                if (aiToolInfo != null) {
                    list.add( new AiToolCallback( aiToolInfo, aiToolCallInfo.isReturnDirect() ) );
                }
            }
            return list.toArray( toolCallbacks );
        } catch (Exception e) {
            logger.error( "获取ToolCallback失败！{}", e.getMessage(), e );
        }
        return toolCallbacks;
    }


    /**
     * 执行工具回调。
     *
     * @param aiToolInfo
     * @param toolInput
     * @return
     */
    public static ResponseData toolCallback(AiToolInfo aiToolInfo, String toolInput) {
        AiToolExecuteParam param = new AiToolExecuteParam();
        param.setToolId( aiToolInfo.getId() );
        param.setToolClass( aiToolInfo.getToolClass() );
        param.setToolInput( toolInput );
        String url = "http://" + aiToolInfo.getAppName() + "/rpc/ai/tool/execute";
        // 发送POST请求并获取响应
        ResponseData responseData = authRestTemplate.postForEntity( url, param, ResponseData.class ).getBody();
        return responseData;
    }

    /**
     * 刷新工具缓存。
     *
     * @return
     */
    public static boolean invalidateToolCache() {
        return FusionCache.invalidate( TOOL_CACHE_NAME, TOOL_CACHE_NAME );
    }
}
