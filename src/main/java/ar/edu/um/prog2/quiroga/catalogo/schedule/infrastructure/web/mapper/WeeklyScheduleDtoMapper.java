package ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.web.mapper;

import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.web.dto.WeeklyScheduleResponse;
import org.springframework.stereotype.Component;

@Component
public class WeeklyScheduleDtoMapper {

    public WeeklyScheduleResponse toResponse(WeeklySchedule domain) {
        if (domain == null) return null;
        return WeeklyScheduleResponse.builder()
                .id(domain.getId())
                .professionalId(domain.getProfessionalId())
                .dayOfWeek(domain.getDayOfWeek())
                .startTime(domain.getStartTime())
                .endTime(domain.getEndTime())
                .slotDurationMinutes(domain.getSlotDurationMinutes())
                .enabled(domain.getEnabled())
                .build();
    }
}
