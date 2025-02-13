package uw.ai.center.service.openai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.stereotype.Service;
import uw.ai.center.service.AiVendor;
import uw.ai.center.vo.AiModelConfigData;

import java.util.List;

/**
 * OllamaVendor。
 */
@Service
public class OpenAiVendor implements AiVendor {

    public OpenAiVendor() {
        OllamaApi api = new OllamaApi();
//        OllamaChatModel model = new OllamaChatModel(api,"deepseek-r1:7b");
    }

    /**
     * 链接器名称
     */
    @Override
    public String vendorName() {
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
        return List.of();
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
        return null;
    }
}
