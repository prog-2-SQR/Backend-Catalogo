package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.redis.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.List;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogProfessional;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogChangeFeed;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.mapper.CatalogSnapshotDtoMapper;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.redis.mapper.CatalogChangesMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Contra un Redis real con la misma ACL que la cátedra: el usuario de la integración solo puede leer
 * catedra:sync:* y no tiene PING, INFO ni CLIENT. Los datos los carga un usuario "admin" del test.
 */
@Testcontainers
class RedisCatalogChangeFeedAdapterTest {

    private static final String USER = "grp_test";
    private static final String PASSWORD = "clave-de-prueba";

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
            .withExposedPorts(6379)
            .withCommand("redis-server",
                    "--user", "default", "off",
                    "--user", "admin", "on", ">admin", "~*", "&*", "+@all",
                    "--user", USER, "on", ">" + PASSWORD, "~catedra:sync:*", "-@all", "+@read", "+hello", "-ping", "-info", "-client");

    private ApplicationContextRunner contextRunner;

    @BeforeEach
    void setUp() throws Exception {
        admin("FLUSHALL");
        contextRunner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(DataRedisAutoConfiguration.class, JacksonAutoConfiguration.class))
                .withBean(CatalogSnapshotDtoMapper.class)
                .withBean(CatalogChangesMapper.class)
                .withBean(RedisCatalogChangeFeedAdapter.class)
                .withPropertyValues(
                        "spring.data.redis.host=" + redis.getHost(),
                        "spring.data.redis.port=" + redis.getMappedPort(6379),
                        "spring.data.redis.username=" + USER,
                        "spring.data.redis.password=" + PASSWORD);
    }

    @Test
    void readsThePublishedVersionsFromTheMetadata() throws Exception {
        admin("HSET", "catedra:sync:metadata", "currentVersion", "7", "oldestAvailableVersion", "4",
                "publishedAt", "2026-09-10T13:44:59Z");

        contextRunner.run(context -> assertThat(feed(context).findVersions()).get()
                .satisfies(versions -> {
                    assertThat(versions.getCurrentVersion()).isEqualTo(7L);
                    assertThat(versions.getOldestAvailableVersion()).isEqualTo(4L);
                }));
    }

    @Test
    void withoutMetadataThereAreNoVersions() {
        contextRunner.run(context -> assertThat(feed(context).findVersions()).isEmpty());
    }

    @Test
    void readsTheChangesOfAVersion() throws Exception {
        admin("SET", "catedra:sync:changes:7",
                "{\"version\":7,\"changes\":{\"professionalCategories\":[],\"professionals\":[201,224],\"weeklySchedules\":[301]}}");

        contextRunner.run(context -> {
            assertThat(feed(context).findChanges(7)).get()
                    .satisfies(changes -> {
                        assertThat(changes.getProfessionalIds()).containsExactly(201L, 224L);
                        assertThat(changes.getWeeklyScheduleIds()).containsExactly(301L);
                    });
            assertThat(feed(context).findChanges(8)).isEmpty();
        });
    }

    @Test
    void readsTheCurrentStateOfTheChangedEntitiesInOrder() throws Exception {
        admin("HSET", "catedra:sync:professionals",
                "201", "{\"id\":201,\"categoryId\":101,\"firstName\":\"Ana\",\"lastName\":\"Torres\",\"enabled\":true}",
                "224", "{\"id\":224,\"categoryId\":105,\"firstName\":\"Emiliano\",\"lastName\":\"Ortiz\",\"enabled\":false}");
        admin("HSET", "catedra:sync:professional-categories",
                "101", "{\"id\":101,\"name\":\"Cardiologia\",\"enabled\":true}");
        admin("HSET", "catedra:sync:weekly-schedules",
                "301", "{\"id\":301,\"professionalId\":201,\"dayOfWeek\":\"MONDAY\",\"startTime\":\"12:00:00\",\"endTime\":\"15:00\"}");

        contextRunner.run(context -> {
            List<CatalogProfessional> professionals = feed(context).findProfessionals(List.of(224L, 201L));
            assertThat(professionals).extracting(CatalogProfessional::getId).containsExactly(224L, 201L);
            assertThat(professionals.getFirst().getEnabled()).isFalse();
            assertThat(feed(context).findCategories(List.of(101L)).getFirst().getName()).isEqualTo("Cardiologia");
            assertThat(feed(context).findWeeklySchedules(List.of(301L)).getFirst().getEndTime()).hasToString("15:00");
            assertThat(feed(context).findCategories(List.of())).isEmpty();
        });
    }

    @Test
    void aMissingEntityMeansTheStateCannotBeVerified() throws Exception {
        admin("HSET", "catedra:sync:professionals", "201", "{\"id\":201}");

        contextRunner.run(context -> assertThatThrownBy(() -> feed(context).findProfessionals(List.of(201L, 999L)))
                .isInstanceOf(SyncException.class)
                .hasMessageContaining("999"));
    }

    @Test
    void invalidJsonOrVersionIsReportedAsSyncException() throws Exception {
        admin("SET", "catedra:sync:changes:5", "{esto no es json");
        admin("HSET", "catedra:sync:metadata", "currentVersion", "siete", "oldestAvailableVersion", "4");

        contextRunner.run(context -> {
            assertThatThrownBy(() -> feed(context).findChanges(5)).isInstanceOf(SyncException.class);
            assertThatThrownBy(() -> feed(context).findVersions()).isInstanceOf(SyncException.class);
        });
    }

    @Test
    void anUnreachableRedisIsReportedAsSyncException() {
        contextRunner.withPropertyValues("spring.data.redis.port=1", "spring.data.redis.timeout=500ms")
                .run(context -> assertThatThrownBy(() -> feed(context).findVersions())
                        .isInstanceOf(SyncException.class)
                        .hasMessageContaining("No se pudo leer"));
    }

    private static CatalogChangeFeed feed(ApplicationContext context) {
        return context.getBean(CatalogChangeFeed.class);
    }

    // Carga datos con el usuario admin (el de la integración no puede escribir, igual que en la cátedra).
    private static void admin(String... command) throws IOException, InterruptedException {
        String[] full = new String[command.length + 5];
        full[0] = "redis-cli";
        full[1] = "--user";
        full[2] = "admin";
        full[3] = "--pass";
        full[4] = "admin";
        System.arraycopy(command, 0, full, 5, command.length);
        redis.execInContainer(full);
    }
}
