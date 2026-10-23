package ar.edu.um.prog2.quiroga.catalogo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryListener;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryState;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.core.retry.Retryable;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

// RestClient hacia la API de la cátedra: URL base, JWT técnico en cada request y timeouts acotados.
@Slf4j
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

    // Solo se reintenta lo que puede resolverse solo: la cátedra no responde (timeout) o falla del lado servidor (5xx).
    // Un 4xx (por ejemplo, JWT rechazado) no cambia por repetirlo.
    @Bean
    public RetryTemplate catedraRetryTemplate(CatedraProperties properties) {
        RetryPolicy policy = RetryPolicy.builder()
                .includes(ResourceAccessException.class, HttpServerErrorException.class)
                .maxRetries(properties.getMaxRetries())
                .delay(properties.getRetryDelay())
                .multiplier(2)
                .build();
        RetryTemplate retryTemplate = new RetryTemplate(policy);
        retryTemplate.setRetryListener(new LoggingRetryListener());
        return retryTemplate;
    }

    // Deja cada reintento en el log, para que sean observables (anexo §18.4).
    static class LoggingRetryListener implements RetryListener {

        @Override
        public void beforeRetry(RetryPolicy policy, Retryable<?> retryable, RetryState state) {
            log.warn("Reintento {} hacia la cátedra después de: {}",
                    state.getRetryCount(), state.getLastException().getMessage());
        }
    }
}
