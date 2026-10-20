package ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.in;

import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;

public interface UpsertWeeklyScheduleUseCase {

    WeeklySchedule upsertWeeklySchedule(WeeklySchedule weeklySchedule);
}
