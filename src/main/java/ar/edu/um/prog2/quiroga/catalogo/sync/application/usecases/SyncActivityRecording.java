package ar.edu.um.prog2.quiroga.catalogo.sync.application.usecases;

import java.util.function.Supplier;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.SyncActivityRepository;

/**
 * Deja registrado cómo terminó una sincronización, para informar el estado (enunciado §4.1). El error se
 * registra con el mensaje de SyncException (escrito por nosotros, sin detalles internos) y se vuelve a lanzar.
 */
final class SyncActivityRecording {

    private SyncActivityRecording() {
    }

    static <T> T recorded(SyncActivityRepository syncActivityRepository, Supplier<T> synchronization) {
        try {
            T result = synchronization.get();
            syncActivityRepository.recordSuccess();
            return result;
        } catch (SyncException e) {
            syncActivityRepository.recordFailure(e.getMessage());
            throw e;
        }
    }
}
