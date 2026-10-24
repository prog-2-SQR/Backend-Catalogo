package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.redis.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogVersions;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogChangeFeed;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

@ExtendWith(MockitoExtension.class)
class CatalogRedisHealthIndicatorTest {

    @Mock
    private CatalogChangeFeed catalogChangeFeed;

    @InjectMocks
    private CatalogRedisHealthIndicator healthIndicator;

    @Test
    void upWithTheCurrentVersionWhenRedisAnswers() {
        when(catalogChangeFeed.findVersions())
                .thenReturn(Optional.of(CatalogVersions.builder().currentVersion(7L).oldestAvailableVersion(4L).build()));

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("currentVersion", 7L);
    }

    @Test
    void downWhenThereAreNoPublishedVersions() {
        when(catalogChangeFeed.findVersions()).thenReturn(Optional.empty());

        assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    void downWhenRedisCannotBeRead() {
        when(catalogChangeFeed.findVersions()).thenThrow(new SyncException("No se pudo leer el Redis de la cátedra"));

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("reason", "No se pudo leer el Redis de la cátedra");
    }
}
