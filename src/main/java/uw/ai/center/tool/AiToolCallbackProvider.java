package uw.ai.center.tool;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;

/**
 * AiToolCallbackProvider。
 */
public class AiToolCallbackProvider implements ToolCallbackProvider {


    @Override
    public ToolCallback[] getToolCallbacks() {
        return AiToolHelper.getAllToolCallbacks();
    }


}
