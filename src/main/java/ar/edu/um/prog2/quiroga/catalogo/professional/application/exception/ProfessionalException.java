package ar.edu.um.prog2.quiroga.catalogo.professional.application.exception;

public class ProfessionalException extends RuntimeException {

    public ProfessionalException() {
        super("Profesional no encontrado");
    }

    public ProfessionalException(Long id) {
        super("No existe el profesional con id " + id);
    }
}
