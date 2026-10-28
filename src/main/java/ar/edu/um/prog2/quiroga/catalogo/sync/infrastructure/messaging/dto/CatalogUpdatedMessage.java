package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.messaging.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** JSON de CatalogUpdated (anexo §15.3). Los campos que la cátedra agregue en v1 se ignoran (anexo §15.1). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogUpdatedMessage {

    private String eventId;
    private String eventType;
    private Long newVersion;
    private Instant occurredAt;
    private String source;
    private Integer schemaVersion;
}
