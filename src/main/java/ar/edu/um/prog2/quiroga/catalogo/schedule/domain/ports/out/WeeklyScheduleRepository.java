package ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.out;

import java.util.List;
import java.util.Optional;

import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;

public interface WeeklyScheduleRepository {

    Optional<WeeklySchedule> findWeeklyScheduleById(Long id);

    List<WeeklySchedule> findAllWeeklySchedules();

    List<WeeklySchedule> findWeeklySchedulesByProfessional(Long professionalId);

    WeeklySchedule upsertWeeklySchedule(WeeklySchedule weeklySchedule);
}
