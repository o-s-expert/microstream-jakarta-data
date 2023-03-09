package os.expert.integration.microstream;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldSaveWhenDataDoesNotExist(Book book) {
        assertThat(data.isEmpty()).isTrue();
        this.library.save(book);
        assertThat(data.isEmpty()).isFalse();
    }

    @Test
    public void shouldReturnErrorWhenSaveIsNull() {
        assertThrows(NullPointerException.class, () -> this.library.save(null));
    }

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldSaveWhenDataExist(Book book) {
        assertThat(data.isEmpty()).isTrue();
        this.library.save(book);
        this.library.save(book);
        assertThat(data.isEmpty()).isFalse();
        assertThat(data.size()).isEqualTo(1);
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldSaveWhenDataDoesNotExist(List<Book> books) {
        assertThat(data.isEmpty()).isTrue();
        this.library.saveAll(books);
        assertThat(data.isEmpty()).isFalse();
        assertThat(data.size()).isEqualTo(books.size());
    }

    @Test
    public void shouldReturnErrorWhenSaveAllIsNull() {
        assertThrows(NullPointerException.class, () -> this.library.saveAll(null));
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldSaveWhenDataExist(List<Book> books) {
        assertThat(data.isEmpty()).isTrue();
        this.library.saveAll(books);
        this.library.saveAll(books);
        assertThat(data.isEmpty()).isFalse();
        assertThat(data.size()).isEqualTo(books.size());
    }


}
