package ar.edu.um.prog2.quiroga.catalogo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Si un listener falla (por ejemplo, la cátedra no responde), el mensaje se reintenta unas pocas veces con espera.
 * Si sigue fallando, se loguea y se confirma su offset para no trabar el topic: la sincronización periódica
 * recupera el atraso (anexo §16, "Kafka es una notificación, no la fuente del catálogo").
 * Spring Boot usa este bean en los listeners que crea.
 */
@Configuration
public class KafkaConfig {

    @Bean
    DefaultErrorHandler kafkaErrorHandler(CatedraProperties catedraProperties) {
        return new DefaultErrorHandler(
                new FixedBackOff(catedraProperties.getRetryDelay().toMillis(), catedraProperties.getMaxRetries()));
    }
}
