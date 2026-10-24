package ar.edu.um.prog2.quiroga.catalogo.sync.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Versiones que publica la cátedra (anexo §14.2): la última y la más vieja de la que todavía hay cambios. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogVersions {

    private Long currentVersion;
    private Long oldestAvailableVersion;

    /** Anexo §14.2: si la versión local es menor que la más vieja disponible, hay que pedir el snapshot. */
    public boolean isOutsideWindow(long localVersion) {
        return localVersion < oldestAvailableVersion;
    }

    public boolean isNewerThan(long localVersion) {
        return currentVersion > localVersion;
    }
}
