package os.expert.integration.microstream;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

@DisplayName("The Microstream's PageableRepository query by methods features")
public class RepositoryMethodQueryTest {

    private Library library;
    private DataStructure data;

    private MicrostreamTemplate template;

    private EntityMetadata metadata;


    @BeforeEach
    public void setUp() {
        this.metadata = EntityMetadata.of(Book.class);
        this.data = new DataStructure();
        this.template = new MicrostreamTemplate(data, metadata);
        this.library = RepositoryProxySupplier.INSTANCE.get(Library.class, template);
    }
}
