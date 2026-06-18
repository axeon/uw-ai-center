package uw.ai.center.vendor.dashscope.realtimeTranscriptionModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.ai.center.vendor.dashscope.DashScopeApiClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * DashScope Fun-ASR 实时语音识别模型实现。
 * 通过 DashScope WebSocket 端点（wss://dashscope.aliyuncs.com/api-ws/v1/inference），
 * 使用 Bearer API Key 鉴权，模型默认为 fun-asr-realtime。
 * <p>
 * 交互流程（参考 docs/阿里云官方文档/webSocket文档.md）：
 * 1. start() → 建立 WebSocket → 发送 run-task 指令 → 等待 task-started 事件
 * 2. sendAudio() → 持续发送二进制音频帧
 * 3. stop() → 发送 finish-task 指令 → 等待 task-finished 事件
 * <p>
 * 本实例可被复用：实例字段只持有配置（apiKey/modelName 等），会话状态封装到 {@link Session}，
 * 每次 start() 创建新的 Session，stop() 后可再次 start()。
 */
public class DashScopeRealtimeTranscriptionModel implements RealtimeTranscriptionModel {

    private static final Logger logger = LoggerFactory.getLogger(DashScopeRealtimeTranscriptionModel.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String apiKey;

    private final String modelName;

    private final String workspaceId;

    private final Map<String, Object> defaultParams;

    /** 当前会话（每次 start() 创建新 Session，stop() 后置 null） */
    private volatile Session currentSession;
    /** 是否已永久关闭（close() 后不可再 start） */
    private final AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * @param apiKey      DashScope API Key
     * @param modelName   模型名（如 fun-asr-realtime）
     * @param workspaceId 业务空间ID（可选）
     * @param params      识别参数（format/sample_rate/language_hints 等）
     */
    public DashScopeRealtimeTranscriptionModel(String apiKey, String modelName, String workspaceId, Map<String, Object> params) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("DashScope API Key 不能为空，请检查 ai_model_api.api_key 配置");
        }
        if (modelName == null || modelName.isEmpty()) {
            throw new IllegalArgumentException("DashScope 模型名不能为空，请检查 ai_model_config.model_name 配置");
        }
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.workspaceId = workspaceId;
        this.defaultParams = params != null ? params : new HashMap<>();
    }

    @Override
    public void start(TranscriptionResultListener listener) {
        if (closed.get()) {
            throw new IllegalStateException("RealtimeTranscriptionModel 已关闭，不可再次 start()");
        }
        Session existing = currentSession;
        if (existing != null && existing.isActive()) {
            throw new IllegalStateException("已有进行中的会话，不可重复调用 start()");
        }

        Session session = new Session(listener);
        currentSession = session;

        try {
            // 1. 创建 WebSocket 连接（带 Authorization 头），listener 持有 session 引用
            session.webSocket = DashScopeApiClient.createDashScopeWebSocket(apiKey, workspaceId, new DashScopeListener(this, session));

            // 2. 发送 run-task 指令
            String startMessage = buildRunTaskMessage(session);
            logger.info("发送 run-task 指令: taskId={}, model={}", session.taskId, modelName);
            session.webSocket.send(startMessage);

            // 3. 等待服务端确认 task-started（最多 10 秒）
            if (!session.startLatch.await(10, TimeUnit.SECONDS)) {
                throw new RuntimeException("等待 DashScope task-started 确认超时（10秒）");
            }
            if (!session.startSuccess) {
                throw new RuntimeException("DashScope task-started 失败: " + session.startError);
            }

            logger.info("DashScope Fun-ASR 实时语音识别会话已启动: taskId={}", session.taskId);

        } catch (Exception e) {
            // await 抛 InterruptedException 时恢复中断状态，避免信号丢失
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            session.closeWebSocket();
            currentSession = null;
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("启动 DashScope 实时语音识别会话失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendAudio(byte[] audioData) {
        Session session = currentSession;
        if (session == null || !session.isActive()) {
            logger.warn("sendAudio 被调用但模型未启动或已停止");
            return;
        }
        if (audioData == null || audioData.length == 0) {
            return;
        }
        if (session.webSocket != null) {
            ByteString bytes = ByteString.of(audioData);
            boolean sent = session.webSocket.send(bytes);
            if (!sent) {
                logger.warn("发送音频数据失败，WebSocket 可能已关闭");
            }
        }
    }

    @Override
    public void stop() {
        Session session = currentSession;
        if (session == null || !session.markStopped()) {
            return;
        }
        try {
            // 发送 finish-task 指令
            String stopMessage = buildFinishTaskMessage(session);
            logger.info("发送 finish-task 指令: taskId={}", session.taskId);
            if (session.webSocket != null) {
                session.webSocket.send(stopMessage);
            }

            // 等待 task-finished（最多 30 秒）
            boolean finished = session.completeLatch.await(30, TimeUnit.SECONDS);
            if (!finished) {
                logger.warn("等待 DashScope task-finished 超时（30秒）, taskId={}", session.taskId);
                if (session.listener != null) {
                    session.listener.onError("停止转录超时（30秒），部分结果可能丢失");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            session.closeWebSocket();
            currentSession = null;
        }
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            stop();
        }
    }

    // ==================== 会话上下文 ====================

    /**
     * 单次识别会话的状态。每次 start() 创建新实例，避免实例字段被复用污染。
     */
    private static class Session {
        /** 转录结果监听器 */
        final TranscriptionResultListener listener;
        /** WebSocket 连接实例 */
        volatile WebSocket webSocket;
        /** 当前会话的 task_id（run-task 时生成，finish-task 复用） */
        volatile String taskId;
        /** 等待 task-started 确认 */
        final CountDownLatch startLatch = new CountDownLatch(1);
        /** 等待 task-finished */
        final CountDownLatch completeLatch = new CountDownLatch(1);
        /** 启动是否成功 */
        volatile boolean startSuccess = false;
        /** 启动失败的错误信息 */
        volatile String startError = null;
        /** 句子计数器（DashScope 不返回 sentence index，本地自增） */
        volatile int sentenceIndex = 0;
        /** 是否已停止 */
        private final AtomicBoolean stopped = new AtomicBoolean(false);

        Session(TranscriptionResultListener listener) {
            this.listener = listener;
        }

        boolean isActive() {
            return !stopped.get();
        }

        boolean markStopped() {
            return stopped.compareAndSet(false, true);
        }

        void closeWebSocket() {
            WebSocket ws = webSocket;
            if (ws != null) {
                try {
                    ws.close(1000, "Normal closure");
                } catch (Exception e) {
                    logger.warn("关闭 DashScope WebSocket 失败", e);
                }
                webSocket = null;
            }
        }
    }

    // ==================== WebSocket 消息处理 ====================

    /**
     * OkHttp WebSocket 回调，处理 DashScope Fun-ASR 服务端推送的事件。
     * 每个会话对应一个 listener 实例，持有该会话的 Session 引用。
     */
    private static class DashScopeListener extends WebSocketListener {

        private final DashScopeRealtimeTranscriptionModel model;
        private final Session session;

        DashScopeListener(DashScopeRealtimeTranscriptionModel model, Session session) {
            this.model = model;
            this.session = session;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            logger.info("DashScope WebSocket 连接已建立");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            try {
                logger.debug("DashScope WebSocket 收到消息: {}", text);
                JsonNode message = OBJECT_MAPPER.readTree(text);
                model.handleEvent(session, message);
            } catch (Exception e) {
                logger.error("处理 DashScope WebSocket 消息失败: {}", text, e);
                if (session.listener != null) {
                    session.listener.onError("处理消息失败: " + e.getMessage());
                }
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            logger.info("DashScope WebSocket 正在关闭: code={}, reason={}", code, reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            logger.info("DashScope WebSocket 已关闭: code={}, reason={}", code, reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            logger.error("DashScope WebSocket 连接异常", t);
            session.startError = t.getMessage();
            session.startLatch.countDown();
            session.completeLatch.countDown();
            if (session.listener != null) {
                session.listener.onError("WebSocket 连接异常: " + t.getMessage());
            }
        }
    }

    /**
     * 处理 DashScope 服务端推送的事件，按 header.event 分发。
     * 事件类型：task-started / result-generated / task-finished / task-failed
     */
    private void handleEvent(Session session, JsonNode message) {
        JsonNode header = message.path("header");
        String event = header.path("event").asText("");
        String taskIdFromServer = header.path("task_id").asText("");
        int errorCode = header.path("error_code").asInt(0);
        String errorMessage = header.path("error_message").asText("");

        if (errorCode != 0 && !event.equals("result-generated")) {
            logger.warn("DashScope 返回错误: event={}, errorCode={}, errorMessage={}", event, errorCode, errorMessage);
        }

        JsonNode payload = message.path("payload");
        // 兼容：部分版本识别结果在 output 字段
        if (payload.isMissingNode() || payload.isEmpty()) {
            payload = message.path("output");
        }

        switch (event) {
            case "task-started":
                handleTaskStarted(session, taskIdFromServer);
                break;
            case "result-generated":
                handleResultGenerated(session, payload);
                break;
            case "task-finished":
                handleTaskFinished(session);
                break;
            case "task-failed":
                handleTaskFailed(session, errorCode, errorMessage);
                break;
            default:
                if (errorCode != 0) {
                    handleTaskFailed(session, errorCode, errorMessage);
                } else {
                    logger.warn("未知的 DashScope 事件类型: event={}", event);
                }
                break;
        }
    }

    private void handleTaskStarted(Session session, String taskIdFromServer) {
        // 优先使用服务端返回的 task_id
        if (!taskIdFromServer.isEmpty()) {
            session.taskId = taskIdFromServer;
        }
        session.startSuccess = true;
        logger.info("DashScope task-started 确认: taskId={}", session.taskId);
        if (session.listener != null) {
            session.listener.onStarted(session.taskId);
        }
        session.startLatch.countDown();
    }

    /**
     * 处理识别结果事件。
     * DashScope Fun-ASR 的实际结构（与早期文档略有差异）：
     * <pre>
     * payload.output.sentence = {
     *   text, begin_time, end_time,
     *   sentence_id, channel_id, speaker_id,
     *   sentence_end (bool), sentence_begin (bool),
     *   words: [{ begin_time, end_time, text, punctuation, ... }]
     * }
     * </pre>
     */
    private void handleResultGenerated(Session session, JsonNode payload) {
        // DashScope Fun-ASR: sentence 在 payload.output.sentence 路径下
        JsonNode output = payload.path("output");
        JsonNode sentenceNode = (!output.isMissingNode() && !output.isEmpty())
                ? output.path("sentence")
                : payload.path("sentence");

        if (sentenceNode.isMissingNode() || sentenceNode.isEmpty()) {
            logger.debug("result-generated 无 sentence 字段: payload={}", payload);
            return;
        }

        String text = sentenceNode.path("text").asText("");
        // 实际字段名是 sentence_end（兼容老协议的 is_sentence_end）
        boolean isSentenceEnd = sentenceNode.path("sentence_end").asBoolean(false);
        long beginTime = sentenceNode.path("begin_time").asLong(0);
        long endTime = sentenceNode.path("end_time").asLong(0);

        if (isSentenceEnd) {
            // 句子结束
            session.sentenceIndex++;
            double confidence = sentenceNode.path("confidence").asDouble(1.0);
            logger.info("DashScope SentenceEnd: index={}, text={}, confidence={}, beginTime={}, endTime={}",
                    session.sentenceIndex, text, confidence, beginTime, endTime);
            if (session.listener != null) {
                session.listener.onSentenceEnd(session.sentenceIndex, text, confidence, beginTime, endTime);
            }
        } else {
            // 中间结果
            logger.info("DashScope 中间结果: text={}, beginTime={}, endTime={}", text, beginTime, endTime);
            if (session.listener != null) {
                session.listener.onTranscriptionResultChanged(session.sentenceIndex + 1, text, endTime);
            }
        }
    }

    private void handleTaskFinished(Session session) {
        logger.info("DashScope task-finished: taskId={}", session.taskId);
        if (session.listener != null) {
            session.listener.onCompleted();
        }
        session.completeLatch.countDown();
    }

    private void handleTaskFailed(Session session, int errorCode, String errorMessage) {
        String errMsg = "DashScope task-failed: errorCode=" + errorCode + ", errorMessage=" + errorMessage;
        logger.error(errMsg);
        if (session.listener != null) {
            session.listener.onError(errMsg);
        }
        // 启动阶段失败，释放 startLatch
        if (!session.startSuccess && session.startLatch.getCount() > 0) {
            session.startError = errMsg;
            session.startLatch.countDown();
        }
        session.completeLatch.countDown();
    }

    // ==================== 构建指令消息 ====================

    /**
     * 构建 run-task 指令（DashScope Fun-ASR 协议）。
     * 参考文档：docs/阿里云官方文档/webSocket文档.md
     */
    private String buildRunTaskMessage(Session session) {
        try {
            ObjectNode message = OBJECT_MAPPER.createObjectNode();

            // header
            ObjectNode header = message.putObject("header");
            header.put("action", "run-task");
            header.put("task_id", UUID.randomUUID().toString());
            header.put("streaming", "duplex");

            session.taskId = header.path("task_id").asText();

            // payload
            ObjectNode payload = message.putObject("payload");
            payload.put("model", modelName);
            payload.put("task_group", "audio");
            payload.put("task", "asr");
            payload.put("function", "recognition");

            // parameters
            ObjectNode parameters = payload.putObject("parameters");
            parameters.put("format", getParam("format", "pcm"));
            parameters.put("sample_rate", getParam("sample_rate", 16000));

            // language_hints：字符串数组
            String languageHintsStr = getParam("language_hints", "zh");
            if (languageHintsStr != null && !languageHintsStr.isEmpty()) {
                ArrayNode langArray = parameters.putArray("language_hints");
                for (String lang : languageHintsStr.split(",")) {
                    String trimmed = lang.trim();
                    if (!trimmed.isEmpty()) {
                        langArray.add(trimmed);
                    }
                }
            }

            // 可选参数
            if (defaultParams.containsKey("semantic_punctuation_enabled")) {
                parameters.put("semantic_punctuation_enabled", getParam("semantic_punctuation_enabled", false));
            }
            if (defaultParams.containsKey("max_sentence_silence")) {
                parameters.put("max_sentence_silence", getParam("max_sentence_silence", 800));
            }
            if (defaultParams.containsKey("speech_noise_threshold")) {
                Object threshold = defaultParams.get("speech_noise_threshold");
                if (threshold instanceof Number) {
                    parameters.put("speech_noise_threshold", ((Number) threshold).doubleValue());
                } else if (threshold != null) {
                    try {
                        parameters.put("speech_noise_threshold", Double.parseDouble(threshold.toString()));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // input（DashScope 要求存在但实时识别无内容）
            payload.putObject("input");

            return OBJECT_MAPPER.writeValueAsString(message);
        } catch (Exception e) {
            throw new RuntimeException("构建 run-task 消息失败", e);
        }
    }

    /**
     * 构建 finish-task 指令。
     * DashScope 要求 payload 字段必须存在（与 run-task 保持一致的 task_group/task/function）。
     */
    private String buildFinishTaskMessage(Session session) {
        try {
            ObjectNode message = OBJECT_MAPPER.createObjectNode();

            ObjectNode header = message.putObject("header");
            header.put("action", "finish-task");
            header.put("task_id", session.taskId != null ? session.taskId : "");
            header.put("streaming", "duplex");

            ObjectNode payload = message.putObject("payload");
            payload.put("model", modelName);
            payload.put("task_group", "audio");
            payload.put("task", "asr");
            payload.put("function", "recognition");
            payload.putObject("input");

            return OBJECT_MAPPER.writeValueAsString(message);
        } catch (Exception e) {
            throw new RuntimeException("构建 finish-task 消息失败", e);
        }
    }

    // ==================== 参数读取工具方法 ====================

    private String getParam(String key, String defaultValue) {
        Object value = defaultParams.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    private int getParam(String key, int defaultValue) {
        Object value = defaultParams.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value != null) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private boolean getParam(String key, boolean defaultValue) {
        Object value = defaultParams.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value != null) {
            return Boolean.parseBoolean(value.toString());
        }
        return defaultValue;
    }
}
