package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.catalog.mapper;

import static ar.edu.um.prog2.quiroga.catalogo.sync.support.SyncFixtures.anaTorres;
import static ar.edu.um.prog2.quiroga.catalogo.sync.support.SyncFixtures.cardiologia;
import static ar.edu.um.prog2.quiroga.catalogo.sync.support.SyncFixtures.mondayAfternoon;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CatalogSlicesMapperTest {

    private final CatalogSlicesMapper catalogSlicesMapper = new CatalogSlicesMapper();

    // Los modelos de sync y de cada slice tienen los mismos campos: se comparan campo por campo.
    @Test
    void mapsEachEntityToTheModelOfItsSlice() {
        assertThat(catalogSlicesMapper.toCategory(cardiologia())).usingRecursiveComparison().isEqualTo(cardiologia());
        assertThat(catalogSlicesMapper.toProfessional(anaTorres())).usingRecursiveComparison().isEqualTo(anaTorres());
        assertThat(catalogSlicesMapper.toWeeklySchedule(mondayAfternoon())).usingRecursiveComparison().isEqualTo(mondayAfternoon());
    }

    @Test
    void nullInputsReturnNull() {
        assertThat(catalogSlicesMapper.toCategory(null)).isNull();
        assertThat(catalogSlicesMapper.toProfessional(null)).isNull();
        assertThat(catalogSlicesMapper.toWeeklySchedule(null)).isNull();
    }
}
