package os.expert.integration.microstream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
                .matches(m -> m.id().name().equals("id"))
                .extracting(m -> m.fields());
    }
}