package ar.edu.um.prog2.quiroga.catalogo.shared.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;

import ar.edu.um.prog2.quiroga.catalogo.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.web.servlet.MockMvc;

/**
 * La cadena de seguridad real con PostgreSQL (Testcontainers). Los tokens se firman acá igual que en turnos:
 * HS512, claims sub y auth. Con el mismo secreto pasan; con otro, no.
 */
@SpringBootTest(properties = {"catedra.api-url=http://localhost:8090", "catedra.jwt=jwt-de-prueba",
        "catedra.sync-on-startup=false"})
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class CatalogSecurityTest {

    private static final String OTHER_SECRET = Base64.getEncoder().encodeToString(
            "otro-secreto-de-64-bytes-que-no-es-el-que-comparten-turnos-y-catalogo".getBytes());

    @Autowired
    private MockMvc mockMvc;

    @Value("${security.jwt.base64-secret}")
    private String sharedSecret;

    // Firma un token como lo hace turnos al iniciar sesión.
    private static String tokenSignedWith(String base64Secret) {
        SecretKeySpec key = new SecretKeySpec(Base64.getDecoder().decode(base64Secret), JwtConfig.ALGORITHM.getName());
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().subject("juan.perez").claim("auth", "ROLE_USER")
                .issuedAt(now).expiresAt(now.plusSeconds(3600)).build();
        return NimbusJwtEncoder.withSecretKey(key).algorithm(JwtConfig.ALGORITHM).build()
                .encode(JwtEncoderParameters.from(JwsHeader.with(JwtConfig.ALGORITHM).build(), claims)).getTokenValue();
    }

    @Test
    void withoutTokenTheApisAreClosed() throws Exception {
        mockMvc.perform(get("/api/categories")).andExpect(status().isUnauthorized());
    }

    @Test
    void aTokenIssuedByTurnosWithTheSharedSecretIsAccepted() throws Exception {
        mockMvc.perform(get("/api/categories").header("Authorization", "Bearer " + tokenSignedWith(sharedSecret)))
                .andExpect(status().isOk());
    }

    @Test
    void aTokenSignedWithAnotherSecretIsRejected() throws Exception {
        mockMvc.perform(get("/api/categories").header("Authorization", "Bearer " + tokenSignedWith(OTHER_SECRET)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void aMalformedTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/categories").header("Authorization", "Bearer no.es.un-jwt"))
                .andExpect(status().isUnauthorized());
    }

    // liveness ("el proceso está vivo") no depende de la cátedra: el health completo puede dar 503 si su Redis no responde.
    @Test
    void healthAndApiDocsArePublic() throws Exception {
        mockMvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk());
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
    }
}
