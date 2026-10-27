package ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Franja semanal de atención de un profesional (anexo §7). Las horas son locales de atención. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklySchedule {

    private Long id;
    /** Referencia por ID al profesional (ADR 0003): este slice no conoce el modelo de profesional. */
    private Long professionalId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDurationMinutes;
    private Boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;

    /** Atiende ese día: el horario es de ese día y está habilitado (los deshabilitados son bajas lógicas, anexo §14.4). */
    public boolean isActiveOn(DayOfWeek day) {
        return day == dayOfWeek && Boolean.TRUE.equals(enabled);
    }
}
