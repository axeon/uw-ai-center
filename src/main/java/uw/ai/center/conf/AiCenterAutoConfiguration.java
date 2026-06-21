package uw.ai.center.conf;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * uw-ai-center 自动配置类。
 * <p>启用 {@link AiCenterProperties} 配置属性绑定，实现 {@link WebMvcConfigurer} 预留 Web 层定制扩展点。
 */
@EnableConfigurationProperties({AiCenterProperties.class})
@Configuration
public class AiCenterAutoConfiguration implements WebMvcConfigurer {


}