package os.expert.integration.microstream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class DataStructureTest {

    private DataStructure data;

    @BeforeEach
    public void setUp() {
        this.data = new DataStructure();
    }

    @Test
    public void shouldPut() {
        this.data.put("one", 1);
        this.data.put("two", 2);
        this.data.put("four", 4);

        Assertions.assertThat(this.data)
                .isNotNull()
                .matches(p -> p.size() == 3);

    }

    @Test
    public void shouldGet() {
        this.data.put("one", 1);
        Optional<Object> one = this.data.get("one");
        Assertions.assertThat(one)
                .isPresent()
                .get()
                .isEqualTo(1);

        Optional<Object> two = this.data.get("two");
        Assertions.assertThat(two)
                .isNotPresent();
    }

    @Test
    public void shouldRemove() {
        this.data.put("one", 1);
        Optional<Object> one = this.data.get("one");
        Assertions.assertThat(one)
                .isPresent()
                .get()
                .isEqualTo(1);

        this.data.remove("one");

        Assertions.assertThat(this.data.get("one"))
                .isNotPresent();
    }

    @Test
    public void shouldSize() {
        this.data.put("one", 1);
        this.data.put("two", 2);
        this.data.put("four", 4);

        Assertions.assertThat(this.data.size())
                .isEqualTo(3);
    }

    @Test
    public void shouldIsEmpty() {
        Assertions.assertThat(this.data.isEmpty())
                .isTrue();

        this.data.put("one", 1);
        this.data.put("two", 2);
        this.data.put("four", 4);

        Assertions.assertThat(this.data.isEmpty())
                .isFalse();

    }

    @Test
    public void shouldValue() {
        this.data.put("one", 1);
        this.data.put("two", 2);
        this.data.put("four", 4);

        Assertions.assertThat(this.data.values())
                .hasSize(3)
                .contains(1, 2, 4);
    }


}