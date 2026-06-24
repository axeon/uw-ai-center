package uw.ai.center.conf;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uw.ai.center.service.AiRagService;

/**
 * Elasticsearch 配置类.
 * <p>将 Spring Boot 自动配置的 RestClient Bean 包装为 ElasticsearchClient Bean，供 {@code AiRagService} 做 RAG 向量与 BM25 检索使用。
 * <p>同时通过 {@link RestClientBuilderCustomizer} 显式设置 connect/socket 超时，避免上游 ES 慢响应时调用方无限挂起。
 * <p>{@link AiRagService} 是纯静态工具类（无 @Service 注解），ES Client 通过 {@link AiRagService#setEsClient} 在 Bean 创建时注入静态字段。
 */
@Configuration
public class AiEsConfiguration {

    /**
     * 创建 ElasticsearchClient Bean.
     * 使用 Jackson 作为 JSON 映射器，并同步注入到 {@link AiRagService} 的静态字段。
     *
     * @param restClient Spring Boot 自动配置的 RestClient
     * @return ElasticsearchClient 实例
     */
    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);
        AiRagService.setEsClient(client);
        return client;
    }

    /**
     * ES RestClient 超时定制：显式设置 connect/socket timeout。
     * <p>超时值通过 {@code uw.ai.center.es.connect-timeout-ms} / {@code uw.ai.center.es.socket-timeout-ms} 配置项覆盖，
     * 默认 connect=5s / socket=60s（兜底，防止未配置时无限挂起）。
     *
     * @param connectTimeoutMs 连接超时（毫秒）
     * @param socketTimeoutMs  socket 超时（毫秒）
     * @return Spring Boot 自动配置会自动应用的 customizer
     */
    @Bean
    public RestClientBuilderCustomizer esTimeoutCustomizer(
            @Value("${uw.ai.center.es.connect-timeout-ms:5000}") int connectTimeoutMs,
            @Value("${uw.ai.center.es.socket-timeout-ms:60000}") int socketTimeoutMs) {
        return builder -> builder.setRequestConfigCallback(rc -> rc
                .setConnectTimeout(connectTimeoutMs)
                .setSocketTimeout(socketTimeoutMs));
    }
}
