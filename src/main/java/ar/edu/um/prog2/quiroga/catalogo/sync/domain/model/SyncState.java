package ar.edu.um.prog2.quiroga.catalogo.sync.domain.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Versión del catálogo que tiene la copia local: solo se guarda si los datos de esa versión quedaron aplicados (anexo §7). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncState {

    private Long snapshotVersion;
    private Instant appliedAt;
}
