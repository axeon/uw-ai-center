package uw.ai.center.tool;

import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.tool.ToolCallbackProvider;

/**
 * AiToolCallbackProvider。
 */
public class AiToolCallbackProvider implements ToolCallbackProvider {


    @Override
    public FunctionCallback[] getToolCallbacks() {
        return AiToolHelper.getAllToolCallbacks();
    }


}
