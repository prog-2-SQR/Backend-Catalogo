package ar.edu.um.prog2.quiroga.catalogo.sync;

import static ar.edu.um.prog2.quiroga.catalogo.sync.support.SyncFixtures.snapshot;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;

import ar.edu.um.prog2.quiroga.catalogo.TestcontainersConfiguration;
import ar.edu.um.prog2.quiroga.catalogo.category.application.service.CategoryService;
import ar.edu.um.prog2.quiroga.catalogo.professional.application.service.ProfessionalService;
import ar.edu.um.prog2.quiroga.catalogo.schedule.application.service.WeeklyScheduleService;
import ar.edu.um.prog2.quiroga.catalogo.sync.application.service.SyncService;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogSnapshot;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogWeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogSnapshotClient;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncStateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Sincronización completa contra un PostgreSQL real: los datos llegan a las tablas de los tres slices y,
 * si algo falla a mitad, la transacción deja la base como estaba (sin datos a medias ni versión nueva).
 */
@SpringBootTest(properties = {"catedra.api-url=http://localhost:8090", "catedra.jwt=jwt-de-prueba",
        "catedra.sync-on-startup=false"})
@Import(TestcontainersConfiguration.class)
class SnapshotSynchronizationTest {

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
    private WeeklyScheduleService weeklyScheduleService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE sync_state, categories, professionals, weekly_schedules");
    }

    @Test
    void emptyDatabaseIsFilledWithTheSnapshotAndItsVersion() {
        when(catalogSnapshotClient.fetchSnapshot()).thenReturn(snapshot(7));

        syncService.initializeCatalog();

        assertThat(categoryService.getAllCategories()).hasSize(1);
        assertThat(professionalService.getAllProfessionals()).hasSize(1);
        assertThat(weeklyScheduleService.getAllWeeklySchedules()).hasSize(1);
        assertThat(syncStateRepository.findSyncState()).get()
                .extracting("snapshotVersion").isEqualTo(7L);
    }

    @Test
    void aFailureHalfwayLeavesTheDatabaseAsItWas() {
        CatalogSnapshot broken = snapshot(8);
        CatalogWeeklySchedule invalid = CatalogWeeklySchedule.builder().id(399L).professionalId(201L).build();
        broken.setWeeklySchedules(List.of(broken.getWeeklySchedules().getFirst(), invalid));
        when(catalogSnapshotClient.fetchSnapshot()).thenReturn(broken);

        assertThatThrownBy(() -> syncService.synchronizeSnapshot()).isInstanceOf(RuntimeException.class);

        assertThat(categoryService.getAllCategories()).isEmpty();
        assertThat(professionalService.getAllProfessionals()).isEmpty();
        assertThat(weeklyScheduleService.getAllWeeklySchedules()).isEmpty();
        assertThat(syncStateRepository.findSyncState()).isEmpty();
    }
}
