package uw.ai.center.tool;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.DefaultToolDefinition;
import org.springframework.ai.tool.definition.ToolDefinition;
import uw.ai.center.entity.AiToolInfo;

/**
 * AiToolCallback实现。
 */
public class AiToolCallback implements ToolCallback {

    /**
     * The tool metadata used to identify the tool.
     */
    private final AiToolInfo aiToolInfo;

    public AiToolCallback(AiToolInfo aiToolInfo) {
        this.aiToolInfo = aiToolInfo;
    }

    /**
     * The tool metadata used to identify the tool.
     */
    public AiToolInfo getAiToolInfo() {
        return aiToolInfo;
    }

    /**
     * Definition used by the AI model to determine when and how to call the tool.
     */
    @Override
    public ToolDefinition getToolDefinition() {
        return DefaultToolDefinition.builder().name( aiToolInfo.getAppName()+"/"+aiToolInfo.getToolClass() ).description( aiToolInfo.getToolDesc() ).inputSchema( aiToolInfo.getToolInput() ).build();
    }

    /**
     * Execute tool with the given input and return the result to send back to the AI
     * model.
     *
     * @param toolInput
     */
    @Override
    public String call(String toolInput) {
        return AiToolHelper.toolCallback(aiToolInfo,toolInput).toString();
    }
}
