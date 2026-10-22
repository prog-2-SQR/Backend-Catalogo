package ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.in;

import java.util.List;

import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;

public interface FindWeeklySchedulesByProfessionalUseCase {

    List<WeeklySchedule> findWeeklySchedulesByProfessional(Long professionalId);
}
