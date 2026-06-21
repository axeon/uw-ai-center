package uw.ai.center.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * uw-ai-center 配置属性。
 * <p>配置前缀：{@code uw.ai.center}，当前无自定义属性，预留用于后续扩展（如默认模型、限流阈值等）。
 */
@ConfigurationProperties(prefix = "uw.ai.center")
public class AiCenterProperties {

}
