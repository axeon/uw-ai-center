package uw.ai.center.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import uw.ai.center.constant.ModelType;
import uw.ai.center.constant.SessionType;
import uw.ai.center.entity.AiSessionInfo;
import uw.ai.center.entity.AiSessionMsg;
import uw.ai.center.vendor.dashscope.realtimeTranscriptionModel.RealtimeTranscriptionModel;
import uw.ai.center.vendor.dashscope.realtimeTranscriptionModel.TranscriptionResultListener;
import uw.ai.center.vendor.AiVendorClientWrapper;
import uw.ai.center.vendor.AiVendorHelper;
import uw.common.app.constant.CommonState;
import uw.common.response.ResponseData;
import uw.common.util.SystemClock;
import uw.dao.DaoManager;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * AI语音服务。
 * 提供实时语音识别（ASR）能力。
 */
public class AiAudioService {

    private static final Logger logger = LoggerFactory.getLogger(AiAudioService.class);
    private static final DaoManager dao = DaoManager.getInstance();

    /** 单次文件转录的音频时长上限（秒）。流式识别需按实时速率发送，时长过长会长时间阻塞请求线程。 */
    private static final int MAX_AUDIO_SECONDS = 60;
    /** 音频字节上限：按 16kHz / 16bit / 单声道 PCM 估算（32000 字节/秒）。wav/mp3 实际字节数更小，此上限偏保守。 */
    private static final int MAX_AUDIO_BYTES = 32000 * MAX_AUDIO_SECONDS;
    /** 文件转录总执行超时（含发送 + 识别收尾），略大于音频时长上限。 */
    private static final long MAX_TOTAL_MILLIS = (MAX_AUDIO_SECONDS + 10) * 1000L;

    /**
     * 创建实时转录会话（供 WebSocket Handler 使用）。
     * 获取 wrapper 中的 RealtimeTranscriptionModel 实例。
     * Model 实例本身可复用，每次 start() 内部创建新会话上下文。
     *
     * @param configId 模型配置ID
     * @return RealtimeTranscriptionModel 实例
     */
    public static RealtimeTranscriptionModel createTranscriptionSession(long configId) {
        AiVendorClientWrapper wrapper;
        try {
            wrapper = AiVendorHelper.getClientWrapper(configId);
        } catch (Exception e) {
            logger.error("获取语音识别模型配置失败, configId={}", configId, e);
            throw new RuntimeException("获取语音识别模型配置失败：" + e.getMessage());
        }

        if (wrapper == null || !wrapper.isType(ModelType.AUDIO_TRANSCRIPTION)) {
            throw new RuntimeException("语音识别模型配置错误，请检查configId和modelType");
        }

        return wrapper.getAudioTranscriptionModel();
    }

    /**
     * 验证模型配置是否为语音识别类型（供 WebSocket Handler 使用）。
     *
     * @param configId 模型配置ID
     * @return 验证结果
     */
    public static ResponseData<Void> validateTranscriptionConfig(long configId) {
        AiVendorClientWrapper wrapper;
        try {
            wrapper = AiVendorHelper.getClientWrapper(configId);
        } catch (Exception e) {
            logger.error("获取语音识别模型配置失败, configId={}", configId, e);
            return ResponseData.errorMsg("获取语音识别模型配置失败：" + e.getMessage());
        }

        if (wrapper == null || !wrapper.isType(ModelType.AUDIO_TRANSCRIPTION)) {
            return ResponseData.errorMsg("语音识别模型配置错误，请检查configId和modelType");
        }

        return ResponseData.success(null);
    }

    /**
     * 文件转录（供 RPC 使用，简化场景）。
     * 接收完整音频文件，内部创建 RealtimeTranscriptionModel 实例，
     * 通过 WebSocket 流式发送音频数据，收集所有识别结果后返回。
     * <p>
     * 注意：流式识别需按实时速率发送音频（每帧间隔 100ms），请求线程会被阻塞约「音频时长」。
     * 为避免长音频拖垮请求线程，限制了单次音频时长上限 {@link #MAX_AUDIO_SECONDS}，超出直接拒绝；
     * 发送阶段亦受 {@link #MAX_TOTAL_MILLIS} 总超时保护。音频格式以服务端 ai_model_config 配置为准。
     *
     * @param saasId    租户ID
     * @param userId    用户ID
     * @param userType  用户类型
     * @param userInfo  用户信息
     * @param configId  模型配置ID
     * @param audioFile 音频文件
     * @return 识别文本
     */
    public static ResponseData<String> transcribeFile(long saasId,
                                                      long userId,
                                                      int userType,
                                                      String userInfo,
                                                      long configId,
                                                      MultipartFile audioFile)
    {
        AiVendorClientWrapper wrapper;
        try {
            wrapper = AiVendorHelper.getClientWrapper(configId);
        } catch (Exception e) {
            logger.error("获取语音识别模型配置失败, configId={}", configId, e);
            return ResponseData.errorMsg("获取语音识别模型配置失败：" + e.getMessage());
        }

        if (wrapper == null || !wrapper.isType(ModelType.AUDIO_TRANSCRIPTION)) {
            return ResponseData.errorMsg("语音识别模型配置错误，请检查configId和modelType");
        }

        if (audioFile == null || audioFile.isEmpty()) {
            return ResponseData.errorMsg("音频文件不能为空");
        }

        // 音频时长上限保护：流式识别按实时速率发送，过长音频会长时间阻塞请求线程
        if (audioFile.getSize() > MAX_AUDIO_BYTES) {
            logger.warn("音频文件过大, configId={}, fileName={}, size={}, maxSize={}",
                    configId, audioFile.getOriginalFilename(), audioFile.getSize(), MAX_AUDIO_BYTES);
            return ResponseData.errorMsg("音频文件过大，上限约 " + MAX_AUDIO_SECONDS + " 秒（" + MAX_AUDIO_BYTES + " 字节）");
        }

        String fileName = audioFile.getOriginalFilename();

        // 初始化或加载会话
        AiSessionInfo sessionInfo = loadOrCreateSession(saasId, userId, userType, userInfo, configId, fileName);

        // 记录请求开始时间
        Date requestDate = SystemClock.nowDate();

        RealtimeTranscriptionModel model = wrapper.getAudioTranscriptionModel();
        if (model == null) {
            return ResponseData.errorMsg("语音识别模型实例为空，请检查模型配置");
        }

        try {
            byte[] audioBytes = audioFile.getBytes();
            logger.info("语音识别请求: userId={}, configId={}, fileName={}, audioSize={}",
                    userId, configId, fileName, audioBytes.length);

            // 收集所有 SentenceEnd 结果
            CopyOnWriteArrayList<String> sentences = new CopyOnWriteArrayList<>();

            TranscriptionResultListener listener = new TranscriptionResultListener() {
                @Override
                public void onStarted(String taskId) {
                    logger.info("语音识别会话已启动: taskId={}", taskId);
                }

                @Override
                public void onSentenceBegin(int index, long time) {
                    logger.debug("检测到句子开始: index={}, time={}", index, time);
                }

                @Override
                public void onTranscriptionResultChanged(int index, String result, long time) {
                    logger.debug("中间识别结果: index={}, result={}, time={}", index, result, time);
                }

                @Override
                public void onSentenceEnd(int index, String result, double confidence, long beginTime, long time) {
                    logger.info("句子识别完成: index={}, result={}, confidence={}", index, result, confidence);
                    sentences.add(result);
                }

                @Override
                public void onCompleted() {
                    logger.info("语音识别会话完成");
                }

                @Override
                public void onError(String message) {
                    logger.error("语音识别错误: {}", message);
                }
            };

            // 启动识别会话
            model.start(listener);

            // 分块发送音频数据，模拟实时流
            // 16kHz 采样率下，3200 字节约 100ms 的音频数据
            int chunkSize = 3200;
            int offset = 0;
            // 发送阶段总超时起点
            long sendStartMillis = requestDate.getTime();
            while (offset < audioBytes.length) {
                // 总执行超时保护：避免长音频阻塞请求线程
                if (SystemClock.nowDate().getTime() - sendStartMillis > MAX_TOTAL_MILLIS) {
                    logger.warn("语音识别发送阶段超时, configId={}, fileName={}, sentBytes={}/{}",
                            configId, fileName, offset, audioBytes.length);
                    return ResponseData.errorMsg("语音识别超时，音频过长（上限约 " + MAX_AUDIO_SECONDS + " 秒）");
                }
                int end = Math.min(offset + chunkSize, audioBytes.length);
                byte[] chunk = new byte[end - offset];
                System.arraycopy(audioBytes, offset, chunk, 0, end - offset);
                model.sendAudio(chunk);
                offset = end;
                // 模拟实时流速度：每次发送间隔 100ms
                Thread.sleep(100);
            }

            // 停止识别
            model.stop();

            // 拼接所有句子结果
            String transcriptionText = String.join("", sentences);

            if (transcriptionText.isEmpty()) {
                // 识别为空属于异常结果，不写入会话历史，避免污染
                return ResponseData.errorMsg("语音识别返回的文本为空");
            }

            // 保存会话消息（仅成功时保存）
            saveSessionMsg(saasId, userId, userType, userInfo, configId, sessionInfo, fileName, transcriptionText, requestDate);

            return ResponseData.success(transcriptionText);

        } catch (IOException e) {
            logger.error("读取音频文件失败, configId={}, fileName={}", configId, fileName, e);
            return ResponseData.errorMsg("读取音频文件失败：" + e.getMessage());
        } catch (Exception e) {
            logger.error("语音识别失败, configId={}, fileName={}", configId, fileName, e);
            return ResponseData.errorMsg("语音识别失败：" + e.getMessage());
        } finally {
            // 仅停止当前会话（实例复用，不调用 close()，close() 只在 wrapper 缓存失效时由 AiVendorHelper 触发）
            if (model != null) {
                try {
                    model.stop();
                } catch (Exception e) {
                    logger.warn("停止语音识别会话失败", e);
                }
            }
        }
    }

    /**
     * 加载或创建语音的会话。
     * 复用 AiChatService.initSession 的模式。
     */
    private static AiSessionInfo loadOrCreateSession(long saasId, long userId, int userType, String userInfo, long configId, String prompt) {
        // 尝试加载已有的通用会话
        AiSessionInfo sessionInfo = AiChatService.loadSession(saasId, userId, SessionType.COMMON.getValue(), null).getData();
        if (sessionInfo != null) {
            return sessionInfo;
        }
        // 没有会话则创建新的
        ResponseData<AiSessionInfo> responseData = AiChatService.initSession(saasId, userId, userType, userInfo, configId, SessionType.COMMON.getValue(), prompt, null, null, null, null);
        if (responseData.isSuccess()) {
            return responseData.getData();
        }
        logger.warn("创建语音会话失败: {}", responseData.getMsg());
        return null;
    }

    /**
     * 保存语音的会话消息。
     * userPrompt 存文件名/文本，responseInfo 存识别文本/状态。
     */
    private static void saveSessionMsg(long saasId, long userId, int userType, String userInfo, long configId, AiSessionInfo sessionInfo, String prompt, String responseInfo, Date requestDate) {
        if (sessionInfo == null) {
            logger.warn("会话信息为空，跳过保存语音历史");
            return;
        }
        try {
            long msgId = dao.getSequenceId(AiSessionMsg.class);
            AiSessionMsg sessionMsg = new AiSessionMsg();
            sessionMsg.setId(msgId);
            sessionMsg.setSaasId(saasId);
            sessionMsg.setUserId(userId);
            sessionMsg.setUserType(userType);
            sessionMsg.setUserInfo(userInfo);
            sessionMsg.setConfigId(configId);
            sessionMsg.setSessionId(sessionInfo.getId());
            sessionMsg.setUserPrompt(prompt);
            sessionMsg.setResponseInfo(responseInfo);
            sessionMsg.setState(CommonState.ENABLED.getValue());
            sessionMsg.setRequestDate(requestDate);
            sessionMsg.setResponseStartDate(requestDate);
            sessionMsg.setResponseEndDate(SystemClock.nowDate());
            AiChatService.saveSessionMsg(sessionMsg);
        } catch (Exception e) {
            logger.error("保存语音会话消息失败", e);
        }
    }
}
