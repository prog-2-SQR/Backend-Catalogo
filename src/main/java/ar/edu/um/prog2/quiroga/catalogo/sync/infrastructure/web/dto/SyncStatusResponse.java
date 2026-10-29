package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.web.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncStatusResponse {

    private String health;
    private Long localVersion;
    private Instant appliedAt;
    private Long publishedVersion;
    private Instant lastSuccessAt;
    private Instant lastFailureAt;
    private String lastFailureMessage;
}
