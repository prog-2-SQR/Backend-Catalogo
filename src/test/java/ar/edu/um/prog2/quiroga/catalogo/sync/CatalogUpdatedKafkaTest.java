package ar.edu.um.prog2.quiroga.catalogo.sync;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;

import ar.edu.um.prog2.quiroga.catalogo.TestcontainersConfiguration;
import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.SynchronizeChangesUseCase;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Del topic a la base: Kafka y PostgreSQL reales, listener prendido. La sincronización (Redis/REST) se reemplaza
 * por un mock para contar cuántas veces la dispara cada aviso.
 */
@SpringBootTest(properties = {"catedra.api-url=http://localhost:8090", "catedra.jwt=jwt-de-prueba",
        "catedra.sync-on-startup=false", "catedra.sync-polling=false",
        "spring.kafka.listener.auto-startup=true", "catedra.retry-delay=100ms"})
@Import({TestcontainersConfiguration.class, CatalogUpdatedKafkaTest.KafkaConfiguration.class})
class CatalogUpdatedKafkaTest {

    private static final String TOPIC = "catedra.catalog.test";
    private static final Duration WAIT = Duration.ofSeconds(30);

    @TestConfiguration(proxyBeanMethods = false)
    static class KafkaConfiguration {

        @Bean
        @ServiceConnection
        KafkaContainer kafkaContainer() {
            return new KafkaContainer(DockerImageName.parse("apache/kafka-native:4.1.1"));
        }

        // KafkaAdmin crea el topic al arrancar, antes de que el listener se suscriba.
        @Bean
        NewTopic catalogTopic() {
            return new NewTopic(TOPIC, 1, (short) 1);
        }
    }

    @MockitoBean
    private SynchronizeChangesUseCase synchronizeChangesUseCase;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void synchronizationSucceeds() {
        when(synchronizeChangesUseCase.synchronizeChanges()).thenReturn(SyncState.builder().snapshotVersion(7L).build());
    }

    @AfterEach
    void clean() {
        jdbcTemplate.execute("TRUNCATE sync_processed_events");
    }

    @Test
    void repeatedEventIdSynchronizesOnlyOnce() {
        publish("7", catalogUpdated("catalog-event-7-4f3b", 7));
        publish("7", catalogUpdated("catalog-event-7-4f3b", 7));
        publish("8", catalogUpdated("catalog-event-8-9a1c", 8));

        // Una partición: el aviso 8 llega después del repetido, así que al procesarlo el repetido ya pasó.
        verify(synchronizeChangesUseCase, timeout(WAIT.toMillis()).times(2)).synchronizeChanges();
        await().atMost(WAIT).untilAsserted(() ->
                assertThat(processedEventIds()).containsExactlyInAnyOrder("catalog-event-7-4f3b", "catalog-event-8-9a1c"));
    }

    @Test
    void unusableMessageDoesNotBlockTheTopic() {
        publish("9", "no es json");
        publish("9", catalogUpdated("catalog-event-9-77de", 9));

        verify(synchronizeChangesUseCase, timeout(WAIT.toMillis()).times(1)).synchronizeChanges();
        await().atMost(WAIT).untilAsserted(() -> assertThat(processedEventIds()).containsExactly("catalog-event-9-77de"));
    }

    @Test
    void failedSynchronizationIsRetriedAndThenRecorded() {
        when(synchronizeChangesUseCase.synchronizeChanges())
                .thenThrow(new SyncException("cátedra caída"))
                .thenReturn(SyncState.builder().snapshotVersion(10L).build());

        publish("10", catalogUpdated("catalog-event-10-c3a0", 10));

        verify(synchronizeChangesUseCase, timeout(WAIT.toMillis()).times(2)).synchronizeChanges();
        await().atMost(WAIT).untilAsserted(() -> assertThat(processedEventIds()).containsExactly("catalog-event-10-c3a0"));
    }

    // Message key: la versión como texto (anexo §15.1).
    private void publish(String key, String payload) {
        kafkaTemplate.send(TOPIC, key, payload).join();
    }

    private static String catalogUpdated(String eventId, long version) {
        return """
                {"eventId":"%s","eventType":"CatalogUpdated","newVersion":%d,
                 "occurredAt":"2026-10-28T12:00:00Z","source":"MANUAL","schemaVersion":1}""".formatted(eventId, version);
    }

    private List<String> processedEventIds() {
        return jdbcTemplate.queryForList("SELECT event_id FROM sync_processed_events", String.class);
    }
}
