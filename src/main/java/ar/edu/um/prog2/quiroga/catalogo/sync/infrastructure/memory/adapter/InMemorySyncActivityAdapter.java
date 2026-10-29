package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.memory.adapter;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncActivity;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncActivityRepository;
import org.springframework.stereotype.Component;

/**
 * Los intentos de sincronización se guardan en memoria: son actividad de la app en ejecución (como un health check)
 * y vuelven a empezar al reiniciarla. La versión local, que sí importa conservar, está en la base (sync_state).
 * AtomicReference: el arranque, el polling y el listener de Kafka pueden registrar desde hilos distintos.
 */
@Component
public class InMemorySyncActivityAdapter implements SyncActivityRepository {

    private final AtomicReference<SyncActivity> activity = new AtomicReference<>(new SyncActivity());

    @Override
    public void recordSuccess() {
        Instant now = Instant.now();
        activity.updateAndGet(current -> current.toBuilder().lastSuccessAt(now).build());
    }

    @Override
    public void recordFailure(String message) {
        Instant now = Instant.now();
        activity.updateAndGet(current -> current.toBuilder().lastFailureAt(now).lastFailureMessage(message).build());
    }

    // Una copia: quien la lee no puede cambiar lo registrado.
    @Override
    public SyncActivity findActivity() {
        return activity.get().toBuilder().build();
    }
}
