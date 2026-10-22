package ar.edu.um.prog2.quiroga.catalogo.schedule.application.exception;

public class WeeklyScheduleException extends RuntimeException {

    public WeeklyScheduleException() {
        super("Horario no encontrado");
    }

    public WeeklyScheduleException(Long id) {
        super("No existe el horario con id " + id);
    }
}
