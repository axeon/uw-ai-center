package uw.ai.center.tool;

import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uw.ai.center.entity.AiToolInfo;
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
    public static FunctionCallback[] getToolCallbacks() {
        FunctionCallback[] functionCallbacks = new FunctionCallback[0];
        try {
            DataList<AiToolInfo> dataList = dao.list( AiToolInfo.class, "select * from ai_tool_info where state=?", new Object[]{StateCommon.ENABLED.getValue()} );
            functionCallbacks = dataList.results().stream().map( AiToolCallback::new ).toArray( ToolCallback[]::new );
        } catch (TransactionException e) {
            throw new RuntimeException( e );
        }
        return functionCallbacks;
    }

    /**
     * 执行工具回调。
     *
     * @param aiToolInfo
     * @param toolInput
     * @return
     */
    public static ResponseData toolCallback(AiToolInfo aiToolInfo, String toolInput) {
        // 创建表单数据
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add( "toolClass", aiToolInfo.getToolClass() );
        formData.add( "toolInput", toolInput );

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_FORM_URLENCODED );

        // 创建HttpEntity对象
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>( formData, headers );

        String url = "http://" + aiToolInfo.getAppName() + "/rpc/ai/tool/run";
        // 发送POST请求并获取响应
        ResponseData responseData = tokenRestTemplate.postForObject( url, request, ResponseData.class );
        return responseData;
    }
}
