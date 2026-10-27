package ar.edu.um.prog2.quiroga.catalogo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// Habilita los @Scheduled (chequeo periódico de versiones del catálogo).
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
