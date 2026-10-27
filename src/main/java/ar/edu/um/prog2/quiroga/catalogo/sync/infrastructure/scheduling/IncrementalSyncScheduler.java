package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.scheduling;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.application.service.SyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Revisa cada cierto tiempo si la cátedra publicó versiones nuevas. Cubre las notificaciones de Kafka perdidas
 * (enunciado §8) y, mientras los topics de la cátedra no funcionen, es lo que mantiene la copia al día.
 * Se apaga con catedra.sync-polling=false.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "catedra", name = "sync-polling", havingValue = "true", matchIfMissing = true)
public class IncrementalSyncScheduler {

    private final SyncService syncService;

    @Scheduled(initialDelayString = "${catedra.sync-interval:PT1M}", fixedDelayString = "${catedra.sync-interval:PT1M}")
    public void synchronize() {
        try {
            log.debug("Catálogo local en la versión {}", syncService.synchronizeChanges().getSnapshotVersion());
        } catch (SyncException e) {
            log.error("No se pudo sincronizar el catálogo; se reintenta en el próximo chequeo: {}", e.getMessage());
        }
    }
}
