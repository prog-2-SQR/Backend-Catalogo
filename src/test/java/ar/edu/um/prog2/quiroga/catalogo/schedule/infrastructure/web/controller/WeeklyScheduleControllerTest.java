package ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.web.controller;

import static ar.edu.um.prog2.quiroga.catalogo.schedule.support.WeeklyScheduleFixtures.mondayAfternoon;
import static ar.edu.um.prog2.quiroga.catalogo.schedule.support.WeeklyScheduleFixtures.wednesdayMorning;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ar.edu.um.prog2.quiroga.catalogo.schedule.application.exception.WeeklyScheduleException;
import ar.edu.um.prog2.quiroga.catalogo.schedule.application.service.WeeklyScheduleService;
import ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.web.mapper.WeeklyScheduleDtoMapper;
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
@WebMvcTest(WeeklyScheduleController.class)
// Sin los filtros de seguridad: acá se prueba el controller; la seguridad tiene su propio test (CatalogSecurityTest).
@AutoConfigureMockMvc(addFilters = false)
@Import(WeeklyScheduleDtoMapper.class)
class WeeklyScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeeklyScheduleService weeklyScheduleService;

    @Test
    void getWeeklySchedulesWithoutFilterReturnsAllInTheAnnexFormat() throws Exception {
        when(weeklyScheduleService.getAllWeeklySchedules()).thenReturn(List.of(mondayAfternoon(), wednesdayMorning()));

        mockMvc.perform(get("/api/schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$[0].startTime").value("12:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("15:00:00"))
                .andExpect(jsonPath("$[0].createdAt").doesNotExist());
        verify(weeklyScheduleService, never()).findWeeklySchedulesByProfessional(anyLong());
    }

    @Test
    void getWeeklySchedulesFiltersByProfessional() throws Exception {
        when(weeklyScheduleService.findWeeklySchedulesByProfessional(201L)).thenReturn(List.of(mondayAfternoon()));

        mockMvc.perform(get("/api/schedules").param("professionalId", "201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].professionalId").value(201));
        verify(weeklyScheduleService, never()).getAllWeeklySchedules();
    }

    @Test
    void getWeeklyScheduleByIdReturns200() throws Exception {
        when(weeklyScheduleService.findWeeklyScheduleById(301L)).thenReturn(mondayAfternoon());

        mockMvc.perform(get("/api/schedules/301"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slotDurationMinutes").value(30));
    }

    @Test
    void getWeeklyScheduleByIdReturns404ProblemJsonWhenItDoesNotExist() throws Exception {
        when(weeklyScheduleService.findWeeklyScheduleById(999L)).thenThrow(new WeeklyScheduleException(999L));

        mockMvc.perform(get("/api/schedules/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("No existe el horario con id 999"))
                .andExpect(jsonPath("$.code").value("WEEKLY_SCHEDULE_NOT_FOUND"));
    }

    @Test
    void invalidProfessionalIdReturns400() throws Exception {
        mockMvc.perform(get("/api/schedules").param("professionalId", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
