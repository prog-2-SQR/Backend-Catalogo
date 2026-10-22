package ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.web.mapper;

import static ar.edu.um.prog2.quiroga.catalogo.schedule.support.WeeklyScheduleFixtures.mondayAfternoon;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.web.dto.WeeklyScheduleResponse;
import java.time.DayOfWeek;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class WeeklyScheduleDtoMapperTest {

    private final WeeklyScheduleDtoMapper weeklyScheduleDtoMapper = new WeeklyScheduleDtoMapper();

    @Test
    void toResponseExposesOnlyThePublicFields() {
        WeeklyScheduleResponse response = weeklyScheduleDtoMapper.toResponse(mondayAfternoon());

        assertThat(response.getId()).isEqualTo(301L);
        assertThat(response.getProfessionalId()).isEqualTo(201L);
        assertThat(response.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(response.getStartTime()).isEqualTo(LocalTime.of(12, 0));
        assertThat(response.getEndTime()).isEqualTo(LocalTime.of(15, 0));
        assertThat(response.getSlotDurationMinutes()).isEqualTo(30);
        assertThat(response.getEnabled()).isTrue();
    }

    @Test
    void nullInputReturnsNull() {
        assertThat(weeklyScheduleDtoMapper.toResponse(null)).isNull();
    }
}
