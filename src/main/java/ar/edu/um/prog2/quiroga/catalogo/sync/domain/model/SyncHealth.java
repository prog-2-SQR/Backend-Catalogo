package ar.edu.um.prog2.quiroga.catalogo.sync.domain.model;

/** Resumen del estado de la copia local del catálogo. */
public enum SyncHealth {
    /** Todavía no se aplicó ningún snapshot. */
    NOT_INITIALIZED,
    /** El último intento de sincronizar falló (y no hubo uno bueno después). */
    FAILING,
    /** No se pudo saber qué versión publica la cátedra. */
    UNKNOWN,
    /** La cátedra publicó una versión más nueva que todavía no se aplicó. */
    OUTDATED,
    /** La copia local está en la última versión publicada. */
    UP_TO_DATE
}
