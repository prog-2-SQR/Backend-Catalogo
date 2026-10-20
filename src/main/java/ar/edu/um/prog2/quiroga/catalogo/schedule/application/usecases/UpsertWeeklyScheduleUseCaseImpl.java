package ar.edu.um.prog2.quiroga.catalogo.schedule.application.usecases;

import org.springframework.stereotype.Component;

import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.in.UpsertWeeklyScheduleUseCase;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.out.WeeklyScheduleRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UpsertWeeklyScheduleUseCaseImpl implements UpsertWeeklyScheduleUseCase {

    private final WeeklyScheduleRepository weeklyScheduleRepository;

    @Override
    public WeeklySchedule upsertWeeklySchedule(WeeklySchedule weeklySchedule) {
        return weeklyScheduleRepository.upsertWeeklySchedule(weeklySchedule);
    }
}
