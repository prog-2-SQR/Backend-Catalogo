package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.redis.mapper;

import java.util.List;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogChanges;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.redis.dto.CatalogChangesMessage;
import org.springframework.stereotype.Component;

@Component
public class CatalogChangesMapper {

    public CatalogChanges toDomain(CatalogChangesMessage message) {
        if (message == null) return null;
        CatalogChangesMessage.ChangedIds changes = message.getChanges() == null
                ? new CatalogChangesMessage.ChangedIds()
                : message.getChanges();
        return CatalogChanges.builder()
                .version(message.getVersion())
                .categoryIds(orEmpty(changes.getProfessionalCategories()))
                .professionalIds(orEmpty(changes.getProfessionals()))
                .weeklyScheduleIds(orEmpty(changes.getWeeklySchedules()))
                .build();
    }

    private static List<Long> orEmpty(List<Long> ids) {
        return ids == null ? List.of() : ids;
    }
}
