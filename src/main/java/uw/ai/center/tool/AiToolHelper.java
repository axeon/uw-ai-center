package uw.ai.center.tool;

import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uw.ai.center.entity.AiToolInfo;
import uw.ai.vo.AiToolExecuteParam;
import uw.common.constant.StateCommon;
import uw.common.dto.ResponseData;
import uw.dao.DaoFactory;
import uw.dao.DataList;
import uw.dao.TransactionException;

/**
 * AiToolHelper。
 */
@Service
public class AiToolHelper {

    /**
     * DaoFactory。
     */
    private static final DaoFactory dao = DaoFactory.getInstance();
    /**
     * Rest模板类
     */
    private static RestTemplate tokenRestTemplate;

    public AiToolHelper(RestTemplate tokenRestTemplate) {
        AiToolHelper.tokenRestTemplate = tokenRestTemplate;
    }

    /**
     * 获取工具回调列表。
     *
     * @return
     */
    public static ToolCallback[] getToolCallbacks() {
        ToolCallback[] toolCallbacks = new ToolCallback[0];
        try {
            DataList<AiToolInfo> dataList = dao.list( AiToolInfo.class, "select * from ai_tool_info where state=?", new Object[]{StateCommon.ENABLED.getValue()} );
            toolCallbacks = dataList.results().stream().map( AiToolCallback::new ).toArray( ToolCallback[]::new );
        } catch (TransactionException e) {
            throw new RuntimeException( e );
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
        ResponseData responseData = tokenRestTemplate.postForEntity( url, param, ResponseData.class ).getBody();
        return responseData;
    }
}
