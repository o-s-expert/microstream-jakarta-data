package os.expert.integration.microstream;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

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


    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldFindByTitle(List<Book> books) {
        this.library.saveAll(books);
        List<Book> cleanCode = this.library.findByTitle("Clean Code");

        Assertions.assertThat(cleanCode)
                .isNotEmpty()
                .map(Book::title)
                .first()
                .isEqualTo("Clean Code");
    }

    static Stream<? extends Arguments> arguments() {
        return Stream.of(Arguments.of(library()));
    }

    static List<Book> library() {
        List<Book> books = new ArrayList<>();
        AtomicInteger integer = new AtomicInteger(10);

        books.add(createBook(integer.incrementAndGet(), "Clean Code", "Robert Martin", 1,
                2020));
        books.add(createBook(integer.incrementAndGet(), "Effective Java", "Joshua Bloch", 1,
                2001));
        books.add(createBook(integer.incrementAndGet(), "Effective Java", "Joshua Bloch", 2,
                2008));
        books.add(createBook(integer.incrementAndGet(), "Effective Java", "Joshua Bloch", 3,
                2017));
        books.add(createBook(integer.incrementAndGet(), "Modern Software Engineering", "David Farley", 1,
                2020));

        return books;
    }

    static Book createBook(Integer isbn, String title, String author, int edition, int year) {
        return Book.builder().isbn(isbn.toString()).title(title)
                .author(author).edition(edition)
                .release(Year.of(year)).build();
    }
}
