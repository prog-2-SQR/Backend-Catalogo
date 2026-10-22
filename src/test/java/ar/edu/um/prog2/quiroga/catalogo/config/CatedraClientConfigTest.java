package ar.edu.um.prog2.quiroga.catalogo.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.http.client.autoconfigure.HttpClientAutoConfiguration;
import org.springframework.boot.restclient.autoconfigure.RestClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

class CatedraClientConfigTest {

    private static final String JWT = "jwt-de-prueba";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(HttpClientAutoConfiguration.class, RestClientAutoConfiguration.class))
            .withUserConfiguration(CatedraClientConfig.class);

    private final AtomicReference<String> receivedAuthorization = new AtomicReference<>();
    private final AtomicReference<String> receivedPath = new AtomicReference<>();
    private HttpServer server;

    // Servidor HTTP mínimo del JDK que hace de cátedra: guarda lo que recibe y responde "ok".
    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", exchange -> {
            receivedAuthorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            receivedPath.set(exchange.getRequestURI().getPath());
            byte[] body = "ok".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.createContext("/lento", exchange -> {
            sleep(Duration.ofSeconds(2));
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    @Test
    void sendsRequestsToApiUrlWithTechnicalJwt() {
        contextRunner
                .withPropertyValues("catedra.api-url=" + serverUrl(), "catedra.jwt=" + JWT)
                .run(context -> {
                    String body = context.getBean(RestClient.class).get().uri("/api/ping").retrieve().body(String.class);

                    assertThat(body).isEqualTo("ok");
                    assertThat(receivedPath.get()).isEqualTo("/api/ping");
                    assertThat(receivedAuthorization.get()).isEqualTo("Bearer " + JWT);
                });
    }

    @Test
    void usesDefaultTimeoutsWhenNotConfigured() {
        contextRunner
                .withPropertyValues("catedra.api-url=" + serverUrl(), "catedra.jwt=" + JWT)
                .run(context -> {
                    CatedraProperties properties = context.getBean(CatedraProperties.class);

                    assertThat(properties.getConnectTimeout()).isEqualTo(Duration.ofSeconds(5));
                    assertThat(properties.getReadTimeout()).isEqualTo(Duration.ofSeconds(10));
                });
    }

    @Test
    void failsWhenServerTakesLongerThanReadTimeout() {
        contextRunner
                .withPropertyValues("catedra.api-url=" + serverUrl(), "catedra.jwt=" + JWT, "catedra.read-timeout=200ms")
                .run(context -> {
                    RestClient restClient = context.getBean(RestClient.class);

                    assertThatThrownBy(() -> restClient.get().uri("/lento").retrieve().toBodilessEntity())
                            .isInstanceOf(ResourceAccessException.class);
                });
    }

    @Test
    void doesNotStartWithoutJwt() {
        contextRunner
                .withPropertyValues("catedra.api-url=" + serverUrl(), "catedra.jwt=")
                .run(context -> assertThat(context).hasFailed()
                        .getFailure().rootCause().hasMessageContaining("catedra.jwt"));
    }

    @Test
    void doesNotStartWithoutApiUrl() {
        contextRunner
                .withPropertyValues("catedra.jwt=" + JWT)
                .run(context -> assertThat(context).hasFailed()
                        .getFailure().rootCause().hasMessageContaining("catedra.apiUrl"));
    }

    private String serverUrl() {
        return "http://localhost:" + server.getAddress().getPort();
    }

    private static void sleep(Duration duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
