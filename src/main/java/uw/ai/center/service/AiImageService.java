package uw.ai.center.service;

import java.util.List;
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
import uw.ai.center.vendor.dashscope.DashScopeImageModel;
import uw.ai.vo.AiImageResultData;
import uw.common.response.ResponseData;
import uw.common.util.SystemClock;

/**
 * AI图片生成服务。
 */
public class AiImageService {

    private static final Logger logger = LoggerFactory.getLogger(AiImageService.class);

    /**
     * 生成图片。
     *
     * @param saasId    租户ID
     * @param userId    用户ID
     * @param userType  用户类型
     * @param userInfo  用户信息
     * @param configId  模型配置ID
     * @param sessionId 会话ID（可选，若大于0则保存到指定会话，否则自动创建新会话）
     * @param prompt    图片提示词
     * @return 图片生成结果
     */
    public static ResponseData<AiImageResultData> generate(long saasId, long userId, int userType, String userInfo,
                                                 long configId, long sessionId, String prompt) {
        AiVendorClientWrapper wrapper;
        try {
            wrapper = AiVendorHelper.getClientWrapper(configId);
        } catch (Exception e) {
            logger.error("获取图片生成模型配置失败, configId={}", configId, e);
            return ResponseData.errorMsg("获取图片生成模型配置失败：" + e.getMessage());
        }

        if (!wrapper.isType(ModelType.IMAGE_GENERATION)) {
            return ResponseData.errorMsg("图片生成模型配置错误，请检查configId和modelType");
        }

        // 加载或创建会话
        AiSessionInfo sessionInfo = loadOrCreateSession(saasId, userId, userType, userInfo, configId, sessionId, prompt);
        if (sessionInfo == null) {
            return ResponseData.errorMsg("会话不存在或创建失败！");
        }

        // 使用 AiChatService.initSessionMsg() 构造消息（与 Chat 完全一致）
        AiSessionMsg sessionMsg = AiChatService.initSessionMsg(
                saasId, userId, userType, userInfo, configId, sessionInfo.getId(),
                null, prompt, null, null, null, null);
        sessionMsg.setResponseStartDate(SystemClock.nowDate());

        try {
            // 优先使用 DashScopeImageModel 的多图生成能力
            List<String> imageUrls;
            if (wrapper.getImageModel() instanceof DashScopeImageModel dashScopeImageModel) {
                imageUrls = dashScopeImageModel.generateMultiple(prompt);
            } else {
                // 其他 ImageModel 实现，回退到单图模式
                Response<Image> response = wrapper.getImageModel().generate(prompt);
                if (response == null) {
                    return ResponseData.errorMsg("图片生成返回结果为空");
                }
                String url = response.content().url() != null ? response.content().url().toString() : null;
                imageUrls = (url != null && !url.isEmpty()) ? List.of(url) : List.of();
            }

            if (imageUrls.isEmpty()) {
                return ResponseData.errorMsg("图片生成返回的URL为空");
            }

            // 设置响应信息并保存
            sessionMsg.setRequestTokens(0);
            sessionMsg.setResponseTokens(0);
            sessionMsg.setResponseEndDate(SystemClock.nowDate());
            sessionMsg.setResponseInfo(String.join(",", imageUrls));
            AiChatService.saveSessionMsg(sessionMsg);

            AiImageResultData resultData = new AiImageResultData();
            resultData.setImageUrlList(imageUrls);
            resultData.setSessionId(sessionInfo.getId());
            return ResponseData.success(resultData);
        } catch (Exception e) {
            logger.error("图片生成失败, configId={}, prompt={}", configId, prompt, e);
            // 错误消息也保存到历史（与 Chat 的 [ERROR] 前缀格式一致）
            sessionMsg.setRequestTokens(0);
            sessionMsg.setResponseTokens(0);
            sessionMsg.setResponseEndDate(SystemClock.nowDate());
            sessionMsg.setResponseInfo("[ERROR] 图片生成失败：" + e.getMessage());
            AiChatService.saveSessionMsg(sessionMsg);
            return ResponseData.errorMsg("图片生成失败：" + e.getMessage());
        }
    }

    /**
     * 加载或创建图片生成的会话。
     */
    private static AiSessionInfo loadOrCreateSession(long saasId, long userId, int userType, String userInfo, long configId, long sessionId, String prompt) {
        // 指定了sessionId，加载该会话
        if (sessionId > 0) {
            AiSessionInfo sessionInfo = AiChatService.loadSession(saasId, userId, null, sessionId).getData();
            if (sessionInfo != null) {
                return sessionInfo;
            }
            logger.warn("指定的会话不存在, saasId={}, userId={}, sessionId={}", saasId, userId, sessionId);
            return null;
        }
        // 未指定sessionId，自动创建新会话
        ResponseData<AiSessionInfo> result = AiChatService.initSession(
                saasId, userId, userType, userInfo, configId,
                SessionType.COMMON.getValue(), prompt, null, null, null, null);
        if (result.isSuccess()) {
            return result.getData();
        }
        logger.warn("创建图片生成会话失败, configId={}, reason={}", configId, result.getMsg());
        return null;
    }
}
