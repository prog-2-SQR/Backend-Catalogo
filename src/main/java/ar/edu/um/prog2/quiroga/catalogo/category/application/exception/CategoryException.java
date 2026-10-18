package ar.edu.um.prog2.quiroga.catalogo.category.application.exception;

public class CategoryException extends RuntimeException {

    public CategoryException() {
        super("Categoría no encontrada");
    }

    public CategoryException(Long id) {
        super("No existe la categoría con id " + id);
    }
}
