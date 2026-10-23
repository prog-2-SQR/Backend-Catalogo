package ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogCategory;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogProfessional;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogWeeklySchedule;

/** Escribe en la copia local del catálogo. sync no sabe qué slices guardan cada entidad (ADR 0004). */
public interface CatalogWriter {

    void saveCategory(CatalogCategory category);

    void saveProfessional(CatalogProfessional professional);

    void saveWeeklySchedule(CatalogWeeklySchedule weeklySchedule);
}
