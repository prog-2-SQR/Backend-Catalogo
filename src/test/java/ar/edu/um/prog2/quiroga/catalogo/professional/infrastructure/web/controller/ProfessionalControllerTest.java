package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.controller;

import static ar.edu.um.prog2.quiroga.catalogo.professional.support.ProfessionalFixtures.anaTorres;
import static ar.edu.um.prog2.quiroga.catalogo.professional.support.ProfessionalFixtures.emilianoOrtiz;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ar.edu.um.prog2.quiroga.catalogo.professional.application.exception.ProfessionalException;
import ar.edu.um.prog2.quiroga.catalogo.professional.application.service.ProfessionalService;
import ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.web.mapper.ProfessionalDtoMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Solo la capa web: el service es un mock, el mapper de DTO es el real. */
@WebMvcTest(ProfessionalController.class)
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
}
