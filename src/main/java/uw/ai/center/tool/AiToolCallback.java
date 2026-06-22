package uw.ai.center.tool;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.ai.center.entity.AiToolInfo;
import uw.common.util.JsonUtils;

/**
 * AI工具回调（用于 RPC 转发工具调用到外部微服务）。
 * <p>封装 {@link AiToolInfo}，提供生成 LangChain4j ToolSpecification 与执行工具调用的能力。
 */
public class AiToolCallback {

    private static final Logger logger = LoggerFactory.getLogger(AiToolCallback.class);

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
     * <p>参数 schema 取自 aiToolInfo.toolInput（JSON Schema object，含 type/properties/required 等），
     * 解析失败时降级为无参数工具，仅记录日志不阻断流程。
     *
     * @return ToolSpecification
     */
    public ToolSpecification toToolSpecification() {
        ToolSpecification.Builder builder = ToolSpecification.builder()
                .name(aiToolInfo.getAppName() + "/" + aiToolInfo.getToolClass())
                .description(aiToolInfo.getToolDesc());
        String toolInput = aiToolInfo.getToolInput();
        if (StringUtils.isNotBlank(toolInput)) {
            try {
                // toolInput 存储的是参数 JSON Schema（type:object），嵌入到完整 ToolSpecification JSON 中借助 fromJson 反序列化
                ToolSpecification parsed = ToolSpecification.fromJson("{\"parameters\":" + toolInput + "}");
                JsonObjectSchema parameters = parsed.parameters();
                if (parameters != null) {
                    builder.parameters(parameters);
                }
            } catch (Exception e) {
                logger.warn("解析工具参数schema失败, toolName={}, toolInput={}",
                        aiToolInfo.getAppName() + "/" + aiToolInfo.getToolClass(), toolInput, e);
            }
        }
        return builder.build();
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
