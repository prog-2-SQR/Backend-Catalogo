package ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.persistence.mapper;

import static ar.edu.um.prog2.quiroga.catalogo.schedule.support.WeeklyScheduleFixtures.mondayAfternoon;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.persistence.entity.WeeklyScheduleEntity;
import java.time.DayOfWeek;
import org.junit.jupiter.api.Test;

class WeeklyScheduleMapperTest {

    private final WeeklyScheduleMapper weeklyScheduleMapper = new WeeklyScheduleMapper();

    @Test
    void domainToEntityAndBackKeepsAllFields() {
        WeeklySchedule original = mondayAfternoon();

        WeeklyScheduleEntity entity = weeklyScheduleMapper.toEntity(original);
        WeeklySchedule back = weeklyScheduleMapper.toDomain(entity);

        assertThat(entity.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(back).usingRecursiveComparison().isEqualTo(original);
    }

    @Test
    void nullInputsReturnNull() {
        assertThat(weeklyScheduleMapper.toEntity(null)).isNull();
        assertThat(weeklyScheduleMapper.toDomain(null)).isNull();
    }
}
