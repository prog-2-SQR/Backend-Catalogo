package ar.edu.um.prog2.quiroga.catalogo.shared.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** El handler global con un controller propio del test: no depende de ningún slice. */
@WebMvcTest(controllers = GlobalExceptionHandlerTest.TestController.class)
// Sin los filtros de seguridad: acá se prueba el controller; la seguridad tiene su propio test (CatalogSecurityTest).
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandlerTest.TestController.class, GlobalExceptionHandler.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    static class TestController {

        @GetMapping("/test/boom")
        String boom() {
            throw new IllegalStateException("password=secreta en la base");
        }

        @GetMapping("/test/number")
        String number(@RequestParam Long value) {
            return String.valueOf(value);
        }
    }

    @Test
    void unexpectedErrorReturns500WithoutLeakingInternals() throws Exception {
        mockMvc.perform(get("/test/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.detail").value("Error interno del servidor"));
    }

    @Test
    void invalidParameterReturns400WithValidationErrorCode() throws Exception {
        mockMvc.perform(get("/test/number").param("value", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void missingParameterReturns400WithValidationErrorCode() throws Exception {
        mockMvc.perform(get("/test/number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void unknownRouteReturns404WithNotFoundCode() throws Exception {
        mockMvc.perform(get("/test/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }
}
