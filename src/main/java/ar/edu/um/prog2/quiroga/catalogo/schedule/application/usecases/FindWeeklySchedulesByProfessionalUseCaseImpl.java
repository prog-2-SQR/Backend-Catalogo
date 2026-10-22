package ar.edu.um.prog2.quiroga.catalogo.schedule.application.usecases;

import java.util.List;

import org.springframework.stereotype.Component;

import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.in.FindWeeklySchedulesByProfessionalUseCase;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.out.WeeklyScheduleRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FindWeeklySchedulesByProfessionalUseCaseImpl implements FindWeeklySchedulesByProfessionalUseCase {

    private final WeeklyScheduleRepository weeklyScheduleRepository;

    @Override
    public List<WeeklySchedule> findWeeklySchedulesByProfessional(Long professionalId) {
        return weeklyScheduleRepository.findWeeklySchedulesByProfessional(professionalId);
    }
}
