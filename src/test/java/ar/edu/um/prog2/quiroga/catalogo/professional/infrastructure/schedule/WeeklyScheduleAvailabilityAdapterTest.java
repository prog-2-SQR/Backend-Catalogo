package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.util.List;

import ar.edu.um.prog2.quiroga.catalogo.schedule.application.service.WeeklyScheduleService;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeeklyScheduleAvailabilityAdapterTest {

    @Mock
    private WeeklyScheduleService weeklyScheduleService;

    @InjectMocks
    private WeeklyScheduleAvailabilityAdapter adapter;

    private static WeeklySchedule schedule(long professionalId, DayOfWeek day, boolean enabled) {
        return WeeklySchedule.builder().professionalId(professionalId).dayOfWeek(day).enabled(enabled).build();
    }

    @Test
    void returnsTheProfessionalsWithAnEnabledScheduleThatDayWithoutRepeating() {
        when(weeklyScheduleService.getAllWeeklySchedules()).thenReturn(List.of(
                schedule(201, DayOfWeek.MONDAY, true),
                schedule(201, DayOfWeek.MONDAY, true),
                schedule(202, DayOfWeek.MONDAY, false),
                schedule(203, DayOfWeek.TUESDAY, true)));

        assertThat(adapter.findProfessionalIdsAvailableOn(DayOfWeek.MONDAY)).containsExactly(201L);
        assertThat(adapter.findProfessionalIdsAvailableOn(DayOfWeek.SUNDAY)).isEmpty();
    }
}
