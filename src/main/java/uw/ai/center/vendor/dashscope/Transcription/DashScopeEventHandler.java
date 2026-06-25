package uw.ai.center.vendor.dashscope.transcription;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DashScope Fun-ASR 服务端事件分发器。
 * <p>从 {@link DashScopeRealtimeTranscriptionModel} 抽出,按 header.event 分发到对应处理器:
 * task-started / result-generated / task-finished / task-failed。
 * <p>事件处理涉及会话级状态({@link DashScopeRealtimeTranscriptionModel.Session} 的 startLatch/completeLatch/sentenceIndex/listener 等),
 * 实例方法直接读写 Session 字段(同包可见)。
 */
class DashScopeEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(DashScopeEventHandler.class);

    /**
     * 处理 DashScope 服务端推送的事件,按 header.event 分发。
     * 事件类型:task-started / result-generated / task-finished / task-failed
     */
    void handleEvent(DashScopeRealtimeTranscriptionModel.Session session, JsonNode message) {
        JsonNode header = message.path("header");
        String event = header.path("event").asText("");
        String taskIdFromServer = header.path("task_id").asText("");
        int errorCode = header.path("error_code").asInt(0);
        String errorMessage = header.path("error_message").asText("");

        if (errorCode != 0 && !event.equals("result-generated")) {
            logger.warn("DashScope 返回错误: event={}, errorCode={}, errorMessage={}", event, errorCode, errorMessage);
        }

        JsonNode payload = message.path("payload");
        // 兼容:部分版本识别结果在 output 字段
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

    private void handleTaskStarted(DashScopeRealtimeTranscriptionModel.Session session, String taskIdFromServer) {
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
     * DashScope Fun-ASR 的实际结构(与早期文档略有差异):
     * <pre>
     * payload.output.sentence = {
     *   text, begin_time, end_time,
     *   sentence_id, channel_id, speaker_id,
     *   sentence_end (bool), sentence_begin (bool),
     *   words: [{ begin_time, end_time, text, punctuation, ... }]
     * }
     * </pre>
     */
    private void handleResultGenerated(DashScopeRealtimeTranscriptionModel.Session session, JsonNode payload) {
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
        // 实际字段名是 sentence_end(兼容老协议的 is_sentence_end)
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

    private void handleTaskFinished(DashScopeRealtimeTranscriptionModel.Session session) {
        logger.info("DashScope task-finished: taskId={}", session.taskId);
        if (session.listener != null) {
            session.listener.onCompleted();
        }
        session.completeLatch.countDown();
    }

    private void handleTaskFailed(DashScopeRealtimeTranscriptionModel.Session session, int errorCode, String errorMessage) {
        String errMsg = "DashScope task-failed: errorCode=" + errorCode + ", errorMessage=" + errorMessage;
        logger.error(errMsg);
        if (session.listener != null) {
            session.listener.onError(errMsg);
        }
        // 启动阶段失败,释放 startLatch
        if (!session.startSuccess && session.startLatch.getCount() > 0) {
            session.startError = errMsg;
            session.startLatch.countDown();
        }
        session.completeLatch.countDown();
    }
}
