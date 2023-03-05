package os.expert.integration.microstream;

import jakarta.data.exceptions.MappingException;
import jakarta.nosql.Column;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityMetadataTest {


    @Test
    public void shouldReturnErrorWhenIsNull() {
        assertThrows(NullPointerException.class, () -> EntityMetadata.of(null));
    }

    @Test
    public void shouldCreateEntityMetadata() {
        EntityMetadata metadata = EntityMetadata.of(Person.class);

        assertThat(metadata)
                .isNotNull()
                .matches(m -> m.type().equals(Person.class))
                .matches(m -> m.id().name().equals("id"));

        assertThat(metadata.fields())
                .hasSize(2)
                .extracting(FieldMetadata::name)
                .contains("name", "birthday");

    }

    @Test
    public void shouldCreateEntityMetadata2() {
        EntityMetadata metadata = EntityMetadata.of(Animal.class);

        assertThat(metadata)
                .isNotNull()
                .matches(m -> m.type().equals(Animal.class))
                .matches(m -> m.id().name().equals("id"));

        assertThat(metadata.fields())
                .hasSize(2)
                .extracting(FieldMetadata::name)
                .contains("name", "year");
    }

    @Test
    public void shouldReturnErrorWhenThereIsNotId() {
        assertThrows(MappingException.class, ()
                -> EntityMetadata.of(Car.class));
    }

    private static class Car {
        @Column
        private String plate;

        @Column
        private Year year;
    }
}