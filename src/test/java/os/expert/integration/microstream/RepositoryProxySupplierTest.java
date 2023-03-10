package os.expert.integration.microstream;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.PageableRepository;
import jakarta.nosql.Template;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RepositoryProxySupplierTest {


    private DataStructure data;

    private EntityMetadata metadata;

    private MicrostreamTemplate template;

    private RepositoryProxySupplier supplier = RepositoryProxySupplier.INSTANCE;

    @BeforeEach
    public void setUp() {
        this.data = new DataStructure();
        this.metadata = EntityMetadata.of(Book.class);
        this.template = new MicrostreamTemplate(data, metadata);

    }


    @Test
    public void shouldCreateCrudRepository() {
        Library library = this.supplier.get(Library.class, template);
        assertThat(library).isNotNull();
    }

    @Test
    public void shouldCreatePageableRepository() {
        BookRepository repository = this.supplier.get(BookRepository.class, template);
        assertThat(repository).isNotNull();
    }

    interface Library extends CrudRepository<Book, String> {

    }

    interface BookRepository extends PageableRepository<Book, String> {

    }

}