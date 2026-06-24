package uw.ai.center.vendor.dashscope.transcription;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.ai.center.vendor.dashscope.DashScopeApiClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * DashScope Fun-ASR 实时语音识别模型实现。
 * 通过 DashScope WebSocket 端点(wss://dashscope.aliyuncs.com/api-ws/v1/inference),
 * 使用 Bearer API Key 鉴权,模型默认为 fun-asr-realtime。
 * <p>
 * 交互流程(参考 docs/阿里云官方文档/webSocket文档.md):
 * 1. start() → 建立 WebSocket → 发送 run-task 指令 → 等待 task-started 事件
 * 2. sendAudio() → 持续发送二进制音频帧
 * 3. stop() → 发送 finish-task 指令 → 等待 task-finished 事件
 * <p>
 * 本实例可被复用:实例字段只持有配置(apiKey/modelName 等),会话状态封装到 {@link Session},
 * 每次 start() 创建新的 Session,stop() 后可再次 start()。
 * <p>
 * 职责拆分:消息构建抽到 {@link DashScopeMessageBuilder},事件分发抽到 {@link DashScopeEventHandler},
 * 本类只保留生命周期编排(start/sendAudio/stop/close)、配置字段、Session 状态与 WebSocket 回调。
 */
public class DashScopeRealtimeTranscriptionModel implements RealtimeTranscriptionModel {

    private static final Logger logger = LoggerFactory.getLogger(DashScopeRealtimeTranscriptionModel.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String apiKey;

    private final String modelName;

    private final String workspaceId;

    private final DashScopeMessageBuilder messageBuilder;
    private final DashScopeEventHandler eventHandler;

    /** 当前会话(每次 start() 创建新 Session,stop() 后置 null) */
    private volatile Session currentSession;
    /** 是否已永久关闭(close() 后不可再 start) */
    private final AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * @param apiKey      DashScope API Key
     * @param modelName   模型名(如 fun-asr-realtime)
     * @param workspaceId 业务空间ID(可选)
     * @param params      识别参数(format/sample_rate/language_hints 等)
     */
    public DashScopeRealtimeTranscriptionModel(String apiKey, String modelName, String workspaceId, Map<String, Object> params) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("DashScope API Key 不能为空,请检查 ai_model_api.api_key 配置");
        }
        if (modelName == null || modelName.isEmpty()) {
            throw new IllegalArgumentException("DashScope 模型名不能为空,请检查 ai_model_config.model_name 配置");
        }
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.workspaceId = workspaceId;
        Map<String, Object> defaultParams = params != null ? params : new HashMap<>();
        this.messageBuilder = new DashScopeMessageBuilder(modelName, defaultParams);
        this.eventHandler = new DashScopeEventHandler();
    }

    @Override
    public void start(TranscriptionResultListener listener) {
        if (closed.get()) {
            throw new IllegalStateException("RealtimeTranscriptionModel 已关闭,不可再次 start()");
        }
        Session existing = currentSession;
        if (existing != null && existing.isActive()) {
            throw new IllegalStateException("已有进行中的会话,不可重复调用 start()");
        }

        Session session = new Session(listener);
        currentSession = session;

        try {
            // 1. 创建 WebSocket 连接(带 Authorization 头),listener 持有 session 引用
            session.webSocket = DashScopeApiClient.createDashScopeWebSocket(apiKey, workspaceId, new DashScopeListener(this, session));

            // 2. 发送 run-task 指令
            String startMessage = messageBuilder.buildRunTaskMessage(session);
            logger.info("发送 run-task 指令: taskId={}, model={}", session.taskId, modelName);
            session.webSocket.send(startMessage);

            // 3. 等待服务端确认 task-started(最多 10 秒)
            if (!session.startLatch.await(10, TimeUnit.SECONDS)) {
                throw new RuntimeException("等待 DashScope task-started 确认超时(10秒)");
            }
            if (!session.startSuccess) {
                throw new RuntimeException("DashScope task-started 失败: " + session.startError);
            }

            logger.info("DashScope Fun-ASR 实时语音识别会话已启动: taskId={}", session.taskId);

        } catch (Exception e) {
            // await 抛 InterruptedException 时恢复中断状态,避免信号丢失
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
                logger.warn("发送音频数据失败,WebSocket 可能已关闭");
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
            String stopMessage = messageBuilder.buildFinishTaskMessage(session);
            logger.info("发送 finish-task 指令: taskId={}", session.taskId);
            if (session.webSocket != null) {
                session.webSocket.send(stopMessage);
            }

            // 等待 task-finished(最多 30 秒)
            boolean finished = session.completeLatch.await(30, TimeUnit.SECONDS);
            if (!finished) {
                logger.warn("等待 DashScope task-finished 超时(30秒), taskId={}", session.taskId);
                if (session.listener != null) {
                    session.listener.onError("停止转录超时(30秒),部分结果可能丢失");
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
     * 单次识别会话的状态。每次 start() 创建新实例,避免实例字段被复用污染。
     * <p>声明为 public 嵌套类,以便同包的 {@link DashScopeMessageBuilder} 与 {@link DashScopeEventHandler} 访问字段。
     */
    public static class Session {
        /** 转录结果监听器 */
        final TranscriptionResultListener listener;
        /** WebSocket 连接实例 */
        volatile WebSocket webSocket;
        /** 当前会话的 task_id(run-task 时生成,finish-task 复用) */
        volatile String taskId;
        /** 等待 task-started 确认 */
        final CountDownLatch startLatch = new CountDownLatch(1);
        /** 等待 task-finished */
        final CountDownLatch completeLatch = new CountDownLatch(1);
        /** 启动是否成功 */
        volatile boolean startSuccess = false;
        /** 启动失败的错误信息 */
        volatile String startError = null;
        /** 句子计数器(DashScope 不返回 sentence index,本地自增) */
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
     * OkHttp WebSocket 回调,处理 DashScope Fun-ASR 服务端推送的事件。
     * 每个会话对应一个 listener 实例,持有该会话的 Session 引用。
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
                model.eventHandler.handleEvent(session, message);
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
}
