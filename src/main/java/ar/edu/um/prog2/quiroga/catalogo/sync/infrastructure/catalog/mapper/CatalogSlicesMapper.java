package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.catalog.mapper;

import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogCategory;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogProfessional;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogWeeklySchedule;
import org.springframework.stereotype.Component;

/** Del modelo de sync al modelo de cada slice del catálogo (ADR 0004). */
@Component
public class CatalogSlicesMapper {

    public Category toCategory(CatalogCategory source) {
        if (source == null) return null;
        return Category.builder()
                .id(source.getId())
                .name(source.getName())
                .description(source.getDescription())
                .enabled(source.getEnabled())
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())
                .build();
    }

    public Professional toProfessional(CatalogProfessional source) {
        if (source == null) return null;
        return Professional.builder()
                .id(source.getId())
                .categoryId(source.getCategoryId())
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .enabled(source.getEnabled())
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())
                .build();
    }

    public WeeklySchedule toWeeklySchedule(CatalogWeeklySchedule source) {
        if (source == null) return null;
        return WeeklySchedule.builder()
                .id(source.getId())
                .professionalId(source.getProfessionalId())
                .dayOfWeek(source.getDayOfWeek())
                .startTime(source.getStartTime())
                .endTime(source.getEndTime())
                .slotDurationMinutes(source.getSlotDurationMinutes())
                .enabled(source.getEnabled())
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())
                .build();
    }
}
