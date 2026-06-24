package uw.ai.center.vendor.dashscope.transcription;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import uw.ai.center.service.AiAudioService;
import uw.common.response.ResponseData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI语音实时转录 WebSocket Handler。
 * 代理客户端与阿里云 NLS 之间的实时语音识别交互。
 * <p>
 * 端点路径：/ws/audio/transcribe?configId=xxx
 * <p>
 * 客户端→服务端消息：
 * - 二进制帧：音频数据（转发给 NLS）
 * - 文本帧 {"action":"stop"}：停止转录
 * <p>
 * 服务端→客户端消息（JSON）：
 * - {"type":"started","taskId":"xxx"}
 * - {"type":"sentence_begin","index":1,"time":0}
 * - {"type":"result_changed","index":1,"result":"北京的天","time":1835}
 * - {"type":"sentence_end","index":1,"result":"北京的天气。","confidence":1.0,"beginTime":0,"time":1820}
 * - {"type":"completed"}
 * - {"type":"error","message":"xxx"}
 */
public class AiAudioTranscriptionHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(AiAudioTranscriptionHandler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 存储每个 WebSocket 会话对应的 NLS 转录模型实例。
     * key = sessionId, value = RealtimeTranscriptionModel
     */
    private static final ConcurrentHashMap<String, RealtimeTranscriptionModel> SESSION_MODELS = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String configIdStr = session.getUri() != null ? getQueryParam(session, "configId") : null;
        if (configIdStr == null || configIdStr.isEmpty()) {
            sendMessage(session, buildErrorMessage("缺少 configId 参数"));
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        long configId;
        try {
            configId = Long.parseLong(configIdStr);
        } catch (NumberFormatException e) {
            sendMessage(session, buildErrorMessage("configId 参数格式错误"));
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        // 验证模型配置
        ResponseData<Void> validateResult = AiAudioService.validateTranscriptionConfig(configId);
        if (!validateResult.isSuccess()) {
            sendMessage(session, buildErrorMessage(validateResult.getMsg()));
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        // 创建转录模型实例
        RealtimeTranscriptionModel model;
        try {
            model = AiAudioService.createTranscriptionSession(configId);
        } catch (Exception e) {
            sendMessage(session, buildErrorMessage("创建转录会话失败: " + e.getMessage()));
            session.close(CloseStatus.SERVER_ERROR);
            return;
        }

        // 创建监听器
        TranscriptionResultListener listener = new TranscriptionResultListener() {
            @Override
            public void onStarted(String taskId) {
                sendMessage(session, buildStartedMessage(taskId));
            }

            @Override
            public void onSentenceBegin(int index, long time) {
                sendMessage(session, buildSentenceBeginMessage(index, time));
            }

            @Override
            public void onTranscriptionResultChanged(int index, String result, long time) {
                sendMessage(session, buildResultChangedMessage(index, result, time));
            }

            @Override
            public void onSentenceEnd(int index, String result, double confidence, long beginTime, long time) {
                sendMessage(session, buildSentenceEndMessage(index, result, confidence, beginTime, time));
            }

            @Override
            public void onCompleted() {
                sendMessage(session, buildCompletedMessage());
            }

            @Override
            public void onError(String message) {
                sendMessage(session, buildErrorMessage(message));
            }
        };

        try {
            // 启动转录会话
            model.start(listener);
            SESSION_MODELS.put(session.getId(), model);
            logger.info("WebSocket 实时转录会话已建立: sessionId={}, configId={}", session.getId(), configId);
        } catch (Exception e) {
            logger.error("启动转录会话失败: sessionId={}, configId={}", session.getId(), configId, e);
            sendMessage(session, buildErrorMessage("启动转录会话失败: " + e.getMessage()));
            try {
                // 启动失败时实例未入 SESSION_MODELS，主动关闭释放资源
                model.close();
            } catch (Exception ignored) {
            }
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof BinaryMessage binaryMessage) {
            RealtimeTranscriptionModel model = SESSION_MODELS.get(session.getId());
            if (model == null) {
                sendMessage(session, buildErrorMessage("转录会话不存在"));
                return;
            }

            // 将 ByteBuffer 转为 byte[] 并转发给 NLS
            ByteBuffer buffer = binaryMessage.getPayload();
            byte[] audioData = new byte[buffer.remaining()];
            buffer.get(audioData);
            model.sendAudio(audioData);

        } else if (message instanceof TextMessage textMessage) {
            String payload = textMessage.getPayload();

            // 解析文本帧
            try {
                JsonNode jsonNode = OBJECT_MAPPER.readTree(payload);
                String action = jsonNode.path("action").asText("");

                if ("stop".equals(action)) {
                    RealtimeTranscriptionModel model = SESSION_MODELS.get(session.getId());
                    if (model != null) {
                        model.stop();
                        logger.info("WebSocket 客户端请求停止转录: sessionId={}", session.getId());
                    }
                } else {
                    sendMessage(session, buildErrorMessage("未知的 action: " + action));
                }
            } catch (Exception e) {
                sendMessage(session, buildErrorMessage("无效的文本消息格式"));
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("WebSocket 传输错误: sessionId={}", session.getId(), exception);
        cleanupSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        cleanupSession(session);
        logger.info("WebSocket 实时转录会话已关闭: sessionId={}, status={}", session.getId(), status);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    // ==================== 会话清理 ====================

    private void cleanupSession(WebSocketSession session) {
        RealtimeTranscriptionModel model = SESSION_MODELS.remove(session.getId());
        if (model != null) {
            try {
                // model 实例为本次会话独立创建，关闭以释放底层 WebSocket 连接
                model.close();
            } catch (Exception e) {
                logger.warn("关闭转录会话失败: sessionId={}", session.getId(), e);
            }
        }
    }

    // ==================== 消息构建 ====================

    private String buildStartedMessage(String taskId) {
        try {
            ObjectNode msg = OBJECT_MAPPER.createObjectNode();
            msg.put("type", "started");
            msg.put("taskId", taskId);
            return OBJECT_MAPPER.writeValueAsString(msg);
        } catch (Exception e) {
            return "{\"type\":\"started\",\"taskId\":\"" + taskId + "\"}";
        }
    }

    private String buildSentenceBeginMessage(int index, long time) {
        try {
            ObjectNode msg = OBJECT_MAPPER.createObjectNode();
            msg.put("type", "sentence_begin");
            msg.put("index", index);
            msg.put("time", time);
            return OBJECT_MAPPER.writeValueAsString(msg);
        } catch (Exception e) {
            return "{\"type\":\"sentence_begin\",\"index\":" + index + ",\"time\":" + time + "}";
        }
    }

    private String buildResultChangedMessage(int index, String result, long time) {
        try {
            ObjectNode msg = OBJECT_MAPPER.createObjectNode();
            msg.put("type", "result_changed");
            msg.put("index", index);
            msg.put("result", result);
            msg.put("time", time);
            return OBJECT_MAPPER.writeValueAsString(msg);
        } catch (Exception e) {
            return "{\"type\":\"result_changed\"}";
        }
    }

    private String buildSentenceEndMessage(int index, String result, double confidence, long beginTime, long time) {
        try {
            ObjectNode msg = OBJECT_MAPPER.createObjectNode();
            msg.put("type", "sentence_end");
            msg.put("index", index);
            msg.put("result", result);
            msg.put("confidence", confidence);
            msg.put("beginTime", beginTime);
            msg.put("time", time);
            return OBJECT_MAPPER.writeValueAsString(msg);
        } catch (Exception e) {
            return "{\"type\":\"sentence_end\"}";
        }
    }

    private String buildCompletedMessage() {
        return "{\"type\":\"completed\"}";
    }

    private String buildErrorMessage(String message) {
        try {
            ObjectNode msg = OBJECT_MAPPER.createObjectNode();
            msg.put("type", "error");
            msg.put("message", message != null ? message : "未知错误");
            return OBJECT_MAPPER.writeValueAsString(msg);
        } catch (Exception e) {
            return "{\"type\":\"error\",\"message\":\"" + (message != null ? message : "未知错误") + "\"}";
        }
    }

    // ==================== 工具方法 ====================

    private void sendMessage(WebSocketSession session, String message) {
        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                logger.warn("发送 WebSocket 消息失败: sessionId={}", session.getId(), e);
            }
        }
    }

    /**
     * 获取 URL 查询参数。
     */
    private String getQueryParam(WebSocketSession session, String paramName) {
        if (session.getUri() == null) {
            return null;
        }
        String query = session.getUri().getQuery();
        if (query == null || query.isEmpty()) {
            return null;
        }
        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2);
            if (pair.length == 2 && pair[0].equals(paramName)) {
                return pair[1];
            }
        }
        return null;
    }
}
