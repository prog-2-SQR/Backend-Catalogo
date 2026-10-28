package ar.edu.um.prog2.quiroga.catalogo.sync.application.usecases;

import java.util.Optional;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogChanges;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogSnapshot;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogVersions;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.ApplySnapshotUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.SynchronizeChangesUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.SynchronizeSnapshotUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogChangeFeed;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncActivityRepository;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Sincronización incremental (enunciado §6.2, anexo §14): versión por versión, en orden, cada una en su propia
 * transacción (ApplySnapshotUseCase), así la versión local solo avanza cuando sus cambios quedaron guardados.
 * Sin versión local, fuera de la ventana o con una versión faltante o incompleta → sincronización completa.
 * El resultado queda registrado para el estado de la sincronización.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SynchronizeChangesUseCaseImpl implements SynchronizeChangesUseCase {

    private final SyncStateRepository syncStateRepository;
    private final CatalogChangeFeed catalogChangeFeed;
    private final ApplySnapshotUseCase applySnapshotUseCase;
    private final SynchronizeSnapshotUseCase synchronizeSnapshotUseCase;
    private final SyncActivityRepository syncActivityRepository;

    @Override
    public SyncState synchronizeChanges() {
        return SyncActivityRecording.recorded(syncActivityRepository, this::synchronize);
    }

    private SyncState synchronize() {
        Optional<SyncState> local = syncStateRepository.findSyncState();
        if (local.isEmpty()) {
            return synchronizeSnapshotUseCase.synchronizeSnapshot();
        }
        long localVersion = local.get().getSnapshotVersion();
        Optional<CatalogVersions> published = catalogChangeFeed.findVersions();
        if (published.isEmpty() || !published.get().isNewerThan(localVersion)) {
            return local.get();
        }
        if (published.get().isOutsideWindow(localVersion)) {
            log.warn("Versión local {} fuera de la ventana de Redis: sincronización completa", localVersion);
            return synchronizeSnapshotUseCase.synchronizeSnapshot();
        }
        return applyVersions(local.get(), published.get().getCurrentVersion());
    }

    private SyncState applyVersions(SyncState local, long currentVersion) {
        SyncState state = local;
        try {
            for (long version = local.getSnapshotVersion() + 1; version <= currentVersion; version++) {
                state = applySnapshotUseCase.applySnapshot(changedEntitiesOf(version));
            }
            return state;
        } catch (SyncException e) {
            log.warn("No se pudo seguir desde la versión {} ({}): sincronización completa",
                    state.getSnapshotVersion(), e.getMessage());
            return synchronizeSnapshotUseCase.synchronizeSnapshot();
        }
    }

    // Los cambios traen solo IDs: el estado vigente de cada entidad se lee de Redis (anexo §14.5).
    private CatalogSnapshot changedEntitiesOf(long version) {
        CatalogChanges changes = catalogChangeFeed.findChanges(version)
                .orElseThrow(() -> new SyncException("Falta en Redis la versión " + version));
        return CatalogSnapshot.builder()
                .snapshotVersion(version)
                .categories(catalogChangeFeed.findCategories(changes.getCategoryIds()))
                .professionals(catalogChangeFeed.findProfessionals(changes.getProfessionalIds()))
                .weeklySchedules(catalogChangeFeed.findWeeklySchedules(changes.getWeeklyScheduleIds()))
                .build();
    }
}
