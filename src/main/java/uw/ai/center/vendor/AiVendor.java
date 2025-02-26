package uw.ai.center.vendor;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.ai.chat.client.ChatClient;
import uw.ai.center.vo.AiModelConfigData;

import java.util.List;

/**
 * Ai供应商接口。
 */
public interface AiVendor {

    /**
     * 供应商名称
     */
    String vendorName();

    /**
     * 供应商版本
     */
    String vendorVersion();

    /**
     * 供应商图标
     */
    String vendorIcon();

    /**
     * 供应商类名
     */
    default String vendorClass() {
        return this.getClass().getSimpleName();
    }

    /**
     * Vendor参数信息集合，管理员可见。
     */
    List<ConfigParam> vendorParam();

    /**
     * model参数信息集合，管理员可见。
     */
    List<ConfigParam> modelParam();

    /**
     * embed参数信息集合，仅管理员可见。
     */
    List<ConfigParam> embedParam();

    /**
     * 构造模型实例。
     * @param aiModelConfigData
     * @return
     */
    ChatClient buildChatClient(AiModelConfigData aiModelConfigData);

    /**
     * 配置信息解析类
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    class ConfigParam {

        /**
         * 配置参数名。
         */
        private String key;

        /**
         * 配置默认值。
         */
        private String value;

        /**
         * 配置类型。 数值，字符串，布尔值，浮点数，浮点数，日期，时间，日期时间,枚举.
         */
        private String type;

        /**
         * 配置名称。
         */
        private String name;

        /**
         * 配置描述。
         */
        private String desc;

        public ConfigParam() {
        }

        public ConfigParam(String key, String value, String type, String name, String desc) {
            this.key = key;
            this.value = value;
            this.type = type;
            this.name = name;
            this.desc = desc;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }
    }
}
