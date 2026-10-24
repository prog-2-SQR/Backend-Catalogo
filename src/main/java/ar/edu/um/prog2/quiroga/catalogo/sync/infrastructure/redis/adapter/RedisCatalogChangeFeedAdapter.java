package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.redis.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import ar.edu.um.prog2.quiroga.catalogo.sync.application.exception.SyncException;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogCategory;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogChanges;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogProfessional;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogVersions;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogWeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogChangeFeed;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.dto.CatalogCategoryResponse;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.dto.CatalogProfessionalResponse;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.dto.CatalogWeeklyScheduleResponse;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.mapper.CatalogSnapshotDtoMapper;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.redis.dto.CatalogChangesMessage;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.redis.mapper.CatalogChangesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * Lee Redis de la cátedra solo con comandos que permite su ACL (GET, HGETALL, HMGET).
 * Las entidades tienen el mismo JSON que en el snapshot REST, así que se reutilizan sus DTOs y su mapper.
 */
@Component
@RequiredArgsConstructor
public class RedisCatalogChangeFeedAdapter implements CatalogChangeFeed {

    private final StringRedisTemplate stringRedisTemplate;
    private final JsonMapper jsonMapper;
    private final CatalogSnapshotDtoMapper catalogSnapshotDtoMapper;
    private final CatalogChangesMapper catalogChangesMapper;

    @Override
    public Optional<CatalogVersions> findVersions() {
        Map<String, String> metadata = read(() -> hash().entries(CatalogRedisKeys.METADATA));
        if (metadata.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(CatalogVersions.builder()
                .currentVersion(parseVersion(metadata.get(CatalogRedisKeys.CURRENT_VERSION_FIELD)))
                .oldestAvailableVersion(parseVersion(metadata.get(CatalogRedisKeys.OLDEST_VERSION_FIELD)))
                .build());
    }

    @Override
    public Optional<CatalogChanges> findChanges(long version) {
        String json = read(() -> stringRedisTemplate.opsForValue().get(CatalogRedisKeys.CHANGES_PREFIX + version));
        return Optional.ofNullable(json)
                .map(value -> parse(value, CatalogChangesMessage.class))
                .map(catalogChangesMapper::toDomain);
    }

    @Override
    public List<CatalogCategory> findCategories(List<Long> ids) {
        return findEntities(CatalogRedisKeys.CATEGORIES, ids, CatalogCategoryResponse.class).stream()
                .map(catalogSnapshotDtoMapper::toDomain)
                .toList();
    }

    @Override
    public List<CatalogProfessional> findProfessionals(List<Long> ids) {
        return findEntities(CatalogRedisKeys.PROFESSIONALS, ids, CatalogProfessionalResponse.class).stream()
                .map(catalogSnapshotDtoMapper::toDomain)
                .toList();
    }

    @Override
    public List<CatalogWeeklySchedule> findWeeklySchedules(List<Long> ids) {
        return findEntities(CatalogRedisKeys.WEEKLY_SCHEDULES, ids, CatalogWeeklyScheduleResponse.class).stream()
                .map(catalogSnapshotDtoMapper::toDomain)
                .toList();
    }

    // Un HMGET para todos los IDs. Si falta alguno, los cambios no coinciden con el estado publicado.
    private <T> List<T> findEntities(String key, List<Long> ids, Class<T> type) {
        if (ids.isEmpty()) {
            return List.of();
        }
        List<String> fields = ids.stream().map(String::valueOf).toList();
        List<String> values = read(() -> hash().multiGet(key, fields));
        List<T> entities = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            if (values.get(i) == null) {
                throw new SyncException("Falta en Redis la entidad " + ids.get(i) + " de " + key);
            }
            entities.add(parse(values.get(i), type));
        }
        return entities;
    }

    private HashOperations<String, String, String> hash() {
        return stringRedisTemplate.opsForHash();
    }

    private static <T> T read(Supplier<T> operation) {
        try {
            return operation.get();
        } catch (DataAccessException e) {
            throw new SyncException("No se pudo leer el Redis de la cátedra", e);
        }
    }

    private <T> T parse(String json, Class<T> type) {
        try {
            return jsonMapper.readValue(json, type);
        } catch (JacksonException e) {
            throw new SyncException("JSON inválido en el Redis de la cátedra", e);
        }
    }

    private static Long parseVersion(String value) {
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            throw new SyncException("Versión inválida en el Redis de la cátedra: " + value, e);
        }
    }
}
