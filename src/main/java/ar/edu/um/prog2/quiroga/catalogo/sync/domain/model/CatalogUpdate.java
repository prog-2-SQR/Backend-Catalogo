package ar.edu.um.prog2.quiroga.catalogo.sync.domain.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Aviso de la cátedra de que publicó una versión nueva del catálogo (anexo §15.3). Solo notifica: los datos se
 * leen de Redis o del snapshot. El eventId identifica el aviso para no procesarlo dos veces (anexo §15.1).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogUpdate {

    private String eventId;
    private Long newVersion;
    private Instant occurredAt;
}
