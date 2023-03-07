package os.expert.integration.microstream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

class ClassScannerTest {

    private ClassScanner scanner;

    @BeforeEach
    public void setUp() {
        this.scanner = ClassScanner.INSTANCE;
    }

    @Test
    public void shouldReturnEntity() {
        Set<Class<?>> entities = this.scanner.entities();
        Assertions.assertThat(entities).hasSize(1)
                .contains(Book.class);
    }

    @Test
    public void shouldReturnRepository() {
        Set<Class<?>> repositories = this.scanner.repositories();
        Assertions.assertThat(repositories).hasSize(1)
                .contains(Library.class);
    }

}