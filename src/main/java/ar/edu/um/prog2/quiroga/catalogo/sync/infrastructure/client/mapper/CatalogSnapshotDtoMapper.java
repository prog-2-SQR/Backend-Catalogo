package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.mapper;

import java.util.List;
import java.util.function.Function;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogCategory;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogProfessional;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogSnapshot;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogWeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.dto.CatalogCategoryResponse;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.dto.CatalogProfessionalResponse;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.dto.CatalogSnapshotResponse;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.dto.CatalogWeeklyScheduleResponse;
import org.springframework.stereotype.Component;

@Component
public class CatalogSnapshotDtoMapper {

    public CatalogSnapshot toDomain(CatalogSnapshotResponse response) {
        if (response == null) return null;
        return CatalogSnapshot.builder()
                .snapshotVersion(response.getSnapshotVersion())
                .generatedAt(response.getGeneratedAt())
                .categories(mapAll(response.getProfessionalCategories(), this::toDomain))
                .professionals(mapAll(response.getProfessionals(), this::toDomain))
                .weeklySchedules(mapAll(response.getWeeklySchedules(), this::toDomain))
                .build();
    }

    public CatalogCategory toDomain(CatalogCategoryResponse response) {
        return CatalogCategory.builder()
                .id(response.getId())
                .name(response.getName())
                .description(response.getDescription())
                .enabled(response.getEnabled())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }

    public CatalogProfessional toDomain(CatalogProfessionalResponse response) {
        return CatalogProfessional.builder()
                .id(response.getId())
                .categoryId(response.getCategoryId())
                .firstName(response.getFirstName())
                .lastName(response.getLastName())
                .enabled(response.getEnabled())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }

    public CatalogWeeklySchedule toDomain(CatalogWeeklyScheduleResponse response) {
        return CatalogWeeklySchedule.builder()
                .id(response.getId())
                .professionalId(response.getProfessionalId())
                .dayOfWeek(response.getDayOfWeek())
                .startTime(response.getStartTime())
                .endTime(response.getEndTime())
                .slotDurationMinutes(response.getSlotDurationMinutes())
                .enabled(response.getEnabled())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }

    // Una colección ausente en el JSON se toma como vacía, igual que el valor por defecto del modelo.
    private static <S, T> List<T> mapAll(List<S> source, Function<S, T> mapper) {
        if (source == null) return List.of();
        return source.stream().map(mapper).toList();
    }
}
