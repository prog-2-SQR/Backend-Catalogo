package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.web.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.service.SyncService;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.SyncStatus;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.web.mapper.SyncStatusDtoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Solo la capa web: el service es un mock, el mapper de DTO es el real. */
@WebMvcTest(SyncStatusController.class)
// Sin los filtros de seguridad: acá se prueba el controller; la seguridad tiene su propio test (CatalogSecurityTest).
@AutoConfigureMockMvc(addFilters = false)
@Import(SyncStatusDtoMapper.class)
class SyncStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SyncService syncService;

    @Test
    void returnsTheStateOfTheSynchronization() throws Exception {
        when(syncService.getSyncStatus()).thenReturn(SyncStatus.builder()
                .localVersion(1L).appliedAt(Instant.parse("2026-10-28T10:00:00Z")).publishedVersion(1L)
                .lastSuccessAt(Instant.parse("2026-10-28T10:05:00Z")).build());

        mockMvc.perform(get("/api/sync/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.health").value("UP_TO_DATE"))
                .andExpect(jsonPath("$.localVersion").value(1))
                .andExpect(jsonPath("$.publishedVersion").value(1))
                .andExpect(jsonPath("$.appliedAt").value("2026-10-28T10:00:00Z"))
                .andExpect(jsonPath("$.lastFailureMessage").isEmpty());
    }

    @Test
    void showsTheLastErrorWhileItIsFailing() throws Exception {
        when(syncService.getSyncStatus()).thenReturn(SyncStatus.builder()
                .localVersion(1L).publishedVersion(1L)
                .lastFailureAt(Instant.parse("2026-10-28T10:06:00Z")).lastFailureMessage("No se pudo leer el Redis de la cátedra").build());

        mockMvc.perform(get("/api/sync/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.health").value("FAILING"))
                .andExpect(jsonPath("$.lastFailureMessage").value("No se pudo leer el Redis de la cátedra"));
    }
}
