package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.catalog.adapter;

import ar.edu.um.prog2.quiroga.catalogo.category.application.service.CategoryService;
import ar.edu.um.prog2.quiroga.catalogo.professional.application.service.ProfessionalService;
import ar.edu.um.prog2.quiroga.catalogo.schedule.application.service.WeeklyScheduleService;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogCategory;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogProfessional;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogWeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.ports.out.CatalogWriter;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.catalog.mapper.CatalogSlicesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Único punto donde sync conoce a otros slices (ADR 0004, opción "puerto de salida" de la skill):
 * usa la API pública de cada uno (su service) y no toca sus repositorios ni su infraestructura.
 */
@Component
@RequiredArgsConstructor
public class CatalogSlicesWriterAdapter implements CatalogWriter {

    private final CategoryService categoryService;
    private final ProfessionalService professionalService;
    private final WeeklyScheduleService weeklyScheduleService;
    private final CatalogSlicesMapper catalogSlicesMapper;

    @Override
    public void saveCategory(CatalogCategory category) {
        categoryService.upsertCategory(catalogSlicesMapper.toCategory(category));
    }

    @Override
    public void saveProfessional(CatalogProfessional professional) {
        professionalService.upsertProfessional(catalogSlicesMapper.toProfessional(professional));
    }

    @Override
    public void saveWeeklySchedule(CatalogWeeklySchedule weeklySchedule) {
        weeklyScheduleService.upsertWeeklySchedule(catalogSlicesMapper.toWeeklySchedule(weeklySchedule));
    }
}
