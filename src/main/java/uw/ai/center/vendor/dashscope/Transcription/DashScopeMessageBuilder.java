package uw.ai.center.vendor.dashscope.transcription;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * DashScope Fun-ASR 协议消息构建器。
 * <p>从 {@link DashScopeRealtimeTranscriptionModel} 抽出,聚焦于 run-task / finish-task 指令的 JSON 构建。
 * <p>持有 modelName 与 defaultParams 引用,实例方法读写 {@link DashScopeRealtimeTranscriptionModel.Session} 的 taskId 字段。
 */
class DashScopeMessageBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DashScopeMessageBuilder.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String modelName;
    private final Map<String, Object> defaultParams;

    DashScopeMessageBuilder(String modelName, Map<String, Object> defaultParams) {
        this.modelName = modelName;
        this.defaultParams = defaultParams;
    }

    /**
     * 构建 run-task 指令(DashScope Fun-ASR 协议)。
     * 生成的 task_id 会写回 session.taskId,供后续 finish-task 复用。
     */
    String buildRunTaskMessage(DashScopeRealtimeTranscriptionModel.Session session) {
        try {
            ObjectNode message = OBJECT_MAPPER.createObjectNode();

            // header
            ObjectNode header = message.putObject("header");
            header.put("action", "run-task");
            String taskId = java.util.UUID.randomUUID().toString();
            header.put("task_id", taskId);
            header.put("streaming", "duplex");

            session.taskId = taskId;

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

            // language_hints:字符串数组
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

            // input(DashScope 要求存在但实时识别无内容)
            payload.putObject("input");

            return OBJECT_MAPPER.writeValueAsString(message);
        } catch (Exception e) {
            throw new RuntimeException("构建 run-task 消息失败", e);
        }
    }

    /**
     * 构建 finish-task 指令。
     * DashScope 要求 payload 字段必须存在(与 run-task 保持一致的 task_group/task/function)。
     */
    String buildFinishTaskMessage(DashScopeRealtimeTranscriptionModel.Session session) {
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
