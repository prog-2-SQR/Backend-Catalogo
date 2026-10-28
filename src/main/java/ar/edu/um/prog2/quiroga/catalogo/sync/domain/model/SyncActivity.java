package ar.edu.um.prog2.quiroga.catalogo.sync.domain.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Último intento de sincronización que terminó bien y último que falló, con su mensaje. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SyncActivity {

    private Instant lastSuccessAt;
    private Instant lastFailureAt;
    private String lastFailureMessage;
}
