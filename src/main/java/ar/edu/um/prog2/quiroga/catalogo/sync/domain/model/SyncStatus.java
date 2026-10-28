package ar.edu.um.prog2.quiroga.catalogo.sync.domain.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Estado de la sincronización que informa el catálogo (enunciado §4.1): versiones, último éxito y último error. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncStatus {

    private Long localVersion;
    private Instant appliedAt;
    private Long publishedVersion;
    private Instant lastSuccessAt;
    private Instant lastFailureAt;
    private String lastFailureMessage;

    public SyncHealth getHealth() {
        if (localVersion == null) {
            return SyncHealth.NOT_INITIALIZED;
        }
        if (isFailing()) {
            return SyncHealth.FAILING;
        }
        if (publishedVersion == null) {
            return SyncHealth.UNKNOWN;
        }
        return publishedVersion > localVersion ? SyncHealth.OUTDATED : SyncHealth.UP_TO_DATE;
    }

    // Falla vigente: la última falla es posterior al último intento que terminó bien.
    private boolean isFailing() {
        return lastFailureAt != null && (lastSuccessAt == null || lastFailureAt.isAfter(lastSuccessAt));
    }
}
