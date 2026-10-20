package ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.persistence.adapter;

import static ar.edu.um.prog2.quiroga.catalogo.schedule.support.WeeklyScheduleFixtures.mondayAfternoon;
import static ar.edu.um.prog2.quiroga.catalogo.schedule.support.WeeklyScheduleFixtures.otherProfessionalFriday;
import static ar.edu.um.prog2.quiroga.catalogo.schedule.support.WeeklyScheduleFixtures.wednesdayMorning;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.prog2.quiroga.catalogo.TestcontainersConfiguration;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.persistence.mapper.WeeklyScheduleMapper;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

/** Adapter contra un PostgreSQL real (Testcontainers) con la tabla creada por la migración de Liquibase. */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, JpaWeeklyScheduleRepositoryAdapter.class, WeeklyScheduleMapper.class})
class JpaWeeklyScheduleRepositoryAdapterTest {

    @Autowired
    private JpaWeeklyScheduleRepositoryAdapter adapter;

    @Test
    void upsertInsertsANewScheduleKeepingDayAndLocalTimes() {
        adapter.upsertWeeklySchedule(mondayAfternoon());

        assertThat(adapter.findWeeklyScheduleById(301L))
                .get()
                .usingRecursiveComparison()
                .isEqualTo(mondayAfternoon());
    }

    @Test
    void upsertUpdatesAnExistingScheduleInsteadOfDuplicatingIt() {
        adapter.upsertWeeklySchedule(mondayAfternoon());
        WeeklySchedule changed = mondayAfternoon();
        changed.setEndTime(LocalTime.of(16, 30));

        adapter.upsertWeeklySchedule(changed);

        assertThat(adapter.findAllWeeklySchedules()).hasSize(1);
        assertThat(adapter.findWeeklyScheduleById(301L)).get()
                .extracting(WeeklySchedule::getEndTime).isEqualTo(LocalTime.of(16, 30));
    }

    @Test
    void findWeeklySchedulesByProfessionalReturnsOnlyThatProfessionalsSchedules() {
        adapter.upsertWeeklySchedule(mondayAfternoon());
        adapter.upsertWeeklySchedule(wednesdayMorning());
        adapter.upsertWeeklySchedule(otherProfessionalFriday());

        assertThat(adapter.findWeeklySchedulesByProfessional(201L))
                .extracting(WeeklySchedule::getId)
                .containsExactlyInAnyOrder(301L, 302L);
        assertThat(adapter.findWeeklySchedulesByProfessional(999L)).isEmpty();
    }

    @Test
    void findWeeklyScheduleByIdReturnsEmptyWhenItDoesNotExist() {
        assertThat(adapter.findWeeklyScheduleById(999L)).isEmpty();
    }
}
