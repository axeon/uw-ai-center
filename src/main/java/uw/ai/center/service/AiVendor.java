package uw.ai.center.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.ai.chat.model.ChatModel;

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
     * PUB参数信息集合，所有人可见。
     */
    List<ConfigParam> pubicParam();

    /**
     * API参数信息集合，管理员可见。
     */
    List<ConfigParam> modelParam();

    /**
     * 日志类型参数信息集合，仅管理员可见。
     */
    List<ConfigParam> logParam();

    /**
     * 构造大模型信息。
     * @param modelId
     * @return
     */
    ChatModel buildModel(long modelId);

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
         * 配置描述。
         */
        private String desc;


        public ConfigParam() {
        }

        public ConfigParam(String key, String value, String desc) {
            this.key = key;
            this.value = value;
            this.desc = desc;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

    }
}
