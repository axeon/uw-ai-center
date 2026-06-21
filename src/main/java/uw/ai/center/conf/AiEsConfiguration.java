package uw.ai.center.conf;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch 配置类.
 * 将 Spring Boot 自动配置的 RestClient Bean 包装为 ElasticsearchClient Bean，供 AiRagService 使用。
 */
/**
 * Elasticsearch 配置类.
 * <p>将 Spring Boot 自动配置的 RestClient Bean 包装为 ElasticsearchClient Bean，供 {@code AiRagService} 做 RAG 向量与 BM25 检索使用。
 */
@Configuration
public class AiEsConfiguration {

    /**
     * 创建 ElasticsearchClient Bean.
     * 使用 Jackson 作为 JSON 映射器。
     *
     * @param restClient Spring Boot 自动配置的 RestClient
     * @return ElasticsearchClient 实例
     */
    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}
