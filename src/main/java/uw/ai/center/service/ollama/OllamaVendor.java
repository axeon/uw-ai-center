package uw.ai.center.service.ollama;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.ollama.management.PullModelStrategy;
import org.springframework.stereotype.Service;
import uw.ai.center.service.AiVendor;
import uw.ai.center.vo.AiModelConfigData;

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
     * PUB参数信息集合，所有人可见。
     */
    @Override
    public List<ConfigParam> pubicParam() {
        return List.of( new ConfigParam( "", "", "" ),
                new ConfigParam( "", "", "" ),
                new ConfigParam( "", "", "" ) );
    }

    /**
     * API参数信息集合，运营商可见。
     */
    @Override
    public List<ConfigParam> modelParam() {
        return List.of(new ConfigParam( "server", "http://localhost:11434", "服务器地址"));
    }

    /**
     * 日志类型参数信息集合，仅管理员可见。
     */
    @Override
    public List<ConfigParam> logParam() {
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
        OllamaApi ollamaApi = new OllamaApi(aiModelConfigData.getModelParam( "server" ));
        if (ollamaApi != null) {
//            OllamaChatModel chatModel = OllamaChatModel.builder().ollamaApi(ollamaApi).defaultOptions(properties.getOptions()).functionCallbackResolver(functionCallbackResolver).toolFunctionCallbacks(toolFunctionCallbacks).observationRegistry((ObservationRegistry)observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP)).modelManagementOptions(new ModelManagementOptions(chatModelPullStrategy, initProperties.getChat().getAdditionalModels(), initProperties.getTimeout(), initProperties.getMaxRetries())).build();
            OllamaChatModel chatModel = OllamaChatModel.builder().ollamaApi(ollamaApi).build();
//            return chatModel;
        }
        return null;
    }
}
