package ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.persistence.entity;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "weekly_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyScheduleEntity {

    @Id
    private Long id;
    @Column(nullable = false)
    private Long professionalId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 9)
    private DayOfWeek dayOfWeek;
    @Column(nullable = false)
    private LocalTime startTime;
    @Column(nullable = false)
    private LocalTime endTime;
    @Column(nullable = false)
    private Integer slotDurationMinutes;
    @Column(nullable = false)
    private Boolean enabled;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;
}
