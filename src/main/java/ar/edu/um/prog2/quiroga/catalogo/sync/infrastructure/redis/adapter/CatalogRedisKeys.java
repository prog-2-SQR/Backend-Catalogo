package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.redis.adapter;

/** Claves de Redis de la cátedra (anexo §14). Solo lectura: la ACL no permite escribir en catedra:sync:*. */
final class CatalogRedisKeys {

    static final String METADATA = "catedra:sync:metadata";
    static final String CURRENT_VERSION_FIELD = "currentVersion";
    static final String OLDEST_VERSION_FIELD = "oldestAvailableVersion";
    static final String CHANGES_PREFIX = "catedra:sync:changes:";
    static final String CATEGORIES = "catedra:sync:professional-categories";
    static final String PROFESSIONALS = "catedra:sync:professionals";
    static final String WEEKLY_SCHEDULES = "catedra:sync:weekly-schedules";

    private CatalogRedisKeys() {
    }
}
