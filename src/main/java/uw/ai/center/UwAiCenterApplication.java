package uw.ai.center;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

@SpringBootApplication
@EnableDiscoveryClient
class UwAiCenterApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder( UwAiCenterApplication.class ).beanNameGenerator( (beanDefinition, beanDefinitionRegistry) -> {
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName.contains( "uw.ai" )) {
                return beanClassName;
            }

            if (beanClassName.endsWith( "LoadBalancerAutoConfiguration" )) {
                return beanClassName;
            }

            return new AnnotationBeanNameGenerator().generateBeanName( beanDefinition, beanDefinitionRegistry );
        } ).run( args );

    }
}
