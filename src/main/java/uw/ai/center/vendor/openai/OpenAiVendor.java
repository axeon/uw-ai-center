package uw.ai.center.vendor.openai;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;
import uw.ai.center.advisor.AiChatLoggerAdvisor;
import uw.ai.center.advisor.AiSessionMemoryAdvisor;
import uw.ai.center.constant.TypeConfigData;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vo.AiModelConfigData;

import java.util.List;

/**
 * OllamaVendor。
 */
@Service
public class OpenAiVendor implements AiVendor {

    public OpenAiVendor() {
    }

    /**
     * 链接器名称
     */
    @Override
    public String vendorName() {
        return "OpenAi";
    }

    /**
     * 供应商描述
     */
    @Override
    public String vendorDesc() {
        return "OpenAi";
    }

    /**
     * 链接器版本
     */
    @Override
    public String vendorVersion() {
        return "1.0.0";
    }

    /**
     * 链接器图标
     */
    @Override
    public String vendorIcon() {
        return "";
    }

    /**
     * Vendor参数信息集合，管理员可见。
     */
    @Override
    public List<ConfigParam> vendorParam() {
        return List.of(
                new ConfigParam("api-path", "/v1/chat/completions",TypeConfigData.STRING.getValue(),"api路径","api路径"  ),
                new ConfigParam("temperature","0.8",TypeConfigData.FLOAT.getValue(),"temperature","要使用的采样温度，用于控制生成的完成项的明显创造性。较高的值将使输出更具随机性，而较低的值将使结果更加集中和确定。不建议为相同的completions请求修改 temperature 和top_p，因为这两个设置的交互很难预测。"),
                new ConfigParam("frequency-penalty","0.0f", TypeConfigData.FLOAT.getValue(),"frequency-penalty","介于 -2.0 和 2.0 之间的数字。正值会根据新标记到目前为止在文本中的现有频率来惩罚新标记，从而降低模型逐字重复同一行的可能性。"),
                new ConfigParam("max-completion-tokens","",TypeConfigData.INT.getValue(),"max-completion-tokens","可以为完成生成的标记数的上限，包括可见的输出标记和推理标记。"),
                new ConfigParam("n","1",TypeConfigData.INT.getValue(),"n","为每个输入消息生成多少个聊天完成选项。请注意，您将根据所有选项中生成的令牌数量付费。将n保留为 1 以最大限度地降低成本。"),
                new ConfigParam("store","FALSE",TypeConfigData.BOOLEAN.getValue(),"store","是否存储此聊天完成请求的输出以在我们的模型中使用"),
                new ConfigParam("output-modalities","",TypeConfigData.STRING.getValue(),"output-modalities","您希望模型为此请求生成的 Output types。大多数模型都能够生成文本，这是默认设置。gpt-4o-audio-preview模型也可用于生成音频。要请求此模型同时生成文本和音频响应，您可以使用：text，audio。不支持流式传输。"),
                new ConfigParam("output-audio","",TypeConfigData.STRING.getValue(),"output-audio","音频生成的音频参数。使用output-modalities：audio请求音频输出时是必需的。需要gpt-4o-audio-preview模型，并且不支持流式完成。"),
                new ConfigParam("presence-penalty","",TypeConfigData.FLOAT.getValue(),"presence-penalty","介于 -2.0 和 2.0 之间的数字。正值根据新标记到目前为止是否出现在文本中来惩罚新标记，从而增加模型讨论新主题的可能性。"),
                new ConfigParam("seed","",TypeConfigData.STRING.getValue(),"seed","此功能目前处于 Beta 阶段。如果指定，我们的系统将尽最大努力进行确定性采样，以便具有相同种子和参数的重复请求应返回相同的结果。"),
                new ConfigParam("stop","",TypeConfigData.STRING.getValue(),"stop","最多 4 个序列，API 将在其中停止生成更多令牌。"),
                new ConfigParam("top-p","",TypeConfigData.FLOAT.getValue(),"top-p","使用温度进行采样的替代方法，称为核采样，其中模型考虑具有top_p概率质量的标记的结果。所以 0.1 意味着只考虑包含前 10% 概率质量的 token。我们通常建议更改此温度或温度，但不能同时更改两者。"),
                new ConfigParam("tools","",TypeConfigData.SET_STRING.getValue(), "tools","模型可以调用的工具列表。目前，仅支持将函数作为工具。使用此函数可提供模型可能为其生成 JSON 输入的函数列表。"),
                new ConfigParam("tool-choice","",TypeConfigData.STRING.getValue(),"tool-choice","控制模型调用哪个 （如果有） 函数。none表示模型不会调用函数，而是生成一条消息。auto表示模型可以在生成消息或调用函数之间进行选择。指定特定函数会强制模型调用该函数。none是不存在函数时的默认值。如果存在函数，则 auto是默认值。"),
                new ConfigParam("user","",TypeConfigData.STRING.getValue(),"user","代表您的最终用户的唯一标识符，可以帮助 OpenAI 监控和检测滥用行为。"),
                new ConfigParam("parallel-tool-calls","true",TypeConfigData.BOOLEAN.getValue(),"parallel-tool-calls","是否在工具使用过程中启用并行函数调用。")
                );
    }

    /**
     * model参数信息集合，运营商可见。
     */
    @Override
    public List<ConfigParam> modelParam() {
        return List.of();
    }

    /**
     * embed参数信息集合，仅管理员可见。
     */
    @Override
    public List<ConfigParam> embedParam() {
        return List.of(
                new ConfigParam("api-path", "/v1/embeddings",TypeConfigData.STRING.getValue(),"api路径","api路径"  )
                );
    }

    /**
     * 构造模型实例。
     *
     * @param aiModelConfigData
     * @return
     */
    @Override
    public ChatClient buildChatClient(AiModelConfigData aiModelConfigData) {
        OpenAiApi openAiApi =  OpenAiApi.builder()
                .baseUrl(aiModelConfigData.getApiUrl())
                .apiKey(new SimpleApiKey(aiModelConfigData.getApiKey()))
                .completionsPath( aiModelConfigData.getVendorParam( "api-path" ) )
                .embeddingsPath( aiModelConfigData.getEmbedParam(  "api-path") )
                .build();
        OpenAiChatModel chatModel =  OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions( OpenAiChatOptions.builder().model( aiModelConfigData.getModelMain() ).build() )
                .build();
        return ChatClient.builder( chatModel )
                // 实现 Chat Memory 的 Advisor
                // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
                .defaultAdvisors( new MessageChatMemoryAdvisor( new AiSessionMemoryAdvisor(), "0:0", 10 ) )
                // 实现 Logger 的 Advisor
                .defaultAdvisors( new AiChatLoggerAdvisor() )
                .defaultOptions( OpenAiChatOptions.builder()
                        .temperature( aiModelConfigData.getVendorDoubleParam( "temperature") )
                        .frequencyPenalty( aiModelConfigData.getVendorDoubleParam( "frequency-penalty") )
                        .maxCompletionTokens( aiModelConfigData.getVendorIntParam( "max-completion-tokens") )
                        .N( aiModelConfigData.getVendorIntParam( "n") )
                        .store(aiModelConfigData.getVendorBooleanParam( "store" ))
                        .presencePenalty( aiModelConfigData.getVendorDoubleParam( "presence-penalty") )
                        .seed( aiModelConfigData.getVendorIntParam( "seed") )
                        .topP( aiModelConfigData.getVendorDoubleParam( "top-p") )
                        .streamUsage( true )
                        .parallelToolCalls( aiModelConfigData.getVendorBooleanParam( "parallel-tool-calls") )
                        .build())
                .build();
    }

    /**
     * 获取模型列表。
     *
     * @return
     */
    @Override
    public List<String> listModel(String apiUrl,String apiKey) {
        return List.of();
    }
}
