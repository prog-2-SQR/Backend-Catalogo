package ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.web.controller;

import static ar.edu.um.prog2.quiroga.catalogo.category.support.CategoryFixtures.cardiologia;
import static ar.edu.um.prog2.quiroga.catalogo.category.support.CategoryFixtures.pediatria;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ar.edu.um.prog2.quiroga.catalogo.category.application.exception.CategoryException;
import ar.edu.um.prog2.quiroga.catalogo.category.application.service.CategoryService;
import ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.web.mapper.CategoryDtoMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Solo la capa web: el service es un mock, el mapper de DTO es el real. */
@WebMvcTest(CategoryController.class)
// Sin los filtros de seguridad: acá se prueba el controller; la seguridad tiene su propio test (CatalogSecurityTest).
@AutoConfigureMockMvc(addFilters = false)
@Import(CategoryDtoMapper.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void getAllCategoriesReturns200WithTheList() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of(cardiologia(), pediatria()));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[1].enabled").value(false));
    }

    @Test
    void getCategoryByIdReturns200WithOnlyThePublicFields() throws Exception {
        when(categoryService.findCategoryById(101L)).thenReturn(cardiologia());

        mockMvc.perform(get("/api/categories/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cardiologia"))
                .andExpect(jsonPath("$.createdAt").doesNotExist());
    }

    @Test
    void getCategoryByIdReturns404ProblemJsonWhenItDoesNotExist() throws Exception {
        when(categoryService.findCategoryById(999L)).thenThrow(new CategoryException(999L));

        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("No existe la categoría con id 999"))
                .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
    }

    @Test
    void getCategoryByIdReturns400WhenTheIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/categories/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
