package ar.edu.um.prog2.quiroga.catalogo.schedule.support;

import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;

/** Datos de prueba basados en el snapshot real de la cátedra. */
public final class WeeklyScheduleFixtures {

    public static final Instant CREATED_AT = Instant.parse("2026-09-01T03:00:00Z");
    public static final Instant UPDATED_AT = Instant.parse("2026-09-10T13:40:00Z");

    private WeeklyScheduleFixtures() {
    }

    /** Lunes de 12 a 15, turnos de 30 minutos, profesional 201. */
    public static WeeklySchedule mondayAfternoon() {
        return WeeklySchedule.builder()
                .id(301L)
                .professionalId(201L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(15, 0))
                .slotDurationMinutes(30)
                .enabled(true)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

    /** Miércoles de 9 a 12, turnos de 20 minutos, profesional 201, deshabilitado. */
    public static WeeklySchedule wednesdayMorning() {
        return WeeklySchedule.builder()
                .id(302L)
                .professionalId(201L)
                .dayOfWeek(DayOfWeek.WEDNESDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(12, 0))
                .slotDurationMinutes(20)
                .enabled(false)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

    /** Viernes de 8:30 a 11, profesional 202. */
    public static WeeklySchedule otherProfessionalFriday() {
        return WeeklySchedule.builder()
                .id(303L)
                .professionalId(202L)
                .dayOfWeek(DayOfWeek.FRIDAY)
                .startTime(LocalTime.of(8, 30))
                .endTime(LocalTime.of(11, 0))
                .slotDurationMinutes(30)
                .enabled(true)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }
}
