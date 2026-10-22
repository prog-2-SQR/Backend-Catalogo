package ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.persistence.mapper;

import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.persistence.entity.WeeklyScheduleEntity;
import org.springframework.stereotype.Component;

@Component
public class WeeklyScheduleMapper {

    public WeeklyScheduleEntity toEntity(WeeklySchedule domain) {
        if (domain == null) return null;
        return WeeklyScheduleEntity.builder()
                .id(domain.getId())
                .professionalId(domain.getProfessionalId())
                .dayOfWeek(domain.getDayOfWeek())
                .startTime(domain.getStartTime())
                .endTime(domain.getEndTime())
                .slotDurationMinutes(domain.getSlotDurationMinutes())
                .enabled(domain.getEnabled())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public WeeklySchedule toDomain(WeeklyScheduleEntity entity) {
        if (entity == null) return null;
        return WeeklySchedule.builder()
                .id(entity.getId())
                .professionalId(entity.getProfessionalId())
                .dayOfWeek(entity.getDayOfWeek())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .slotDurationMinutes(entity.getSlotDurationMinutes())
                .enabled(entity.getEnabled())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
