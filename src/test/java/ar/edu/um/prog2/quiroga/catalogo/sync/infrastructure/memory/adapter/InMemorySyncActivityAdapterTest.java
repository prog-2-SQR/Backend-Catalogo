package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.memory.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncActivity;
import org.junit.jupiter.api.Test;

class InMemorySyncActivityAdapterTest {

    private final InMemorySyncActivityAdapter adapter = new InMemorySyncActivityAdapter();

    @Test
    void startsWithoutAttempts() {
        SyncActivity activity = adapter.findActivity();

        assertThat(activity.getLastSuccessAt()).isNull();
        assertThat(activity.getLastFailureAt()).isNull();
        assertThat(activity.getLastFailureMessage()).isNull();
    }

    @Test
    void keepsTheLastSuccessAndTheLastFailureWithItsMessage() {
        adapter.recordFailure("No se pudo leer el Redis de la cátedra");
        adapter.recordSuccess();

        SyncActivity activity = adapter.findActivity();
        assertThat(activity.getLastFailureMessage()).isEqualTo("No se pudo leer el Redis de la cátedra");
        assertThat(activity.getLastSuccessAt()).isAfterOrEqualTo(activity.getLastFailureAt());
    }

    @Test
    void whatIsReadCannotChangeWhatWasRecorded() {
        adapter.recordFailure("primera");

        adapter.findActivity().setLastFailureMessage("cambiado afuera");

        assertThat(adapter.findActivity().getLastFailureMessage()).isEqualTo("primera");
    }
}
