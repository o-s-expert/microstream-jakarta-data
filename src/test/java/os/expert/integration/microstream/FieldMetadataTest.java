package os.expert.integration.microstream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FieldMetadataTest {


    @Test
    public void shouldGetFromId() {
        EntityMetadata metadata = EntityMetadata.of(Person.class);

        Person poliana = Person.of("poliana", "Poliana", LocalDate.now());
        FieldMetadata id = metadata.id();

        Object value = id.get(poliana);
        Assertions.assertEquals("poliana", value);
    }

    @Test
    public void shouldGetFromColumn() {
        EntityMetadata metadata = EntityMetadata.of(Person.class);

        Person poliana = Person.of("poliana", "Poliana", LocalDate.now());
        List<FieldMetadata> fields = metadata.fields();

        FieldMetadata field = fields.get(0);
        Object value = field.get(poliana);

        Assertions.assertEquals("Poliana", value);

    }

    @Test
    public void shouldOrderByField() {
        EntityMetadata metadata = EntityMetadata.of(Person.class);
        Person poliana = Person.of("poliana", "Poliana", LocalDate.now());
        Person otavio = Person.of("otavio", "Poliana", LocalDate.now());
        Person ada = Person.of("ada", "Poliana", LocalDate.now());
        FieldMetadata id = metadata.id();
        Comparator<Person> comparator =  id.comparator();
        List<Person> people = Stream.of(poliana, otavio, ada)
                .sorted(comparator).collect(Collectors.toUnmodifiableList());

        assertThat(people)
                .isNotEmpty()
                .hasSize(3)
                .containsExactly(ada, otavio, poliana);
    }

}