package ar.edu.um.prog2.quiroga.catalogo.sync.application.usecases;

import static ar.edu.um.prog2.quiroga.catalogo.sync.support.SyncFixtures.anaTorres;
import static ar.edu.um.prog2.quiroga.catalogo.sync.support.SyncFixtures.cardiologia;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogChanges;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogSnapshot;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogVersions;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.ApplySnapshotUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.SynchronizeSnapshotUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogChangeFeed;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncStateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SynchronizeChangesUseCaseImplTest {

    private static final SyncState FULL_SYNC = SyncState.builder().snapshotVersion(99L).build();

    @Mock
    private SyncStateRepository syncStateRepository;

    @Mock
    private CatalogChangeFeed catalogChangeFeed;

    @Mock
    private ApplySnapshotUseCase applySnapshotUseCase;

    @Mock
    private SynchronizeSnapshotUseCase synchronizeSnapshotUseCase;

    @InjectMocks
    private SynchronizeChangesUseCaseImpl synchronizeChangesUseCase;

    private void localVersion(long version) {
        when(syncStateRepository.findSyncState()).thenReturn(Optional.of(SyncState.builder().snapshotVersion(version).build()));
    }

    private void published(long current, long oldest) {
        when(catalogChangeFeed.findVersions())
                .thenReturn(Optional.of(CatalogVersions.builder().currentVersion(current).oldestAvailableVersion(oldest).build()));
    }

    @Test
    void withoutLocalVersionDoesAFullSync() {
        when(syncStateRepository.findSyncState()).thenReturn(Optional.empty());
        when(synchronizeSnapshotUseCase.synchronizeSnapshot()).thenReturn(FULL_SYNC);

        assertThat(synchronizeChangesUseCase.synchronizeChanges()).isSameAs(FULL_SYNC);
        verify(catalogChangeFeed, never()).findVersions();
    }

    @Test
    void upToDateDoesNothing() {
        localVersion(7);
        published(7, 4);

        assertThat(synchronizeChangesUseCase.synchronizeChanges().getSnapshotVersion()).isEqualTo(7L);
        verify(applySnapshotUseCase, never()).applySnapshot(any());
        verify(synchronizeSnapshotUseCase, never()).synchronizeSnapshot();
    }

    @Test
    void withoutPublishedVersionsKeepsTheLocalCopy() {
        localVersion(7);
        when(catalogChangeFeed.findVersions()).thenReturn(Optional.empty());

        assertThat(synchronizeChangesUseCase.synchronizeChanges().getSnapshotVersion()).isEqualTo(7L);
        verify(synchronizeSnapshotUseCase, never()).synchronizeSnapshot();
    }

    @Test
    void aLocalVersionOutsideTheWindowDoesAFullSync() {
        localVersion(2);
        published(7, 4);
        when(synchronizeSnapshotUseCase.synchronizeSnapshot()).thenReturn(FULL_SYNC);

        assertThat(synchronizeChangesUseCase.synchronizeChanges()).isSameAs(FULL_SYNC);
        verify(catalogChangeFeed, never()).findChanges(anyLong());
    }

    @Test
    void appliesEachVersionInOrderWithTheCurrentStateOfTheChangedEntities() {
        localVersion(5);
        published(7, 4);
        when(catalogChangeFeed.findChanges(6)).thenReturn(Optional.of(
                CatalogChanges.builder().version(6L).professionalIds(List.of(201L)).build()));
        when(catalogChangeFeed.findChanges(7)).thenReturn(Optional.of(
                CatalogChanges.builder().version(7L).categoryIds(List.of(101L)).build()));
        when(catalogChangeFeed.findCategories(List.of())).thenReturn(List.of());
        when(catalogChangeFeed.findCategories(List.of(101L))).thenReturn(List.of(cardiologia()));
        when(catalogChangeFeed.findProfessionals(List.of(201L))).thenReturn(List.of(anaTorres()));
        when(catalogChangeFeed.findProfessionals(List.of())).thenReturn(List.of());
        when(catalogChangeFeed.findWeeklySchedules(List.of())).thenReturn(List.of());
        when(applySnapshotUseCase.applySnapshot(any())).thenAnswer(invocation -> SyncState.builder()
                .snapshotVersion(invocation.<CatalogSnapshot>getArgument(0).getSnapshotVersion()).build());

        SyncState state = synchronizeChangesUseCase.synchronizeChanges();

        ArgumentCaptor<CatalogSnapshot> applied = ArgumentCaptor.forClass(CatalogSnapshot.class);
        verify(applySnapshotUseCase, times(2)).applySnapshot(applied.capture());
        assertThat(applied.getAllValues()).extracting(CatalogSnapshot::getSnapshotVersion).containsExactly(6L, 7L);
        assertThat(applied.getAllValues().getFirst().getProfessionals()).extracting("id").containsExactly(201L);
        assertThat(applied.getAllValues().get(1).getCategories()).extracting("id").containsExactly(101L);
        assertThat(state.getSnapshotVersion()).isEqualTo(7L);
        verify(synchronizeSnapshotUseCase, never()).synchronizeSnapshot();
    }

    @Test
    void aMissingVersionDoesAFullSync() {
        localVersion(5);
        published(7, 4);
        when(catalogChangeFeed.findChanges(6)).thenReturn(Optional.empty());
        when(synchronizeSnapshotUseCase.synchronizeSnapshot()).thenReturn(FULL_SYNC);

        assertThat(synchronizeChangesUseCase.synchronizeChanges()).isSameAs(FULL_SYNC);
        verify(applySnapshotUseCase, never()).applySnapshot(any());
    }

    @Test
    void aChangedEntityMissingInRedisDoesAFullSync() {
        localVersion(6);
        published(7, 4);
        when(catalogChangeFeed.findChanges(7)).thenReturn(Optional.of(
                CatalogChanges.builder().version(7L).categoryIds(List.of(999L)).build()));
        when(catalogChangeFeed.findCategories(List.of(999L))).thenThrow(new SyncException("Falta en Redis la entidad 999"));
        when(synchronizeSnapshotUseCase.synchronizeSnapshot()).thenReturn(FULL_SYNC);

        assertThat(synchronizeChangesUseCase.synchronizeChanges()).isSameAs(FULL_SYNC);
    }
}
