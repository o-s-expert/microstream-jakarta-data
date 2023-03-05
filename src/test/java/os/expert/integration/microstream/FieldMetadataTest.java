package os.expert.integration.microstream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldMetadataTest {


    @Test
    public void shouldCreateEntityMetadata() {
        EntityMetadata metadata = EntityMetadata.of(Person.class);

    }
}