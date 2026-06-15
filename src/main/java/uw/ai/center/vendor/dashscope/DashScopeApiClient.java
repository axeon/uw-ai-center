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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DashScope 原生 HTTP API 客户端。
 * 封装阿里云 DashScope 的图片生成、语音识别、语音合成等 API 调用。
 * <p>
 * HTTP 通信使用 uw-base 的 JsonInterfaceHelper（OkHttp 封装），
 * WebSocket 通信使用 JsonInterfaceHelper 底层的 OkHttpClient 创建连接。
 * <p>
 * DashScope 有两套 API 地址：
 * - OpenAI 兼容模式：https://dashscope.aliyuncs.com/compatible-mode/v1（仅支持 Chat/Embedding）
 * - 原生 API：https://dashscope.aliyuncs.com/api/v1（支持图片生成等全部能力）
 * <p>
 * 阿里云智能语音服务（NLS）使用独立的 WebSocket 协议：
 * - 网关地址：wss://nls-gateway-cn-shanghai.aliyuncs.com/ws/v1
 * - 鉴权：AccessKey ID/Secret → Token（POP API 获取，24h 有效）
 */
public class DashScopeApiClient {

    private static final Logger logger = LoggerFactory.getLogger(DashScopeApiClient.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final JsonInterfaceHelper HTTP_HELPER = new JsonInterfaceHelper();

    /**
     * NLS Token 缓存，key = accessKeyId，value = TokenEntry。
     * Token 有效期 24 小时，提前 5 分钟刷新。
     */
    private static final ConcurrentHashMap<String, TokenEntry> TOKEN_CACHE = new ConcurrentHashMap<>();

    /** Token 提前刷新时间（5分钟） */
    private static final long TOKEN_REFRESH_AHEAD_MILLIS = 5 * 60 * 1000L;

    // ==================== DashScope 图片生成 ====================

    /**
     * 将配置的 apiUrl 转换为 NLS WebSocket 网关地址。
     * DashScope 的 HTTP API 地址会自动转换为对应的 NLS WebSocket 地址。
     * <p>
     * 转换规则：
     * - https://dashscope.aliyuncs.com/compatible-mode/v1 → wss://nls-gateway-cn-shanghai.aliyuncs.com/ws/v1
     * - https://dashscope.aliyuncs.com/api/v1 → wss://nls-gateway-cn-shanghai.aliyuncs.com/ws/v1
     * - 已是 wss:// 地址则直接使用
     * - 其他域名按区域推断（默认上海）
     */
    public static String resolveNlsGatewayUrl(String apiUrl) {
        if (apiUrl == null || apiUrl.isEmpty()) {
            return "wss://nls-gateway-cn-shanghai.aliyuncs.com/ws/v1";
        }
        // 已经是 WebSocket 地址，直接使用
        if (apiUrl.startsWith("wss://") || apiUrl.startsWith("ws://")) {
            return apiUrl;
        }
        // DashScope HTTP 地址转换为 NLS WebSocket 地址
        if (apiUrl.contains("dashscope.aliyuncs.com")) {
            String nlsUrl = "wss://nls-gateway-cn-shanghai.aliyuncs.com/ws/v1";
            logger.info("DashScope apiUrl 自动转换为NLS网关地址: {} -> {}", apiUrl, nlsUrl);
            return nlsUrl;
        }
        // 其他地址默认按上海区域转换
        logger.warn("无法识别的apiUrl格式，使用默认NLS上海网关地址: {} -> wss://nls-gateway-cn-shanghai.aliyuncs.com/ws/v1", apiUrl);
        return "wss://nls-gateway-cn-shanghai.aliyuncs.com/ws/v1";
    }

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

            logger.info("DashScope图片生成提交任务: url={}, model={}, prompt={}", taskUrl, model, prompt);

            HttpData httpData = HTTP_HELPER.postBodyForData(taskUrl, headers, body);
            String responseBody = httpData.getResponseData();
            logger.info("DashScope图片生成任务提交响应: statusCode={}, body={}", httpData.getStatusCode(), responseBody);

            JsonNode responseJson = OBJECT_MAPPER.readTree(responseBody);

            // 检查是否直接返回了结果（部分模型支持同步模式）
            String statusCode = responseJson.path("status_code").asText();
            if ("200".equals(statusCode) && responseJson.has("output")) {
                JsonNode outputNode = responseJson.path("output");
                if ("SUCCEEDED".equals(outputNode.path("task_status").asText())) {
                    return extractImageUrls(outputNode);
                }
            }

            // 获取异步任务的 task_id
            String taskId = responseJson.path("output").path("task_id").asText();
            String errorMsg = responseJson.path("message").asText("");
            if (taskId.isEmpty()) {
                throw new RuntimeException("DashScope图片生成任务提交失败: statusCode=" + statusCode + ", message=" + errorMsg + ", response=" + responseBody);
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
                logger.debug("DashScope图片生成任务轮询({}/{}): taskId={}, status={}", i + 1, maxRetries, taskId, taskStatus);

                switch (taskStatus) {
                    case "SUCCEEDED":
                        return extractImageUrls(queryJson.path("output"));
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
     * 语音识别（Paraformer）。
     *
     * @param baseUrl  DashScope API 基础 URL
     * @param apiKey   API Key
     * @param model    模型名（如 paraformer-v2）
     * @param audioUrl 音频文件 URL
     * @param params   额外参数
     * @return 识别文本
     */
    public static String transcribeAudio(String baseUrl, String apiKey, String model, String audioUrl, Map<String, Object> params) {
        // 语音识别将在功能点3中实现
        throw new UnsupportedOperationException("语音识别功能将在后续功能点中实现");
    }

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
