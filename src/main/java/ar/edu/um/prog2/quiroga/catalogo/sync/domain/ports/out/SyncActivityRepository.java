package ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncActivity;

/** Registro de los intentos de sincronización: el adapter pone la hora de cada uno. */
public interface SyncActivityRepository {

    void recordSuccess();

    void recordFailure(String message);

    SyncActivity findActivity();
}
