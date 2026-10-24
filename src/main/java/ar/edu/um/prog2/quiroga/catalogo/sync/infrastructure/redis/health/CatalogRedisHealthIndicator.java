package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.redis.health;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogChangeFeed;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Estado del Redis de la cátedra en /actuator/health ("catalogRedis"). El indicador de Spring usa PING o INFO,
 * que la ACL de la cátedra no permite, así que se reemplaza por una lectura real de las versiones publicadas.
 */
@Component
@RequiredArgsConstructor
public class CatalogRedisHealthIndicator implements HealthIndicator {

    private final CatalogChangeFeed catalogChangeFeed;

    @Override
    public Health health() {
        try {
            return catalogChangeFeed.findVersions()
                    .map(versions -> Health.up().withDetail("currentVersion", versions.getCurrentVersion()).build())
                    .orElseGet(() -> Health.down().withDetail("reason", "La cátedra no publicó versiones").build());
        } catch (SyncException e) {
            return Health.down().withDetail("reason", e.getMessage()).build();
        }
    }
}
