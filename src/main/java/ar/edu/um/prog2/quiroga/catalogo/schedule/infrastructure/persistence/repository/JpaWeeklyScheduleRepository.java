package ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.persistence.repository;

import ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.persistence.entity.WeeklyScheduleEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaWeeklyScheduleRepository extends JpaRepository<WeeklyScheduleEntity, Long> {

    /** Consulta derivada: Spring Data arma el SQL a partir del nombre del método. */
    List<WeeklyScheduleEntity> findByProfessionalId(Long professionalId);
}
