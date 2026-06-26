package uw.ai.center.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.ai.center.entity.AiSessionInfo;
import uw.ai.center.entity.AiSessionMsg;
import uw.ai.center.vendor.AiVendorHelper;
import uw.ai.center.vendor.client.ImageGenerationClient;
import uw.ai.vo.AiImageResultData;
import uw.common.response.ResponseData;
import uw.common.util.SystemClock;

/**
 * AI图片生成服务。
 *
 * <p>设计思路：图片生成默认不走会话（不创建 session、不保存 msg）；
 * 仅当调用方显式传入有效 sessionId 时，才将本次生成结果追加到指定会话历史。</p>
 *
 * <p>创建理由：
 * <ul>
 *   <li>功能性：封装图片生成模型调用 + 可选的会话历史记录</li>
 * </ul>
 * </p>
 *
 * @author tanlx
 * @since 1.2.0
 */
public class AiImageService {

    private static final Logger logger = LoggerFactory.getLogger(AiImageService.class);

    /**
     * 生成图片。
     *
     * <p>设计思路：sessionId=0 时纯生成图片返回，不写 ai_session_info/ai_session_msg；
     * sessionId>0 时加载已有会话，将提示词与生成结果作为消息保存到 ai_session_msg，
     * 不自动创建新会话（图片生成与会话解耦）。</p>
     *
     * <p>实现步骤：</p>
     * <ol>
     *   <li>[调用缓存] AiVendorHelper.getImageClient(configId) 从 FusionCache 取图片生成客户端</li>
     *   <li>[校验] sessionId>0 时，AiChatService.loadSession 加载指定会话；不存在则返回错误，不自动创建</li>
     *   <li>[调用AI] imageClient.generateImages(prompt) 调用模型生成图片 URL 列表</li>
     *   <li>[校验] imageUrls 为空时返回错误；sessionInfo 非空时同步写入 [ERROR] 消息历史</li>
     *   <li>[保存] sessionInfo 非空时，AiChatService.initSessionMsg + saveSessionMsg 保存提示词与图片URL到会话</li>
     *   <li>[封装] 构造 AiImageResultData，sessionId 字段：有会话则填会话ID，否则填 0</li>
     *   <li>[异常处理] 生成异常时，sessionInfo 非空则同步写入 [ERROR] 消息历史</li>
     * </ol>
     *
     * @param saasId    租户ID
     * @param userId    用户ID
     * @param userType  用户类型
     * @param userInfo  用户信息
     * @param configId  模型配置ID（须为 IMAGE_GENERATION 类型）
     * @param sessionId 会话ID（0 表示不写会话；大于 0 则追加到指定会话历史）
     * @param prompt    图片提示词
     * @return 图片生成结果（URL 列表 + 会话ID，会话ID为 0 表示本次未写会话）
     */
    public static ResponseData<AiImageResultData> generate(long saasId, long userId, int userType, String userInfo,
                                                           long configId, long sessionId, String prompt) {
        ImageGenerationClient imageClient;
        try {
            imageClient = AiVendorHelper.getImageClient(configId);
        } catch (Exception e) {
            logger.error("获取图片生成模型配置失败, configId={}", configId, e);
            return ResponseData.errorMsg("获取图片生成模型配置失败：" + e.getMessage());
        }

        // sessionId>0 时加载已有会话；sessionId=0 时不创建新会话（图片生成不走会话）
        AiSessionInfo sessionInfo = null;
        if (sessionId > 0) {
            sessionInfo = AiChatService.loadSession(saasId, userId, null, sessionId).getData();
            if (sessionInfo == null) {
                return ResponseData.errorMsg("指定的会话不存在");
            }
        }

        try {
            List<String> imageUrls = imageClient.generateImages(prompt);
            if (imageUrls.isEmpty()) {
                if (sessionInfo != null) {
                    saveImageHistory(sessionInfo, saasId, userId, userType, userInfo, configId, prompt,
                            "[ERROR] 图片生成返回的URL为空");
                }
                return ResponseData.errorMsg("图片生成返回的URL为空");
            }

            if (sessionInfo != null) {
                saveImageHistory(sessionInfo, saasId, userId, userType, userInfo, configId, prompt,
                        String.join(",", imageUrls));
            }

            AiImageResultData resultData = new AiImageResultData();
            resultData.setImageUrlList(imageUrls);
            resultData.setSessionId(sessionInfo != null ? sessionInfo.getId() : 0L);
            return ResponseData.success(resultData);
        } catch (Exception e) {
            logger.error("图片生成失败, configId={}, prompt={}", configId, prompt, e);
            if (sessionInfo != null) {
                saveImageHistory(sessionInfo, saasId, userId, userType, userInfo, configId, prompt,
                        "[ERROR] 图片生成失败：" + e.getMessage());
            }
            return ResponseData.errorMsg("图片生成失败：" + e.getMessage());
        }
    }

    /**
     * 保存图片生成历史到指定会话（仅在 sessionInfo 非空时调用）。
     *
     * <p>实现步骤：</p>
     * <ol>
     *   <li>[构造] AiChatService.initSessionMsg 构造消息对象（与 Chat 流程一致）</li>
     *   <li>[填充] 设置响应起止时间、token 用量（图片生成不计 token，置 0）、响应内容</li>
     *   <li>[保存] AiChatService.saveSessionMsg 写入 ai_session_msg 表</li>
     * </ol>
     *
     * @param sessionInfo 目标会话（非空）
     * @param saasId      租户ID
     * @param userId      用户ID
     * @param userType    用户类型
     * @param userInfo    用户信息
     * @param configId    模型配置ID
     * @param prompt      提示词
     * @param responseInfo 响应内容（成功为URL列表，失败为 [ERROR] 前缀消息）
     */
    private static void saveImageHistory(AiSessionInfo sessionInfo, long saasId, long userId, int userType,
                                         String userInfo, long configId, String prompt, String responseInfo) {
        AiSessionMsg sessionMsg = AiChatService.initSessionMsg(
                saasId, userId, userType, userInfo, configId, sessionInfo.getId(),
                null, prompt, null, null, null, null);
        sessionMsg.setRequestTokens(0);
        sessionMsg.setResponseTokens(0);
        sessionMsg.setResponseStartDate(SystemClock.nowDate());
        sessionMsg.setResponseEndDate(SystemClock.nowDate());
        sessionMsg.setResponseInfo(responseInfo);
        AiChatService.saveSessionMsg(sessionMsg);
    }
}
