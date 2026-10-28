package ar.edu.um.prog2.quiroga.catalogo.sync;

import static ar.edu.um.prog2.quiroga.catalogo.sync.support.SyncFixtures.snapshot;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.io.IOException;

import ar.edu.um.prog2.quiroga.catalogo.TestcontainersConfiguration;
import ar.edu.um.prog2.quiroga.catalogo.category.application.service.CategoryService;
import ar.edu.um.prog2.quiroga.catalogo.professional.application.service.ProfessionalService;
import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.application.service.SyncService;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncHealth;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncStatus;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogSnapshotClient;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncStateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Sincronización incremental de punta a punta: PostgreSQL y Redis reales. La base parte de la versión 1
 * (snapshot) y Redis publica las versiones 2 y 3 con los formatos del anexo §14.
 */
@SpringBootTest(properties = {"catedra.api-url=http://localhost:8090", "catedra.jwt=jwt-de-prueba",
        "catedra.sync-on-startup=false", "catedra.sync-polling=false"})
@Import({TestcontainersConfiguration.class, IncrementalSynchronizationTest.RedisConfiguration.class})
class IncrementalSynchronizationTest {

    @TestConfiguration(proxyBeanMethods = false)
    static class RedisConfiguration {

        @Bean
        @ServiceConnection(name = "redis")
        GenericContainer<?> redisContainer() {
            return new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine")).withExposedPorts(6379);
        }
    }

    @MockitoBean
    private CatalogSnapshotClient catalogSnapshotClient;

    @Autowired
    private SyncService syncService;

    @Autowired
    private SyncStateRepository syncStateRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProfessionalService professionalService;

    @Autowired
    private GenericContainer<?> redisContainer;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void clean() throws Exception {
        jdbcTemplate.execute("TRUNCATE sync_state, categories, professionals, weekly_schedules");
        redis("FLUSHALL");
    }

    @Test
    void appliesThePublishedVersionsOverTheLocalCopy() throws Exception {
        when(catalogSnapshotClient.fetchSnapshot()).thenReturn(snapshot(1));
        syncService.synchronizeSnapshot();
        redis("HSET", "catedra:sync:metadata", "currentVersion", "3", "oldestAvailableVersion", "1");
        // Versión 2: se deshabilita la profesional 201. Versión 3: cambia el nombre de la categoría 101.
        redis("SET", "catedra:sync:changes:2", "{\"version\":2,\"changes\":{\"professionalCategories\":[],\"professionals\":[201],\"weeklySchedules\":[]}}");
        redis("SET", "catedra:sync:changes:3", "{\"version\":3,\"changes\":{\"professionalCategories\":[101],\"professionals\":[],\"weeklySchedules\":[]}}");
        redis("HSET", "catedra:sync:professionals", "201",
                "{\"id\":201,\"categoryId\":101,\"firstName\":\"Ana\",\"lastName\":\"Torres\",\"enabled\":false,\"createdAt\":\"2026-09-01T03:00:00Z\",\"updatedAt\":\"2026-10-26T12:00:00Z\"}");
        redis("HSET", "catedra:sync:professional-categories", "101",
                "{\"id\":101,\"name\":\"Cardiologia infantil\",\"enabled\":true,\"createdAt\":\"2026-09-01T03:00:00Z\",\"updatedAt\":\"2026-10-26T12:00:00Z\"}");

        syncService.synchronizeChanges();

        assertThat(syncStateRepository.findSyncState()).get().extracting("snapshotVersion").isEqualTo(3L);
        assertThat(professionalService.findProfessionalById(201L).getEnabled()).isFalse();
        assertThat(categoryService.findCategoryById(101L).getName()).isEqualTo("Cardiologia infantil");
    }

    @Test
    void aMissingVersionFallsBackToTheSnapshot() throws Exception {
        when(catalogSnapshotClient.fetchSnapshot()).thenReturn(snapshot(1), snapshot(5));
        syncService.synchronizeSnapshot();
        // La cátedra dice que hay hasta la 5, pero la 2 ya no está: no se puede seguir de forma segura.
        redis("HSET", "catedra:sync:metadata", "currentVersion", "5", "oldestAvailableVersion", "1");

        syncService.synchronizeChanges();

        assertThat(syncStateRepository.findSyncState()).get().extracting("snapshotVersion").isEqualTo(5L);
    }

    @Test
    void theStatusShowsTheVersionsAndTheLastError() throws Exception {
        when(catalogSnapshotClient.fetchSnapshot()).thenReturn(snapshot(1));
        syncService.synchronizeSnapshot();
        redis("HSET", "catedra:sync:metadata", "currentVersion", "1", "oldestAvailableVersion", "1");
        assertThat(syncService.getSyncStatus().getHealth()).isEqualTo(SyncHealth.UP_TO_DATE);

        // Se publica la 2 pero falta su detalle, y el snapshot de respaldo tampoco responde.
        redis("HSET", "catedra:sync:metadata", "currentVersion", "2");
        when(catalogSnapshotClient.fetchSnapshot()).thenThrow(new SyncException("No se pudo obtener el snapshot de la cátedra"));
        assertThatThrownBy(() -> syncService.synchronizeChanges()).isInstanceOf(SyncException.class);

        SyncStatus status = syncService.getSyncStatus();
        assertThat(status.getHealth()).isEqualTo(SyncHealth.FAILING);
        assertThat(status.getLocalVersion()).isEqualTo(1L);
        assertThat(status.getPublishedVersion()).isEqualTo(2L);
        assertThat(status.getLastFailureMessage()).isEqualTo("No se pudo obtener el snapshot de la cátedra");
    }

    private void redis(String... command) throws IOException, InterruptedException {
        String[] full = new String[command.length + 1];
        full[0] = "redis-cli";
        System.arraycopy(command, 0, full, 1, command.length);
        redisContainer.execInContainer(full);
    }
}
