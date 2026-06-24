package uw.ai.center.vendor.dashscope;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.httpclient.http.HttpData;
import uw.httpclient.json.JsonInterfaceHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DashScope 原生 API 客户端。
 * 封装阿里云百炼 DashScope 的图片生成、实时语音识别（Fun-ASR）等能力。
 * <p>
 * HTTP 通信使用 uw-base 的 JsonInterfaceHelper（OkHttp 封装），
 * WebSocket 通信使用 JsonInterfaceHelper 底层的 OkHttpClient 创建连接。
 * <p>
 * DashScope 有两套 API 地址：
 * - OpenAI 兼容模式：https://dashscope.aliyuncs.com/compatible-mode/v1（仅支持 Chat/Embedding）
 * - 原生 API：https://dashscope.aliyuncs.com/api/v1（支持图片生成等全部能力）
 * <p>
 * 实时语音识别（Fun-ASR）使用独立的 WebSocket 端点：
 * - 华北2：wss://dashscope.aliyuncs.com/api-ws/v1/inference
 * - 鉴权：请求头 Authorization: Bearer &lt;api_key&gt;（握手阶段验证）
 */
public class DashScopeApiClient {

    private static final Logger logger = LoggerFactory.getLogger(DashScopeApiClient.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final JsonInterfaceHelper HTTP_HELPER = new JsonInterfaceHelper();

    /**
     * 响应体截断上限：日志与异常 message 中只保留前 500 字符，避免完整 HTTP 响应（可能含上游鉴权回显等敏感信息）进入可观测链路。
     */
    private static final int RESPONSE_BODY_LOG_LIMIT = 500;

    /**
     * 截断 HTTP 响应体到 {@value #RESPONSE_BODY_LOG_LIMIT} 字符上限。
     * 用于日志和异常 message，避免上游错误响应被原样塞入堆栈/日志造成信息泄露。
     */
    private static String truncateBody(String body) {
        if (body == null) {
            return "null";
        }
        return body.length() > RESPONSE_BODY_LOG_LIMIT
                ? body.substring(0, RESPONSE_BODY_LOG_LIMIT) + "...(truncated)"
                : body;
    }

    /**
     * DashScope Fun-ASR WebSocket
     */
    public static final String DASHSCOPE_WS_URL = "wss://dashscope.aliyuncs.com/api-ws/v1/inference";

    // ==================== DashScope 图片生成 ====================

    /**
     * 将配置的 apiUrl 转换为 DashScope 原生 API 的 base URL。
     * 兼容模式地址（compatible-mode/v1）会自动转换为原生 API 地址（api/v1）。
     */
    public static String resolveNativeApiBaseUrl(String apiUrl) {
        if (apiUrl == null || apiUrl.isEmpty()) {
            return "https://dashscope.aliyuncs.com/api/v1";
        }
        // 如果是 OpenAI 兼容模式地址，替换为原生 API 地址
        if (apiUrl.contains("compatible-mode")) {
            String nativeUrl = apiUrl.replace("compatible-mode/v1", "api/v1");
            logger.info("DashScope apiUrl 从兼容模式转换为原生API: {} -> {}", apiUrl, nativeUrl);
            return nativeUrl;
        }
        // 已经是原生 API 地址，直接使用
        return apiUrl;
    }

    /**
     * 图片生成（异步任务模式）。
     * 通义万相 API 先提交任务获取 task_id，再轮询获取结果。
     *
     * @param baseUrl  DashScope API 基础 URL（如 https://dashscope.aliyuncs.com/api/v1）
     * @param apiKey   API Key
     * @param model    模型名（如 wanx-v1）
     * @param prompt   图片提示词
     * @param params   额外参数（size、style、n 等）
     * @return 图片 URL 列表
     */
    public static List<String> generateImage(String baseUrl, String apiKey, String model, String prompt, Map<String, Object> params) {
        // 自动转换 URL
        String nativeBaseUrl = resolveNativeApiBaseUrl(baseUrl);

        try {
            // 1. 提交异步任务
            String taskUrl = nativeBaseUrl + "/services/aigc/text2image/image-synthesis";

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + apiKey);
            headers.put("X-DashScope-Async", "enable");

            Map<String, Object> input = new HashMap<>();
            input.put("prompt", prompt);

            Map<String, Object> parameters = new HashMap<>();
            if (params != null) {
                parameters.putAll(params);
            }
            // 默认生成1张，若配置中已指定 n 则以配置为准
            if (!parameters.containsKey("n")) {
                parameters.put("n", 1);
            }

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("input", input);
            body.put("parameters", parameters);

            logger.info("DashScope图片生成提交任务: url={}, model={}", taskUrl, model);
            if (logger.isDebugEnabled()) {
                logger.debug("DashScope图片生成请求参数: parameters={}, prompt={}", parameters, prompt);
            }

            HttpData httpData = HTTP_HELPER.postBodyForData(taskUrl, headers, body);
            String responseBody = httpData.getResponseData();
            logger.info("DashScope图片生成任务提交响应: statusCode={}", httpData.getStatusCode());
            if (logger.isDebugEnabled()) {
                logger.debug("DashScope图片生成任务提交响应体: body={}", truncateBody(responseBody));
            }

            JsonNode responseJson = OBJECT_MAPPER.readTree(responseBody);

            // 检查是否直接返回了结果（部分模型支持同步模式）
            String statusCode = responseJson.path("status_code").asText();
            if ("200".equals(statusCode) && responseJson.has("output")) {
                JsonNode outputNode = responseJson.path("output");
                if ("SUCCEEDED".equals(outputNode.path("task_status").asText())) {
                    List<String> urls = extractImageUrls(outputNode);
                    logger.info("DashScope图片生成同步返回成功: imageCount={}, urls={}", urls.size(), urls);
                    return urls;
                }
            }

            // 获取异步任务的 task_id
            String taskId = responseJson.path("output").path("task_id").asText();
            String errorMsg = responseJson.path("message").asText("");
            if (taskId.isEmpty()) {
                throw new RuntimeException("DashScope图片生成任务提交失败: statusCode=" + statusCode
                        + ", message=" + errorMsg + ", response=" + truncateBody(responseBody));
            }
            logger.info("DashScope图片生成任务已提交: taskId={}", taskId);

            // 2. 轮询任务结果
            String queryUrl = nativeBaseUrl + "/tasks/" + taskId;
            Map<String, String> queryHeaders = new HashMap<>();
            queryHeaders.put("Authorization", "Bearer " + apiKey);

            int maxRetries = 30;
            for (int i = 0; i < maxRetries; i++) {
                Thread.sleep(2000);

                HttpData queryData = HTTP_HELPER.getForData(queryUrl, queryHeaders, null);
                JsonNode queryJson = OBJECT_MAPPER.readTree(queryData.getResponseData());

                String taskStatus = queryJson.path("output").path("task_status").asText();
                logger.info("DashScope图片生成任务轮询({}/{}): taskId={}, status={}", i + 1, maxRetries, taskId, taskStatus);

                switch (taskStatus) {
                    case "SUCCEEDED":
                        List<String> urls = extractImageUrls(queryJson.path("output"));
                        logger.info("DashScope图片生成异步返回成功: imageCount={}, urls={}", urls.size(), urls);
                        return urls;
                    case "FAILED":
                        String failMsg = queryJson.path("output").path("message").asText("未知错误");
                        throw new RuntimeException("DashScope图片生成任务失败: " + failMsg);
                    case "PENDING", "RUNNING":
                        // 继续轮询
                        break;
                    default:
                        throw new RuntimeException("DashScope图片生成任务未知状态: " + taskStatus);
                }
            }

            throw new RuntimeException("DashScope图片生成任务超时（60秒）");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("DashScope图片生成任务被中断", e);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("DashScope图片生成调用失败", e);
        }
    }

    /**
     * 从任务输出中提取所有图片 URL。
     */
    private static List<String> extractImageUrls(JsonNode outputNode) {
        JsonNode resultsNode = outputNode.path("results");
        if (resultsNode.isArray() && !resultsNode.isEmpty()) {
            List<String> urls = new ArrayList<>();
            for (JsonNode result : resultsNode) {
                String url = result.path("url").asText();
                if (!url.isEmpty()) {
                    urls.add(url);
                    continue;
                }
                // 有些模型返回 b64_image
                String b64Image = result.path("b64_image").asText();
                if (!b64Image.isEmpty()) {
                    urls.add("data:image/png;base64," + b64Image);
                }
            }
            if (!urls.isEmpty()) {
                return urls;
            }
        }
        throw new RuntimeException("DashScope图片生成结果中未找到图片URL");
    }

    /**
     * 创建 DashScope Fun-ASR WebSocket 连接。
     * 鉴权通过请求头 Authorization: Bearer &lt;apiKey&gt; 在握手阶段验证。
     *
     * @param apiKey      DashScope API Key（必填）
     * @param workspaceId 阿里云百炼业务空间ID（可选，传 null 则不设置 X-DashScope-WorkSpace 头）
     * @param listener    OkHttp WebSocketListener 回调
     * @return WebSocket 实例
     */
    public static WebSocket createDashScopeWebSocket(String apiKey, String workspaceId, WebSocketListener listener) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(DASHSCOPE_WS_URL)
                .header("Authorization", "Bearer " + apiKey);

        if (workspaceId != null && !workspaceId.isEmpty()) {
            requestBuilder.header("X-DashScope-WorkSpace", workspaceId);
        }

        Request request = requestBuilder.build();
        logger.info("创建DashScope Fun-ASR WebSocket连接: url={}, workspaceId={}", DASHSCOPE_WS_URL, workspaceId);
        return HTTP_HELPER.getOkHttpClient().newWebSocket(request, listener);
    }

    // ==================== 语音合成 ====================

    /**
     * 语音合成（CosyVoice/Sambert）。
     *
     * @param baseUrl DashScope API 基础 URL
     * @param apiKey  API Key
     * @param model   模型名
     * @param text    待合成文本
     * @param params  额外参数
     * @return 音频二进制数据
     */
    public static byte[] synthesizeSpeech(String baseUrl, String apiKey, String model, String text, Map<String, Object> params) {
        // 语音合成将在功能点4中实现
        throw new UnsupportedOperationException("语音合成功能将在后续功能点中实现");
    }
}
