package ar.edu.um.prog2.quiroga.catalogo.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

// RestClient hacia la API de la cátedra: URL base, JWT técnico en cada request y timeouts acotados.
@Configuration
@EnableConfigurationProperties(CatedraProperties.class)
public class CatedraClientConfig {

    @Bean
    public RestClient catedraRestClient(RestClient.Builder builder, CatedraProperties properties) {
        HttpClientSettings settings = HttpClientSettings.defaults()
                .withTimeouts(properties.getConnectTimeout(), properties.getReadTimeout());
        return builder
                .baseUrl(properties.getApiUrl())
                .defaultHeaders(headers -> headers.setBearerAuth(properties.getJwt()))
                .requestFactory(ClientHttpRequestFactoryBuilder.detect().build(settings))
                .build();
    }
}
