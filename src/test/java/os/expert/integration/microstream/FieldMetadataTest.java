package os.expert.integration.microstream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FieldMetadataTest {


    @Test
    public void shouldCreateEntityMetadata() {
        EntityMetadata metadata = EntityMetadata.of(Person.class);

        Person poliana = Person.of("poliana", "Poliana", LocalDate.now());
        FieldMetadata id = metadata.id();

        Object value = id.get(poliana);
        Assertions.assertEquals("poliana", value);
    }
}