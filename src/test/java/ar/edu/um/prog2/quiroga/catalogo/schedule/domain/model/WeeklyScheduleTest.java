package ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model;

import static ar.edu.um.prog2.quiroga.catalogo.schedule.support.WeeklyScheduleFixtures.mondayAfternoon;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;

import org.junit.jupiter.api.Test;

class WeeklyScheduleTest {

    @Test
    void anEnabledScheduleIsActiveOnlyOnItsDay() {
        assertThat(mondayAfternoon().isActiveOn(DayOfWeek.MONDAY)).isTrue();
        assertThat(mondayAfternoon().isActiveOn(DayOfWeek.TUESDAY)).isFalse();
    }

    @Test
    void aDisabledScheduleIsNeverActive() {
        WeeklySchedule disabled = mondayAfternoon();
        disabled.setEnabled(false);

        assertThat(disabled.isActiveOn(DayOfWeek.MONDAY)).isFalse();
    }

    @Test
    void withoutADayItIsNotActive() {
        assertThat(mondayAfternoon().isActiveOn(null)).isFalse();
    }
}
