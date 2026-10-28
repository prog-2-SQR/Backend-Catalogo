package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.messaging.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.application.service.SyncService;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogUpdate;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.messaging.mapper.CatalogUpdatedMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class CatalogUpdatedListenerTest {

    // Ejemplo del anexo §15.3.
    private static final String CATALOG_UPDATED = """
            {"eventId":"catalog-event-7-4f3b","eventType":"CatalogUpdated","newVersion":7,
             "occurredAt":"2026-09-10T13:44:59Z","source":"MANUAL","schemaVersion":1}""";

    @Mock
    private SyncService syncService;

    private CatalogUpdatedListener listener;

    @BeforeEach
    void setUp() {
        listener = new CatalogUpdatedListener(syncService, JsonMapper.builder().build(), new CatalogUpdatedMapper());
    }

    @Test
    void catalogUpdatedTriggersTheSynchronization() {
        listener.onCatalogUpdated(CATALOG_UPDATED);

        ArgumentCaptor<CatalogUpdate> update = ArgumentCaptor.forClass(CatalogUpdate.class);
        verify(syncService).handleCatalogUpdate(update.capture());
        assertThat(update.getValue().getEventId()).isEqualTo("catalog-event-7-4f3b");
        assertThat(update.getValue().getNewVersion()).isEqualTo(7L);
        assertThat(update.getValue().getOccurredAt()).isEqualTo(Instant.parse("2026-09-10T13:44:59Z"));
    }

    @Test
    void unknownFieldsAreIgnored() {
        listener.onCatalogUpdated("""
                {"eventId":"catalog-event-8-9a1c","eventType":"CatalogUpdated","newVersion":8,"campoNuevo":"x"}""");

        verify(syncService).handleCatalogUpdate(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "no es json",
            "{\"eventId\":\"e-1\",\"eventType\":\"AppointmentConfirmed\",\"newVersion\":7}",
            "{\"eventType\":\"CatalogUpdated\",\"newVersion\":7}",
            "{\"eventId\":\" \",\"eventType\":\"CatalogUpdated\",\"newVersion\":7}",
            "{\"eventId\":\"e-1\",\"eventType\":\"CatalogUpdated\"}",
            "null"})
    void unusableMessagesAreDiscarded(String payload) {
        listener.onCatalogUpdated(payload);

        verify(syncService, never()).handleCatalogUpdate(any());
    }

    @Test
    void longDiscardedMessagesDoNotBreakTheListener() {
        listener.onCatalogUpdated("x".repeat(500));

        verify(syncService, never()).handleCatalogUpdate(any());
    }

    @Test
    void synchronizationErrorsReachTheErrorHandlerToBeRetried() {
        doThrow(new SyncException("cátedra caída")).when(syncService).handleCatalogUpdate(any());

        assertThatThrownBy(() -> listener.onCatalogUpdated(CATALOG_UPDATED)).isInstanceOf(SyncException.class);
    }
}
