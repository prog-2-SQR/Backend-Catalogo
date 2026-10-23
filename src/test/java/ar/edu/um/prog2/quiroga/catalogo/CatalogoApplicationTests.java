package ar.edu.um.prog2.quiroga.catalogo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = {"catedra.api-url=http://localhost:8090", "catedra.jwt=jwt-de-prueba",
        "catedra.sync-on-startup=false"})
class CatalogoApplicationTests {

	@Test
	void contextLoads() {
	}

}
