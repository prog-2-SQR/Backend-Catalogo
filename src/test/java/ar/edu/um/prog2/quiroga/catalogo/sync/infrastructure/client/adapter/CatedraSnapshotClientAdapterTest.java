package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.adapter;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.serviceUnavailable;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ar.edu.um.prog2.quiroga.catalogo.config.CatedraClientConfig;
import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogSnapshot;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogWeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogSnapshotClient;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.mapper.CatalogSnapshotDtoMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.http.client.autoconfigure.HttpClientAutoConfiguration;
import org.springframework.boot.restclient.autoconfigure.RestClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.io.ClassPathResource;

class CatedraSnapshotClientAdapterTest {

    private static final String JWT = "jwt-de-prueba";
    private static final String PATH = CatedraSnapshotClientAdapter.SNAPSHOT_PATH;

    // WireMock hace de API de la cátedra en un puerto libre.
    @RegisterExtension
    static WireMockExtension catedra = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(HttpClientAutoConfiguration.class, RestClientAutoConfiguration.class))
            .withUserConfiguration(CatedraClientConfig.class)
            .withBean(CatalogSnapshotDtoMapper.class)
            .withBean(CatedraSnapshotClientAdapter.class)
            .withPropertyValues(
                    "catedra.api-url=" + catedra.baseUrl(),
                    "catedra.jwt=" + JWT,
                    "catedra.retry-delay=10ms");

    @Test
    void fetchesTheRealSnapshotWithTheTechnicalJwt() throws IOException {
        catedra.stubFor(get(PATH).willReturn(okJson(fixture())));

        contextRunner.run(context -> {
            CatalogSnapshot snapshot = context.getBean(CatalogSnapshotClient.class).fetchSnapshot();

            assertThat(snapshot.getSnapshotVersion()).isEqualTo(1L);
            assertThat(snapshot.getCategories()).hasSize(8);
            assertThat(snapshot.getProfessionals()).hasSize(24);
            assertThat(snapshot.getWeeklySchedules()).hasSize(44);
            CatalogWeeklySchedule first = snapshot.getWeeklySchedules().getFirst();
            assertThat(first.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
            assertThat(first.getStartTime()).isEqualTo(LocalTime.of(12, 0));
        });
        catedra.verify(1, getRequestedFor(urlEqualTo(PATH)).withHeader("Authorization", equalTo("Bearer " + JWT)));
    }

    @Test
    void acceptsShortTimesAndIgnoresUnknownFields() {
        catedra.stubFor(get(PATH).willReturn(okJson("""
                {"snapshotVersion": 9, "campoNuevo": "x",
                 "weeklySchedules": [{"id": 1, "professionalId": 2, "dayOfWeek": "FRIDAY",
                                      "startTime": "09:00", "endTime": "12:30", "slotDurationMinutes": 30}]}
                """)));

        contextRunner.run(context -> {
            CatalogSnapshot snapshot = context.getBean(CatalogSnapshotClient.class).fetchSnapshot();

            assertThat(snapshot.getSnapshotVersion()).isEqualTo(9L);
            assertThat(snapshot.getCategories()).isEmpty();
            assertThat(snapshot.getWeeklySchedules().getFirst().getEndTime()).isEqualTo(LocalTime.of(12, 30));
        });
    }

    @Test
    void retriesWhenTheCatedraIsTemporarilyUnavailable() {
        catedra.stubFor(get(PATH).inScenario("caida").whenScenarioStateIs(STARTED)
                .willReturn(serviceUnavailable()).willSetStateTo("recuperada"));
        catedra.stubFor(get(PATH).inScenario("caida").whenScenarioStateIs("recuperada")
                .willReturn(okJson("{\"snapshotVersion\": 3}")));

        contextRunner.run(context ->
                assertThat(context.getBean(CatalogSnapshotClient.class).fetchSnapshot().getSnapshotVersion()).isEqualTo(3L));
        catedra.verify(2, getRequestedFor(urlEqualTo(PATH)));
    }

    @Test
    void givesUpAfterTheConfiguredRetries() {
        catedra.stubFor(get(PATH).willReturn(serviceUnavailable()));

        contextRunner.run(context ->
                assertThatThrownBy(() -> context.getBean(CatalogSnapshotClient.class).fetchSnapshot())
                        .isInstanceOf(SyncException.class));
        catedra.verify(3, getRequestedFor(urlEqualTo(PATH)));
    }

    @Test
    void doesNotRetryWhenTheJwtIsRejected() {
        catedra.stubFor(get(PATH).willReturn(unauthorized()));

        contextRunner.run(context ->
                assertThatThrownBy(() -> context.getBean(CatalogSnapshotClient.class).fetchSnapshot())
                        .isInstanceOf(SyncException.class));
        catedra.verify(1, getRequestedFor(urlEqualTo(PATH)));
    }

    @Test
    void failsWhenTheResponseHasNoBody() {
        catedra.stubFor(get(PATH).willReturn(aResponse().withStatus(200)));

        contextRunner.run(context ->
                assertThatThrownBy(() -> context.getBean(CatalogSnapshotClient.class).fetchSnapshot())
                        .isInstanceOf(SyncException.class)
                        .hasMessageContaining("sin contenido"));
    }

    private static String fixture() throws IOException {
        return new ClassPathResource("sync/snapshot-catedra.json").getContentAsString(StandardCharsets.UTF_8);
    }
}
