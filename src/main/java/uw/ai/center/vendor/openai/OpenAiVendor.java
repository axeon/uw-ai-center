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
        return List.of( new ConfigParam( "apiPath", "/v1/chat/completions","String","api路径","api路径"  ) );
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
        return List.of();
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
                .completionsPath( "/chat/completions" )
                .build();
        OpenAiChatModel chatModel =  OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions( OpenAiChatOptions.builder().model( aiModelConfigData.getModelMain() ).build() )
                .build();

        return ChatClient.builder( chatModel )
                .defaultSystem( "你是一个AI智能助理。" )
                // 实现 Chat Memory 的 Advisor
                // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
                .defaultAdvisors( new MessageChatMemoryAdvisor( new AiSessionMemoryAdvisor(), "0:0", 10 ) )
                // 实现 Logger 的 Advisor
                .defaultAdvisors( new AiChatLoggerAdvisor() )
                .defaultOptions( OllamaOptions.builder().build())
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
