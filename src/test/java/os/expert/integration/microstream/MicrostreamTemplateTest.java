package os.expert.integration.microstream;

import jakarta.nosql.Template;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MicrostreamTemplateTest {

    private DataStructure data;

    private EntityMetadata metadata;

    private Template template;

    @Test
    public void setUp() {
        this.data = new DataStructure();
        this.metadata = EntityMetadata.of(Book.class);
        this.template = new MicrostreamTemplate(data, metadata);

    }

}