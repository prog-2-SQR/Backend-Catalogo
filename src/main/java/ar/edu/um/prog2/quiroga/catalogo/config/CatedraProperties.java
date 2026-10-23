package ar.edu.um.prog2.quiroga.catalogo.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

// Acceso al servicio de la cátedra (anexo §3 y §4). Sin toString (ni @Data) para que el JWT no llegue a un log.
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "catedra")
public class CatedraProperties {

    @NotBlank
    private String apiUrl;

    @NotBlank
    private String jwt;

    @NotNull
    private Duration connectTimeout = Duration.ofSeconds(5);

    @NotNull
    private Duration readTimeout = Duration.ofSeconds(10);
}
