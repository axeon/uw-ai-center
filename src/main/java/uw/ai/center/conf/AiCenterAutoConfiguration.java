package uw.ai.center.conf;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * uw-auth-center 自动配置类
 */
@EnableConfigurationProperties({AiCenterProperties.class})
@Configuration
public class AiCenterAutoConfiguration implements WebMvcConfigurer {

    /**
     * 移除XML消息转换器
     *
     * @param converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf( x -> x instanceof MappingJackson2XmlHttpMessageConverter );
    }

}