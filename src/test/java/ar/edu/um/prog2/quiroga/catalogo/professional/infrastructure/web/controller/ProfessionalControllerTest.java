package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.controller;

import static ar.edu.um.prog2.quiroga.catalogo.professional.support.ProfessionalFixtures.anaTorres;
import static ar.edu.um.prog2.quiroga.catalogo.professional.support.ProfessionalFixtures.emilianoOrtiz;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ar.edu.um.prog2.quiroga.catalogo.professional.application.exception.ProfessionalException;
import ar.edu.um.prog2.quiroga.catalogo.professional.application.service.ProfessionalService;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalPage;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.ProfessionalSearch;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.mapper.ProfessionalDtoMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Solo la capa web: el service es un mock, el mapper de DTO es el real. */
@WebMvcTest(ProfessionalController.class)
// Sin los filtros de seguridad: acá se prueba el controller; la seguridad tiene su propio test (CatalogSecurityTest).
@AutoConfigureMockMvc(addFilters = false)
@Import(ProfessionalDtoMapper.class)
class ProfessionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfessionalService professionalService;

    @Test
    void getAllProfessionalsReturns200WithTheList() throws Exception {
        when(professionalService.getAllProfessionals()).thenReturn(List.of(anaTorres(), emilianoOrtiz()));

        mockMvc.perform(get("/api/professionals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].categoryId").value(101))
                .andExpect(jsonPath("$[1].enabled").value(false));
    }

    @Test
    void getProfessionalByIdReturns200WithOnlyThePublicFields() throws Exception {
        when(professionalService.findProfessionalById(201L)).thenReturn(anaTorres());

        mockMvc.perform(get("/api/professionals/201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Torres"))
                .andExpect(jsonPath("$.updatedAt").doesNotExist());
    }

    @Test
    void getProfessionalByIdReturns404ProblemJsonWhenItDoesNotExist() throws Exception {
        when(professionalService.findProfessionalById(999L)).thenThrow(new ProfessionalException(999L));

        mockMvc.perform(get("/api/professionals/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("No existe el profesional con id 999"))
                .andExpect(jsonPath("$.code").value("PROFESSIONAL_NOT_FOUND"));
    }

    @Test
    void getProfessionalByIdReturns400WhenTheIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/professionals/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void searchPassesTheFiltersAndReturnsThePage() throws Exception {
        when(professionalService.searchProfessionals(any(ProfessionalSearch.class))).thenReturn(ProfessionalPage.builder()
                .content(List.of(anaTorres())).page(0).size(10).totalElements(1).build());

        mockMvc.perform(get("/api/professionals/search")
                        .param("categoryId", "101").param("name", "ana").param("enabled", "true").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(201))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0));
        ArgumentCaptor<ProfessionalSearch> search = ArgumentCaptor.forClass(ProfessionalSearch.class);
        verify(professionalService).searchProfessionals(search.capture());
        assertThat(search.getValue())
                .extracting("categoryId", "name", "enabled", "page", "size")
                .containsExactly(101L, "ana", true, 0, 10);
    }

    @Test
    void searchWithoutParametersUsesTheFirstPageOfTwenty() throws Exception {
        when(professionalService.searchProfessionals(any(ProfessionalSearch.class)))
                .thenReturn(ProfessionalPage.builder().page(0).size(20).build());

        mockMvc.perform(get("/api/professionals/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void anOutOfRangePageSizeReturns400() throws Exception {
        mockMvc.perform(get("/api/professionals/search").param("size", "500"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        mockMvc.perform(get("/api/professionals/search").param("page", "-1"))
                .andExpect(status().isBadRequest());
        verify(professionalService, never()).searchProfessionals(any());
    }
}
