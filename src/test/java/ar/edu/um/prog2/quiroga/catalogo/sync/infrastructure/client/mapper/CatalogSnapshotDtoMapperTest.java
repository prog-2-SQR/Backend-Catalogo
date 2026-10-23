package ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogSnapshot;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogWeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.dto.CatalogCategoryResponse;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.dto.CatalogProfessionalResponse;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.dto.CatalogSnapshotResponse;
import ar.edu.um.prog2.quiroga.catalogo.sync.infrastructure.client.dto.CatalogWeeklyScheduleResponse;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class CatalogSnapshotDtoMapperTest {

    private static final Instant CREATED = Instant.parse("2026-09-01T03:00:00Z");

    private final CatalogSnapshotDtoMapper catalogSnapshotDtoMapper = new CatalogSnapshotDtoMapper();

    @Test
    void toDomainMapsVersionAndTheThreeCollections() {
        CatalogSnapshotResponse response = CatalogSnapshotResponse.builder()
                .snapshotVersion(7L)
                .generatedAt(CREATED)
                .professionalCategories(List.of(CatalogCategoryResponse.builder()
                        .id(101L).name("Cardiologia").description("Atencion cardiovascular").enabled(true)
                        .createdAt(CREATED).updatedAt(CREATED).build()))
                .professionals(List.of(CatalogProfessionalResponse.builder()
                        .id(201L).categoryId(101L).firstName("Ana").lastName("Torres").enabled(true)
                        .createdAt(CREATED).updatedAt(CREATED).build()))
                .weeklySchedules(List.of(CatalogWeeklyScheduleResponse.builder()
                        .id(301L).professionalId(201L).dayOfWeek(DayOfWeek.MONDAY)
                        .startTime(LocalTime.of(12, 0)).endTime(LocalTime.of(15, 0)).slotDurationMinutes(30)
                        .enabled(false).createdAt(CREATED).updatedAt(CREATED).build()))
                .build();

        CatalogSnapshot snapshot = catalogSnapshotDtoMapper.toDomain(response);

        assertThat(snapshot.getSnapshotVersion()).isEqualTo(7L);
        assertThat(snapshot.getGeneratedAt()).isEqualTo(CREATED);
        assertThat(snapshot.getCategories()).singleElement()
                .satisfies(category -> {
                    assertThat(category.getId()).isEqualTo(101L);
                    assertThat(category.getName()).isEqualTo("Cardiologia");
                    assertThat(category.getDescription()).isEqualTo("Atencion cardiovascular");
                    assertThat(category.getEnabled()).isTrue();
                    assertThat(category.getUpdatedAt()).isEqualTo(CREATED);
                });
        assertThat(snapshot.getProfessionals()).singleElement()
                .satisfies(professional -> {
                    assertThat(professional.getCategoryId()).isEqualTo(101L);
                    assertThat(professional.getFirstName()).isEqualTo("Ana");
                    assertThat(professional.getLastName()).isEqualTo("Torres");
                });
        CatalogWeeklySchedule schedule = snapshot.getWeeklySchedules().getFirst();
        assertThat(schedule.getProfessionalId()).isEqualTo(201L);
        assertThat(schedule.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(schedule.getStartTime()).isEqualTo(LocalTime.of(12, 0));
        assertThat(schedule.getEndTime()).isEqualTo(LocalTime.of(15, 0));
        assertThat(schedule.getSlotDurationMinutes()).isEqualTo(30);
        assertThat(schedule.getEnabled()).isFalse();
    }

    @Test
    void missingCollectionsBecomeEmptyLists() {
        CatalogSnapshot snapshot = catalogSnapshotDtoMapper.toDomain(
                CatalogSnapshotResponse.builder().snapshotVersion(1L).build());

        assertThat(snapshot.getCategories()).isEmpty();
        assertThat(snapshot.getProfessionals()).isEmpty();
        assertThat(snapshot.getWeeklySchedules()).isEmpty();
    }

    @Test
    void nullInputReturnsNull() {
        assertThat(catalogSnapshotDtoMapper.toDomain((CatalogSnapshotResponse) null)).isNull();
    }
}
