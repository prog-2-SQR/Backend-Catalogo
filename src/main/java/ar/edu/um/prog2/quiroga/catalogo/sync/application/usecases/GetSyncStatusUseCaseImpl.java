package ar.edu.um.prog2.quiroga.catalogo.sync.application.usecases;

import java.util.Optional;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogVersions;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncActivity;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncStatus;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.GetSyncStatusUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogChangeFeed;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncActivityRepository;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetSyncStatusUseCaseImpl implements GetSyncStatusUseCase {

    private final SyncStateRepository syncStateRepository;
    private final CatalogChangeFeed catalogChangeFeed;
    private final SyncActivityRepository syncActivityRepository;

    @Override
    public SyncStatus getSyncStatus() {
        Optional<SyncState> local = syncStateRepository.findSyncState();
        SyncActivity activity = syncActivityRepository.findActivity();
        return SyncStatus.builder()
                .localVersion(local.map(SyncState::getSnapshotVersion).orElse(null))
                .appliedAt(local.map(SyncState::getAppliedAt).orElse(null))
                .publishedVersion(publishedVersion())
                .lastSuccessAt(activity.getLastSuccessAt())
                .lastFailureAt(activity.getLastFailureAt())
                .lastFailureMessage(activity.getLastFailureMessage())
                .build();
    }

    // Con Redis caído la consulta del estado igual responde: la versión publicada queda desconocida (UNKNOWN).
    private Long publishedVersion() {
        try {
            return catalogChangeFeed.findVersions().map(CatalogVersions::getCurrentVersion).orElse(null);
        } catch (SyncException e) {
            return null;
        }
    }
}
