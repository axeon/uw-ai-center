package uw.ai.center.tool;

import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import uw.dao.DaoFactory;

/**
 * AiToolCallbackProvider。
 */
public class AiToolCallbackProvider implements ToolCallbackProvider {


    @Override
    public FunctionCallback[] getToolCallbacks() {
        return AiToolHelper.getToolCallbacks();
    }


}
