package uw.ai.center.tool;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.DefaultToolDefinition;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import uw.ai.center.entity.AiToolInfo;
import uw.common.util.JsonUtils;

import java.util.Map;

/**
 * AiToolCallback实现。
 */
public class AiToolCallback implements ToolCallback {

    /**
     * The tool metadata used to identify the tool.
     */
    private final AiToolInfo aiToolInfo;

    /**
     * 是否直接返回结果，不返回提示。
     */
    private final boolean returnDirect;

    public AiToolCallback(AiToolInfo aiToolInfo,boolean returnDirect) {
        this.aiToolInfo = aiToolInfo;
        this.returnDirect = returnDirect;
    }

    /**
     * The tool metadata used to identify the tool.
     */
    public AiToolInfo getAiToolInfo() {
        return aiToolInfo;
    }

    /**
     * 是否直接返回结果，不返回提示。
     * @return
     */
    public boolean isReturnDirect() {
        return returnDirect;
    }

    /**
     * Definition used by the AI model to determine when and how to call the tool.
     */
    @Override
    public ToolDefinition getToolDefinition() {
        return DefaultToolDefinition.builder().name( aiToolInfo.getAppName() + "/" + aiToolInfo.getToolClass() ).description( aiToolInfo.getToolDesc() ).inputSchema( aiToolInfo.getToolInput() ).build();
    }

    /**
     * Metadata providing additional information on how to handle the tool.
     */
    @Override
    public ToolMetadata getToolMetadata() {
        return ToolMetadata.builder().returnDirect( returnDirect ).build();
    }

    /**
     * Execute tool with the given input and return the result to send back to the AI
     * model.
     *
     * @param toolInput
     */
    @Override
    public String call(String toolInput) {
        return JsonUtils.toString( AiToolHelper.toolCallback( aiToolInfo, toolInput ) );
    }

    /**
     * Execute tool with the given input and context, and return the result to send back
     * to the AI model.
     *
     * @param toolInput
     * @param toolContext
     */
    @Override
    public String call(String toolInput, ToolContext toolContext) {
        Map<String, Object> map = JsonUtils.parse( toolInput, new TypeReference<Map<String, Object>>() {
        } );
        //合并上下文，为了防止用户通过toolInput偷偷注入信息，必须用toolContext覆盖。
        if (toolContext != null) {
            map.putAll( toolContext.getContext() );
        }
        toolInput =  JsonUtils.toString( map );
        return call( toolInput );
    }
}
