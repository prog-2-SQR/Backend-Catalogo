package ar.edu.um.prog2.quiroga.catalogo.sync.application.usecases;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogUpdate;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.in.SynchronizeChangesUseCase;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.ProcessedEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HandleCatalogUpdateUseCaseImplTest {

    private static final CatalogUpdate UPDATE = CatalogUpdate.builder().eventId("catalog-event-7-4f3b").newVersion(7L).build();

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @Mock
    private SynchronizeChangesUseCase synchronizeChangesUseCase;

    @InjectMocks
    private HandleCatalogUpdateUseCaseImpl handleCatalogUpdateUseCase;

    @Test
    void newUpdateSynchronizesAndIsRecorded() {
        when(processedEventRepository.existsByEventId("catalog-event-7-4f3b")).thenReturn(false);
        when(synchronizeChangesUseCase.synchronizeChanges()).thenReturn(SyncState.builder().snapshotVersion(7L).build());

        handleCatalogUpdateUseCase.handleCatalogUpdate(UPDATE);

        verify(synchronizeChangesUseCase).synchronizeChanges();
        verify(processedEventRepository).saveProcessedEvent(UPDATE);
    }

    @Test
    void repeatedUpdateDoesNothing() {
        when(processedEventRepository.existsByEventId("catalog-event-7-4f3b")).thenReturn(true);

        handleCatalogUpdateUseCase.handleCatalogUpdate(UPDATE);

        verify(synchronizeChangesUseCase, never()).synchronizeChanges();
        verify(processedEventRepository, never()).saveProcessedEvent(any());
    }

    @Test
    void failedSynchronizationIsNotRecordedSoTheUpdateCanBeRetried() {
        when(processedEventRepository.existsByEventId("catalog-event-7-4f3b")).thenReturn(false);
        when(synchronizeChangesUseCase.synchronizeChanges()).thenThrow(new SyncException("cátedra caída"));

        assertThatThrownBy(() -> handleCatalogUpdateUseCase.handleCatalogUpdate(UPDATE)).isInstanceOf(SyncException.class);

        verify(processedEventRepository, never()).saveProcessedEvent(any());
    }
}
