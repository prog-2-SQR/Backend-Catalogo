package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.startup;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.application.service.SyncService;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

@ExtendWith(MockitoExtension.class)
class SnapshotStartupRunnerTest {

    @Mock
    private SyncService syncService;

    @InjectMocks
    private SnapshotStartupRunner runner;

    @Test
    void initializesTheCatalogOnStartup() {
        when(syncService.initializeCatalog()).thenReturn(SyncState.builder().snapshotVersion(1L).build());

        runner.run(new DefaultApplicationArguments());

        verify(syncService).initializeCatalog();
    }

    @Test
    void theAppStartsEvenIfTheCatedraIsDown() {
        when(syncService.initializeCatalog()).thenThrow(new SyncException("La cátedra no responde"));

        assertThatCode(() -> runner.run(new DefaultApplicationArguments())).doesNotThrowAnyException();
    }
}
