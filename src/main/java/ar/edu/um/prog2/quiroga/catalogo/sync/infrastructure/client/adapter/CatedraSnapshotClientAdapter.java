package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.adapter;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogSnapshot;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogSnapshotClient;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.dto.CatalogSnapshotResponse;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.mapper.CatalogSnapshotDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatedraSnapshotClientAdapter implements CatalogSnapshotClient {

    static final String SNAPSHOT_PATH = "/api/synchronization/snapshot";

    private final RestClient catedraRestClient;
    private final RetryTemplate catedraRetryTemplate;
    private final CatalogSnapshotDtoMapper catalogSnapshotDtoMapper;

    @Override
    public CatalogSnapshot fetchSnapshot() {
        try {
            CatalogSnapshotResponse response = catedraRetryTemplate.invoke(this::requestSnapshot);
            if (response == null) {
                throw new SyncException("La cátedra respondió el snapshot sin contenido");
            }
            log.info("Snapshot recibido: versión {}", response.getSnapshotVersion());
            return catalogSnapshotDtoMapper.toDomain(response);
        } catch (RestClientException e) {
            log.error("No se pudo obtener el snapshot de la cátedra", e);
            throw new SyncException("No se pudo obtener el snapshot de la cátedra", e);
        }
    }

    private CatalogSnapshotResponse requestSnapshot() {
        return catedraRestClient.get()
                .uri(SNAPSHOT_PATH)
                .retrieve()
                .body(CatalogSnapshotResponse.class);
    }
}
