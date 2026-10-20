package ar.edu.um.prog2.quiroga.catalogo.schedule.application.usecases;

import java.util.List;

import org.springframework.stereotype.Component;

import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.in.GetAllWeeklySchedulesUseCase;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.out.WeeklyScheduleRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetAllWeeklySchedulesUseCaseImpl implements GetAllWeeklySchedulesUseCase {

    private final WeeklyScheduleRepository weeklyScheduleRepository;

    @Override
    public List<WeeklySchedule> getAllWeeklySchedules() {
        return weeklyScheduleRepository.findAllWeeklySchedules();
    }
}
