package ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out;

import java.util.List;
import java.util.Optional;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogCategory;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogChanges;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogProfessional;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogVersions;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogWeeklySchedule;

/**
 * Cambios incrementales del catálogo que publica la cátedra (anexo §14). Si no se puede leer, o falta una
 * entidad que debería estar, lanza SyncException: el estado no se puede verificar.
 */
public interface CatalogChangeFeed {

    Optional<CatalogVersions> findVersions();

    /** Vacío si esa versión ya no está disponible (fuera de la ventana retenida). */
    Optional<CatalogChanges> findChanges(long version);

    List<CatalogCategory> findCategories(List<Long> ids);

    List<CatalogProfessional> findProfessionals(List<Long> ids);

    List<CatalogWeeklySchedule> findWeeklySchedules(List<Long> ids);
}
