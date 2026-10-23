package ar.edu.um.prog2.quiroga.catalogo.sync.application.usecases;

import static ar.edu.um.prog2.quiroga.catalogo.sync.support.SyncFixtures.snapshot;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogSnapshot;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogWriter;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncStateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApplySnapshotUseCaseImplTest {

    @Mock
    private CatalogWriter catalogWriter;

    @Mock
    private SyncStateRepository syncStateRepository;

    @InjectMocks
    private ApplySnapshotUseCaseImpl applySnapshotUseCase;

    @Test
    void savesTheThreeCollectionsAndOnlyThenTheVersion() {
        CatalogSnapshot snapshot = snapshot(7);
        when(syncStateRepository.saveSyncState(any(SyncState.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SyncState state = applySnapshotUseCase.applySnapshot(snapshot);

        InOrder order = inOrder(catalogWriter, syncStateRepository);
        order.verify(catalogWriter).saveCategory(snapshot.getCategories().getFirst());
        order.verify(catalogWriter).saveProfessional(snapshot.getProfessionals().getFirst());
        order.verify(catalogWriter).saveWeeklySchedule(snapshot.getWeeklySchedules().getFirst());
        order.verify(syncStateRepository).saveSyncState(any(SyncState.class));
        assertThat(state.getSnapshotVersion()).isEqualTo(7L);
        assertThat(state.getAppliedAt()).isNotNull();
    }

    @Test
    void doesNotSaveTheVersionWhenAnEntityFails() {
        CatalogSnapshot snapshot = snapshot(7);
        doThrow(new IllegalStateException("falla al guardar")).when(catalogWriter).saveProfessional(any());

        assertThatThrownBy(() -> applySnapshotUseCase.applySnapshot(snapshot))
                .isInstanceOf(IllegalStateException.class);
        verify(syncStateRepository, never()).saveSyncState(any());
    }
}
