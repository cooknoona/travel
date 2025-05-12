package com.travel.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;

import java.util.Objects;

/** Elasticsearch config, First try without credentials. If user wants, can check with credentials */
public class ElasticsearchConfig {
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        String elasticsearchUri = dotenv.get("ELASTIC_END_POINT", System.getenv("ELASTIC_END_POINT"));
        String username = dotenv.get("ELASTIC_USERNAME", System.getenv("ELASTIC_USERNAME"));
        String password = dotenv.get("ELASTIC_PASSWORD", System.getenv("ELASTIC_PASSWORD"));

        if (Objects.equals(username, "") && Objects.equals(password, "")){
            RestClientBuilder restClientBuilder = RestClient.builder(HttpHost.create(elasticsearchUri));
            RestClient restClient = restClientBuilder.build();
            ElasticsearchTransport transport = new RestClientTransport(
                    restClient, new JacksonJsonpMapper());
            return new ElasticsearchClient(transport);
        }
        else {
            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

            RestClientBuilder restClientBuilder = RestClient.builder(HttpHost.create(elasticsearchUri))
                    .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

            RestClient restClient = restClientBuilder.build();
            ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
            return new ElasticsearchClient(transport);
        }
    }
}
