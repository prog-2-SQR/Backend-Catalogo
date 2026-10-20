package ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.persistence.adapter;

import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.ports.out.WeeklyScheduleRepository;
import ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.persistence.entity.WeeklyScheduleEntity;
import ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.persistence.mapper.WeeklyScheduleMapper;
import ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.persistence.repository.JpaWeeklyScheduleRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaWeeklyScheduleRepositoryAdapter implements WeeklyScheduleRepository {

    private final JpaWeeklyScheduleRepository jpaWeeklyScheduleRepository;
    private final WeeklyScheduleMapper weeklyScheduleMapper;

    @Override
    public Optional<WeeklySchedule> findWeeklyScheduleById(Long id) {
        return jpaWeeklyScheduleRepository.findById(id)
                .map(weeklyScheduleMapper::toDomain);
    }

    @Override
    public List<WeeklySchedule> findAllWeeklySchedules() {
        return jpaWeeklyScheduleRepository.findAll()
                .stream()
                .map(weeklyScheduleMapper::toDomain)
                .toList();
    }

    @Override
    public List<WeeklySchedule> findWeeklySchedulesByProfessional(Long professionalId) {
        return jpaWeeklyScheduleRepository.findByProfessionalId(professionalId)
                .stream()
                .map(weeklyScheduleMapper::toDomain)
                .toList();
    }

    @Override
    public WeeklySchedule upsertWeeklySchedule(WeeklySchedule weeklySchedule) {
        WeeklyScheduleEntity entity = weeklyScheduleMapper.toEntity(weeklySchedule);
        WeeklyScheduleEntity saved = jpaWeeklyScheduleRepository.save(entity);
        return weeklyScheduleMapper.toDomain(saved);
    }
}
