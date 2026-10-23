package ar.edu.um.prog2.quiroga.catalogo.sync.support;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogCategory;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogProfessional;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogSnapshot;
import ar.edu.um.prog2.quiroga.catalogo.sync.domain.model.CatalogWeeklySchedule;

/** Un snapshot chico con una entidad de cada tipo, con los IDs del snapshot real. */
public final class SyncFixtures {

    public static final Instant CREATED = Instant.parse("2026-09-01T03:00:00Z");

    private SyncFixtures() {
    }

    public static CatalogCategory cardiologia() {
        return CatalogCategory.builder()
                .id(101L).name("Cardiologia").description("Atencion cardiovascular").enabled(true)
                .createdAt(CREATED).updatedAt(CREATED).build();
    }

    public static CatalogProfessional anaTorres() {
        return CatalogProfessional.builder()
                .id(201L).categoryId(101L).firstName("Ana").lastName("Torres").enabled(true)
                .createdAt(CREATED).updatedAt(CREATED).build();
    }

    public static CatalogWeeklySchedule mondayAfternoon() {
        return CatalogWeeklySchedule.builder()
                .id(301L).professionalId(201L).dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(12, 0)).endTime(LocalTime.of(15, 0)).slotDurationMinutes(30)
                .enabled(true).createdAt(CREATED).updatedAt(CREATED).build();
    }

    public static CatalogSnapshot snapshot(long version) {
        return CatalogSnapshot.builder()
                .snapshotVersion(version)
                .generatedAt(CREATED)
                .categories(List.of(cardiologia()))
                .professionals(List.of(anaTorres()))
                .weeklySchedules(List.of(mondayAfternoon()))
                .build();
    }
}
