package uw.ai.center.tool;

import dev.langchain4j.agent.tool.ToolSpecification;
import uw.ai.center.entity.AiToolInfo;
import uw.common.util.JsonUtils;

/**
 * AI工具回调（用于 RPC 转发工具调用到外部微服务）。
 * <p>封装 {@link AiToolInfo}，提供生成 LangChain4j ToolSpecification 与执行工具调用的能力。
 */
public class AiToolCallback {

    /** 工具信息。 */
    private final AiToolInfo aiToolInfo;

    /** 是否直接返回工具结果（不再交由模型继续生成）。 */
    private final boolean returnDirect;

    /**
     * 构造工具回调。
     *
     * @param aiToolInfo  工具信息
     * @param returnDirect 是否直接返回工具结果
     */
    public AiToolCallback(AiToolInfo aiToolInfo, boolean returnDirect) {
        this.aiToolInfo = aiToolInfo;
        this.returnDirect = returnDirect;
    }

    /**
     * 获取工具信息。
     *
     * @return 工具信息
     */
    public AiToolInfo getAiToolInfo() {
        return aiToolInfo;
    }

    /**
     * 是否直接返回工具结果。
     *
     * @return true=直接返回
     */
    public boolean isReturnDirect() {
        return returnDirect;
    }

    /**
     * 生成 LangChain4j ToolSpecification。
     * <p>工具名为 "appName/toolClass"，描述取自 aiToolInfo.toolDesc。
     *
     * @return ToolSpecification
     */
    public ToolSpecification toToolSpecification() {
        return ToolSpecification.builder()
                .name(aiToolInfo.getAppName() + "/" + aiToolInfo.getToolClass())
                .description(aiToolInfo.getToolDesc())
                .build();
    }

    /**
     * 通过 RPC 执行工具调用。
     *
     * @param toolInput 工具入参 JSON
     * @return 工具执行结果 JSON 字符串
     */
    public String execute(String toolInput) {
        return JsonUtils.toString(AiToolHelper.toolCallback(aiToolInfo, toolInput));
    }
}
