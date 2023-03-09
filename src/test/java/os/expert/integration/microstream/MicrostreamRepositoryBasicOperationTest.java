package os.expert.integration.microstream;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The Microstream's PageableRepository default methods")
public class MicrostreamRepositoryBasicOperationTest {


    private MicrostreamRepository<Book, String> library;

    private DataStructure data;

    private MicrostreamTemplate template;

    private EntityMetadata metadata;

    @BeforeEach
    public void setUp() {
        this.metadata = EntityMetadata.of(Book.class);
        this.data = new DataStructure();
        this.template = new MicrostreamTemplate(data, metadata);
        this.library =  new MicrostreamRepository<>(template);
    }

    @Test
    public void shouldSave() {

    }
}
