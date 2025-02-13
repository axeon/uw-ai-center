package uw.ai.center.vendor.ollama;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.ollama.management.PullModelStrategy;
import org.springframework.stereotype.Service;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vo.AiModelConfigData;

import java.time.Duration;
import java.util.List;

/**
 * OllamaVendor。
 */
@Service
public class OllamaVendor implements AiVendor {

    public OllamaVendor() {
        OllamaApi api = new OllamaApi();
//        OllamaChatModel model = new OllamaChatModel(api,"deepseek-r1:7b");
    }

    /**
     * 链接器名称
     */
    @Override
    public String vendorName() {
        return "Ollama";
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
        return List.of( new ConfigParam( "", "", "" ), new ConfigParam( "", "", "" ), new ConfigParam( "", "", "" ) );
    }

    /**
     * model参数信息集合，运营商可见。
     */
    @Override
    public List<ConfigParam> modelParam() {
        return List.of( new ConfigParam( "server", "http://localhost:11434", "服务器地址" ) );
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
        OllamaApi ollamaApi = new OllamaApi( aiModelConfigData.getApiUrl() );
        if (ollamaApi != null) {
            OllamaChatModel chatModel = OllamaChatModel.builder().ollamaApi( ollamaApi ).modelManagementOptions( new ModelManagementOptions( PullModelStrategy.NEVER,
                    List.of( aiModelConfigData.getModelMain() ), Duration.ofSeconds( 0 ), 3 ) ).build();
            return ChatClient.builder( chatModel )
                    // 实现 Chat Memory 的 Advisor
                    // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
                    .defaultAdvisors( new MessageChatMemoryAdvisor( new InMemoryChatMemory() ) )
                    // 实现 Logger 的 Advisor
                    .defaultAdvisors( new SimpleLoggerAdvisor() )
                    // 设置 ChatClient 中 ChatModel 的 Options 参数
                    .defaultOptions( OllamaOptions.builder().topP( 0.7 ).model( aiModelConfigData.getModelMain() ).build() ).build();
        }
        return null;
    }
}
