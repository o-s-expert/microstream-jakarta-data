package os.expert.integration.microstream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

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
}