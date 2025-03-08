package uw.ai.center.vendor.openai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
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
        return List.of(  );
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
        return null;
    }
}
