package uw.ai.center.service;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.output.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.ai.center.constant.ModelType;
import uw.ai.center.constant.SessionType;
import uw.ai.center.entity.AiSessionInfo;
import uw.ai.center.entity.AiSessionMsg;
import uw.ai.center.vendor.AiVendorClientWrapper;
import uw.ai.center.vendor.AiVendorHelper;
import uw.common.app.constant.CommonState;
import uw.common.dto.ResponseData;
import uw.common.util.SystemClock;
import uw.dao.DaoManager;

/**
 * AI图片生成服务。
 */
public class AiImageService {

    private static final Logger logger = LoggerFactory.getLogger(AiImageService.class);
    private static final DaoManager dao = DaoManager.getInstance();

    /**
     * 生成图片。
     *
     * @param saasId   租户ID
     * @param userId   用户ID
     * @param userType 用户类型
     * @param userInfo 用户信息
     * @param configId 模型配置ID
     * @param prompt   图片提示词
     * @return 图片URL
     */
    public static ResponseData<String> generate(long saasId, long userId, int userType, String userInfo,
                                                 long configId, String prompt) {
        AiVendorClientWrapper wrapper;
        try {
            wrapper = AiVendorHelper.getClientWrapper(configId);
        } catch (Exception e) {
            logger.error("获取图片生成模型配置失败, configId={}", configId, e);
            return ResponseData.errorMsg("获取图片生成模型配置失败：" + e.getMessage());
        }

        if (wrapper == null || !wrapper.isType(ModelType.IMAGE_GENERATION)) {
            return ResponseData.errorMsg("图片生成模型配置错误，请检查configId和modelType");
        }

        // 初始化或加载会话
        AiSessionInfo sessionInfo = loadOrCreateSession(saasId, userId, userType, userInfo, configId, prompt);

        // 记录请求开始时间
        java.util.Date requestDate = SystemClock.nowDate();

        try {
            Response<Image> response = wrapper.getImageModel().generate(prompt);
            if (response == null || response.content() == null) {
                return ResponseData.errorMsg("图片生成返回结果为空");
            }
            String imageUrl = response.content().url() != null ? response.content().url().toString() : null;
            if (imageUrl == null || imageUrl.isEmpty()) {
                return ResponseData.errorMsg("图片生成返回的URL为空");
            }

            // 保存会话消息
            saveSessionMsg(saasId, userId, userType, userInfo, configId, sessionInfo, prompt, imageUrl, requestDate);

            return ResponseData.success(imageUrl);
        } catch (Exception e) {
            logger.error("图片生成失败, configId={}, prompt={}", configId, prompt, e);
            // 保存错误消息到历史
            saveSessionMsg(saasId, userId, userType, userInfo, configId, sessionInfo, prompt, "图片生成失败：" + e.getMessage(), requestDate);
            return ResponseData.errorMsg("图片生成失败：" + e.getMessage());
        }
    }

    /**
     * 加载或创建图片生成的会话。
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
        logger.warn("创建图片生成会话失败: {}", responseData.getMsg());
        return null;
    }

    /**
     * 保存图片生成的会话消息。
     * userPrompt 存提示词，responseInfo 存图片URL。
     */
    private static void saveSessionMsg(long saasId, long userId, int userType, String userInfo, long configId, AiSessionInfo sessionInfo, String prompt, String imageUrl, java.util.Date requestDate) {
        if (sessionInfo == null) {
            logger.warn("会话信息为空，跳过保存图片生成历史");
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
            sessionMsg.setResponseInfo(imageUrl);
            sessionMsg.setState(CommonState.ENABLED.getValue());
            sessionMsg.setRequestDate(requestDate);
            sessionMsg.setResponseStartDate(requestDate);
            sessionMsg.setResponseEndDate(SystemClock.nowDate());
            AiChatService.saveSessionMsg(sessionMsg);
        } catch (Exception e) {
            logger.error("保存图片生成会话消息失败", e);
        }
    }
}
