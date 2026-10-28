package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.web.mapper;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncStatus;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.web.dto.SyncStatusResponse;
import org.springframework.stereotype.Component;

@Component
public class SyncStatusDtoMapper {

    public SyncStatusResponse toResponse(SyncStatus status) {
        if (status == null) return null;
        return SyncStatusResponse.builder()
                .health(status.getHealth().name())
                .localVersion(status.getLocalVersion())
                .appliedAt(status.getAppliedAt())
                .publishedVersion(status.getPublishedVersion())
                .lastSuccessAt(status.getLastSuccessAt())
                .lastFailureAt(status.getLastFailureAt())
                .lastFailureMessage(status.getLastFailureMessage())
                .build();
    }
}
