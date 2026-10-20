package ar.edu.um.prog2.quiroga.catalogo.schedule.application.usecases;

import java.util.Optional;

import org.springframework.stereotype.Component;

import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.in.FindWeeklyScheduleByIdUseCase;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.out.WeeklyScheduleRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FindWeeklyScheduleByIdUseCaseImpl implements FindWeeklyScheduleByIdUseCase {

    private final WeeklyScheduleRepository weeklyScheduleRepository;

    @Override
    public Optional<WeeklySchedule> findWeeklyScheduleById(Long id) {
        return weeklyScheduleRepository.findWeeklyScheduleById(id);
    }
}
