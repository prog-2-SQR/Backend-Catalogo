package ar.edu.um.prog2.quiroga.catalogo.sync.application.usecases;

import java.time.Instant;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogSnapshot;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.ApplySnapshotUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogWriter;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aplica las tres colecciones y recién después guarda la versión, todo en una transacción:
 * si algo falla a mitad, no queda ni una parte de los datos ni una versión que no les corresponde (anexo §7).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApplySnapshotUseCaseImpl implements ApplySnapshotUseCase {

    private final CatalogWriter catalogWriter;
    private final SyncStateRepository syncStateRepository;

    @Override
    @Transactional
    public SyncState applySnapshot(CatalogSnapshot snapshot) {
        snapshot.getCategories().forEach(catalogWriter::saveCategory);
        snapshot.getProfessionals().forEach(catalogWriter::saveProfessional);
        snapshot.getWeeklySchedules().forEach(catalogWriter::saveWeeklySchedule);
        SyncState applied = syncStateRepository.saveSyncState(SyncState.builder()
                .snapshotVersion(snapshot.getSnapshotVersion())
                .appliedAt(Instant.now())
                .build());
        log.info("Snapshot {} aplicado: {} categorías, {} profesionales, {} horarios",
                snapshot.getSnapshotVersion(), snapshot.getCategories().size(),
                snapshot.getProfessionals().size(), snapshot.getWeeklySchedules().size());
        return applied;
    }
}
