package ar.edu.um.prog2.quiroga.catalogo.sync.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CatalogVersionsTest {

    private final CatalogVersions versions = CatalogVersions.builder().currentVersion(7L).oldestAvailableVersion(4L).build();

    @Test
    void aLocalVersionBelowTheOldestAvailableIsOutsideTheWindow() {
        assertThat(versions.isOutsideWindow(3)).isTrue();
        assertThat(versions.isOutsideWindow(4)).isFalse();
        assertThat(versions.isOutsideWindow(7)).isFalse();
    }

    @Test
    void thereIsSomethingNewOnlyIfTheCurrentVersionIsGreater() {
        assertThat(versions.isNewerThan(6)).isTrue();
        assertThat(versions.isNewerThan(7)).isFalse();
    }
}
