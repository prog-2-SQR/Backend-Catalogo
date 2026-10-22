package ar.edu.um.prog2.quiroga.catalogo.schedule.application.usecases;

import static ar.edu.um.prog2.quiroga.catalogo.schedule.support.WeeklyScheduleFixtures.mondayAfternoon;
import static ar.edu.um.prog2.quiroga.catalogo.schedule.support.WeeklyScheduleFixtures.wednesdayMorning;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.out.WeeklyScheduleRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeeklyScheduleUseCasesTest {

    @Mock
    private WeeklyScheduleRepository weeklyScheduleRepository;

    @Test
    void getAllWeeklySchedulesReturnsWhatTheRepositoryHas() {
        when(weeklyScheduleRepository.findAllWeeklySchedules()).thenReturn(List.of(mondayAfternoon(), wednesdayMorning()));

        List<WeeklySchedule> result = new GetAllWeeklySchedulesUseCaseImpl(weeklyScheduleRepository).getAllWeeklySchedules();

        assertThat(result).extracting(WeeklySchedule::getId).containsExactly(301L, 302L);
    }

    @Test
    void findWeeklyScheduleByIdReturnsTheScheduleWhenItExists() {
        when(weeklyScheduleRepository.findWeeklyScheduleById(301L)).thenReturn(Optional.of(mondayAfternoon()));

        Optional<WeeklySchedule> result = new FindWeeklyScheduleByIdUseCaseImpl(weeklyScheduleRepository).findWeeklyScheduleById(301L);

        assertThat(result).get().extracting(WeeklySchedule::getSlotDurationMinutes).isEqualTo(30);
    }

    @Test
    void findWeeklyScheduleByIdReturnsEmptyWhenItDoesNotExist() {
        when(weeklyScheduleRepository.findWeeklyScheduleById(999L)).thenReturn(Optional.empty());

        Optional<WeeklySchedule> result = new FindWeeklyScheduleByIdUseCaseImpl(weeklyScheduleRepository).findWeeklyScheduleById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findWeeklySchedulesByProfessionalDelegatesTheProfessionalId() {
        when(weeklyScheduleRepository.findWeeklySchedulesByProfessional(201L)).thenReturn(List.of(mondayAfternoon()));

        List<WeeklySchedule> result = new FindWeeklySchedulesByProfessionalUseCaseImpl(weeklyScheduleRepository)
                .findWeeklySchedulesByProfessional(201L);

        assertThat(result).hasSize(1);
        verify(weeklyScheduleRepository).findWeeklySchedulesByProfessional(201L);
    }

    @Test
    void upsertWeeklyScheduleDelegatesToTheRepository() {
        WeeklySchedule schedule = mondayAfternoon();
        when(weeklyScheduleRepository.upsertWeeklySchedule(schedule)).thenReturn(schedule);

        WeeklySchedule result = new UpsertWeeklyScheduleUseCaseImpl(weeklyScheduleRepository).upsertWeeklySchedule(schedule);

        assertThat(result).isSameAs(schedule);
    }
}
