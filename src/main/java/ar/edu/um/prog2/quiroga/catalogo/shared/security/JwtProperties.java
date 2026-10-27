package ar.edu.um.prog2.quiroga.catalogo.shared.security;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

// Secreto compartido con el servicio de turnos para validar sus JWT. Sin toString para no loguearlo.
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    @NotBlank
    private String base64Secret;
}
