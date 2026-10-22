package ar.edu.um.prog2.quiroga.catalogo.schedule.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.um.prog2.quiroga.catalogo.schedule.application.exception.WeeklyScheduleException;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.in.FindWeeklyScheduleByIdUseCase;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.in.FindWeeklySchedulesByProfessionalUseCase;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.in.GetAllWeeklySchedulesUseCase;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.in.UpsertWeeklyScheduleUseCase;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeeklyScheduleService {

    private final GetAllWeeklySchedulesUseCase getAllWeeklySchedulesUseCase;
    private final FindWeeklyScheduleByIdUseCase findWeeklyScheduleByIdUseCase;
    private final FindWeeklySchedulesByProfessionalUseCase findWeeklySchedulesByProfessionalUseCase;
    private final UpsertWeeklyScheduleUseCase upsertWeeklyScheduleUseCase;

    public List<WeeklySchedule> getAllWeeklySchedules() {
        return getAllWeeklySchedulesUseCase.getAllWeeklySchedules();
    }

    public WeeklySchedule findWeeklyScheduleById(Long id) {
        return findWeeklyScheduleByIdUseCase.findWeeklyScheduleById(id)
                .orElseThrow(() -> new WeeklyScheduleException(id));
    }

    public List<WeeklySchedule> findWeeklySchedulesByProfessional(Long professionalId) {
        return findWeeklySchedulesByProfessionalUseCase.findWeeklySchedulesByProfessional(professionalId);
    }

    public WeeklySchedule upsertWeeklySchedule(WeeklySchedule weeklySchedule) {
        return upsertWeeklyScheduleUseCase.upsertWeeklySchedule(weeklySchedule);
    }
}
