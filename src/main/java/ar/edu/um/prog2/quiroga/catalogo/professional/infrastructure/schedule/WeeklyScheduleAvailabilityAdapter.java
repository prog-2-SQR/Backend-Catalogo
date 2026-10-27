package ar.edu.um.prog2.quiroga.catalogo.professional.infrastructure.schedule;

import java.time.DayOfWeek;
import java.util.Set;
import java.util.stream.Collectors;

import ar.edu.um.prog2.quiroga.catalogo.schedule.application.service.WeeklyScheduleService;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.out.ProfessionalAvailability;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Único punto donde professional usa al slice schedule (ADR 0005, opción "puerto de salida" de la skill):
 * entra por su API pública (WeeklyScheduleService) y no por su repositorio ni su infraestructura.
 */
@Component
@RequiredArgsConstructor
public class WeeklyScheduleAvailabilityAdapter implements ProfessionalAvailability {

    private final WeeklyScheduleService weeklyScheduleService;

    @Override
    public Set<Long> findProfessionalIdsAvailableOn(DayOfWeek day) {
        return weeklyScheduleService.getAllWeeklySchedules().stream()
                .filter(schedule -> schedule.isActiveOn(day))
                .map(WeeklySchedule::getProfessionalId)
                .collect(Collectors.toSet());
    }
}
