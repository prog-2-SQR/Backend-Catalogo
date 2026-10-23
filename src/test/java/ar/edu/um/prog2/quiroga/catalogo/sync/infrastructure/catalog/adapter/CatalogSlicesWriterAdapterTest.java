package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.catalog.adapter;

import static ar.edu.um.prog2.quiroga.catalogo.sync.support.SyncFixtures.anaTorres;
import static ar.edu.um.prog2.quiroga.catalogo.sync.support.SyncFixtures.cardiologia;
import static ar.edu.um.prog2.quiroga.catalogo.sync.support.SyncFixtures.mondayAfternoon;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import ar.edu.um.prog2.quiroga.catalogo.category.application.service.CategoryService;
import ar.edu.um.prog2.quiroga.catalogo.category.domain.model.Category;
import ar.edu.um.prog2.quiroga.catalogo.professional.application.service.ProfessionalService;
import ar.edu.um.prog2.quiroga.catalogo.professional.domain.model.Professional;
import ar.edu.um.prog2.quiroga.catalogo.schedule.application.service.WeeklyScheduleService;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.catalog.mapper.CatalogSlicesMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatalogSlicesWriterAdapterTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProfessionalService professionalService;

    @Mock
    private WeeklyScheduleService weeklyScheduleService;

    private CatalogSlicesWriterAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CatalogSlicesWriterAdapter(categoryService, professionalService, weeklyScheduleService,
                new CatalogSlicesMapper());
    }

    @Test
    void saveCategoryUpsertsThroughTheCategoryService() {
        adapter.saveCategory(cardiologia());

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryService).upsertCategory(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(101L);
    }

    @Test
    void saveProfessionalUpsertsThroughTheProfessionalService() {
        adapter.saveProfessional(anaTorres());

        ArgumentCaptor<Professional> captor = ArgumentCaptor.forClass(Professional.class);
        verify(professionalService).upsertProfessional(captor.capture());
        assertThat(captor.getValue().getCategoryId()).isEqualTo(101L);
    }

    @Test
    void saveWeeklyScheduleUpsertsThroughTheScheduleService() {
        adapter.saveWeeklySchedule(mondayAfternoon());

        ArgumentCaptor<WeeklySchedule> captor = ArgumentCaptor.forClass(WeeklySchedule.class);
        verify(weeklyScheduleService).upsertWeeklySchedule(captor.capture());
        assertThat(captor.getValue().getProfessionalId()).isEqualTo(201L);
    }
}
