package ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.in;

import java.util.Optional;

import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;

public interface FindWeeklyScheduleByIdUseCase {

    Optional<WeeklySchedule> findWeeklyScheduleById(Long id);
}
