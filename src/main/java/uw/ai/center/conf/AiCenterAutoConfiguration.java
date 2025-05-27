package uw.ai.center.conf;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * uw-auth-center 自动配置类
 */
@EnableConfigurationProperties({AiCenterProperties.class})
@Configuration
public class AiCenterAutoConfiguration implements WebMvcConfigurer {


}