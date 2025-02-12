package uw.ai.center.service.deepseek;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.stereotype.Service;
import uw.ai.center.service.AiVendor;

import java.util.List;

/**
 * OllamaVendor。
 */
@Service
public class DeepSeekVendor implements AiVendor {

    /**
     * 链接器名称
     */
    @Override
    public String vendorName() {
        return "DeepSeek";
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
     * 构造供应商信息。
     *
     * @param configId
     * @return
     */
    @Override
    public ChatModel buildModel(long configId) {
        return null;
    }
}
