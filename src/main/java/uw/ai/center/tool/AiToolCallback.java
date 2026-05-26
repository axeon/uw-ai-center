package uw.ai.center.tool;

import dev.langchain4j.agent.tool.ToolSpecification;
import uw.ai.center.entity.AiToolInfo;
import uw.common.util.JsonUtils;

/**
 * AI工具回调（用于 RPC 转发工具调用到外部微服务）。
 */
public class AiToolCallback {

    private final AiToolInfo aiToolInfo;

    private final boolean returnDirect;

    public AiToolCallback(AiToolInfo aiToolInfo, boolean returnDirect) {
        this.aiToolInfo = aiToolInfo;
        this.returnDirect = returnDirect;
    }

    public AiToolInfo getAiToolInfo() {
        return aiToolInfo;
    }

    public boolean isReturnDirect() {
        return returnDirect;
    }

    /**
     * 生成 LangChain4j ToolSpecification。
     */
    public ToolSpecification toToolSpecification() {
        return ToolSpecification.builder()
                .name(aiToolInfo.getAppName() + "/" + aiToolInfo.getToolClass())
                .description(aiToolInfo.getToolDesc())
                .build();
    }

    /**
     * 通过 RPC 执行工具调用。
     */
    public String execute(String toolInput) {
        return JsonUtils.toString(AiToolHelper.toolCallback(aiToolInfo, toolInput));
    }
}
