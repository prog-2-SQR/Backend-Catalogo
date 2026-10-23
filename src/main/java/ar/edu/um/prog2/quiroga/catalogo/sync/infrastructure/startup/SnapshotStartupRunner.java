package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.startup;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.application.service.SyncService;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Al arrancar con la base vacía, carga el catálogo con el snapshot (enunciado §6.1). Si la cátedra no responde,
 * la app arranca igual: el error queda en el log y la copia se completa en la próxima sincronización.
 * Se puede apagar con catedra.sync-on-startup=false (lo usan los tests).
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "catedra", name = "sync-on-startup", havingValue = "true", matchIfMissing = true)
public class SnapshotStartupRunner implements ApplicationRunner {

    private final SyncService syncService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            SyncState state = syncService.initializeCatalog();
            log.info("Catálogo local en la versión {}", state.getSnapshotVersion());
        } catch (SyncException e) {
            log.error("No se pudo cargar el catálogo al arrancar; queda para la próxima sincronización: {}", e.getMessage());
        }
    }
}
