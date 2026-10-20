package ar.edu.um.prog2.quiroga.catalogo.category.infrastructure.web.handler;

import ar.edu.um.prog2.quiroga.catalogo.category.application.exception.CategoryException;
import ar.edu.um.prog2.quiroga.catalogo.shared.web.ApiProblems;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Traduce los errores del slice a problem+json. Tiene prioridad sobre el handler global. */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CategoryExceptionHandler {

    static final String NOT_FOUND = "CATEGORY_NOT_FOUND";

    @ExceptionHandler(CategoryException.class)
    public ProblemDetail handleNotFound(CategoryException ex) {
        log.warn(ex.getMessage());
        return ApiProblems.notFound(NOT_FOUND, ex.getMessage());
    }
}
