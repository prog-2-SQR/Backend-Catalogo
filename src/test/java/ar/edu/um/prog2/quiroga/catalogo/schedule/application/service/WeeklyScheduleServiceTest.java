package ar.edu.um.prog2.quiroga.catalogo.schedule.application.service;

import static ar.edu.um.prog2.quiroga.catalogo.schedule.support.WeeklyScheduleFixtures.mondayAfternoon;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.quiroga.catalogo.schedule.application.exception.WeeklyScheduleException;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.in.FindWeeklyScheduleByIdUseCase;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.in.FindWeeklySchedulesByProfessionalUseCase;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.in.GetAllWeeklySchedulesUseCase;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.in.UpsertWeeklyScheduleUseCase;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeeklyScheduleServiceTest {

    @Mock
    private GetAllWeeklySchedulesUseCase getAllWeeklySchedulesUseCase;
    @Mock
    private FindWeeklyScheduleByIdUseCase findWeeklyScheduleByIdUseCase;
    @Mock
    private FindWeeklySchedulesByProfessionalUseCase findWeeklySchedulesByProfessionalUseCase;
    @Mock
    private UpsertWeeklyScheduleUseCase upsertWeeklyScheduleUseCase;

    @InjectMocks
    private WeeklyScheduleService weeklyScheduleService;

    @Test
    void getAllWeeklySchedulesDelegatesToTheUseCase() {
        when(getAllWeeklySchedulesUseCase.getAllWeeklySchedules()).thenReturn(List.of(mondayAfternoon()));

        assertThat(weeklyScheduleService.getAllWeeklySchedules()).hasSize(1);
    }

    @Test
    void findWeeklyScheduleByIdReturnsTheSchedule() {
        when(findWeeklyScheduleByIdUseCase.findWeeklyScheduleById(301L)).thenReturn(Optional.of(mondayAfternoon()));

        WeeklySchedule result = weeklyScheduleService.findWeeklyScheduleById(301L);

        assertThat(result.getProfessionalId()).isEqualTo(201L);
    }

    @Test
    void findWeeklyScheduleByIdThrowsWeeklyScheduleExceptionWhenItDoesNotExist() {
        when(findWeeklyScheduleByIdUseCase.findWeeklyScheduleById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> weeklyScheduleService.findWeeklyScheduleById(999L))
                .isInstanceOf(WeeklyScheduleException.class)
                .hasMessageContaining("999");
    }

    @Test
    void findWeeklySchedulesByProfessionalDelegatesToTheUseCase() {
        when(findWeeklySchedulesByProfessionalUseCase.findWeeklySchedulesByProfessional(201L)).thenReturn(List.of(mondayAfternoon()));

        assertThat(weeklyScheduleService.findWeeklySchedulesByProfessional(201L)).hasSize(1);
    }

    @Test
    void upsertWeeklyScheduleDelegatesToTheUseCase() {
        WeeklySchedule schedule = mondayAfternoon();
        when(upsertWeeklyScheduleUseCase.upsertWeeklySchedule(schedule)).thenReturn(schedule);

        assertThat(weeklyScheduleService.upsertWeeklySchedule(schedule)).isSameAs(schedule);
    }

    @Test
    void weeklyScheduleExceptionWithoutIdHasAGenericMessage() {
        assertThat(new WeeklyScheduleException()).hasMessage("Horario no encontrado");
    }
}
