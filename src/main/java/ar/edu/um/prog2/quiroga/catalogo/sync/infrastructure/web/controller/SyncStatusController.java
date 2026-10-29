package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.web.controller;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.service.SyncService;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.web.dto.SyncStatusResponse;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.web.mapper.SyncStatusDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncStatusController {

    private final SyncService syncService;
    private final SyncStatusDtoMapper syncStatusDtoMapper;

    // Enunciado §4.1: el catálogo informa el estado y los errores de la sincronización. Requiere JWT, como todo /api.
    @GetMapping("/status")
    public ResponseEntity<SyncStatusResponse> getSyncStatus() {
        return ResponseEntity.ok(syncStatusDtoMapper.toResponse(syncService.getSyncStatus()));
    }
}
