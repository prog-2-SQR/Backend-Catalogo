package ar.edu.um.prog2.quiroga.catalogo.professional.domain.ports.out;

import java.time.DayOfWeek;
import java.util.Set;

/** Qué profesionales atienden un día de la semana. Los horarios no son de este slice (ADR 0005). */
public interface ProfessionalAvailability {

    Set<Long> findProfessionalIdsAvailableOn(DayOfWeek day);
}
