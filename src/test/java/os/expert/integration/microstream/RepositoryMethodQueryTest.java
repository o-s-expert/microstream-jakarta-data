package os.expert.integration.microstream;


import jakarta.data.exceptions.MappingException;
import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.Sort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        assertThat(cleanCode)
                .isNotEmpty()
                .map(Book::title)
                .first()
                .isEqualTo("Clean Code");
    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldFindByTitleOrderByIsbn(List<Book> books) {
        this.library.saveAll(books);
        Set<Book> effectiveJava = this.library.findByTitleOrderByIsbn("Effective Java");

        assertThat(effectiveJava)
                .isNotEmpty()
                .hasSize(3)
                .map(Book::edition)
                .contains(1, 2, 3);
    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldFindByEditionLessThan(List<Book> books) {
        this.library.saveAll(books);
        Queue<Book> firstEdition = this.library.findByEditionLessThan(2);
        assertThat(firstEdition)
                .isNotEmpty()
                .hasSize(3)
                .map(Book::edition)
                .allMatch(p -> p == 1);
    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldFindByEditionLessThanEqual(List<Book> books) {
        this.library.saveAll(books);
        Stream<Book> editions = this.library.findByEditionLessThanEqual(2);

        assertThat(editions)
                .isNotEmpty()
                .hasSize(4)
                .map(Book::edition)
                .allMatch(p -> p <= 2);
    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldFindByEditionGreaterThan(List<Book> books) {
        this.library.saveAll(books);
        List<Book> thirdEditions = this.library.findByEditionGreaterThan(2);

        assertThat(thirdEditions)
                .isNotEmpty()
                .hasSize(1)
                .map(Book::edition)
                .allMatch(p -> p > 2);
    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldFindByEditionGreaterThanEqual(List<Book> books) {
        this.library.saveAll(books);
        List<Book> editions = this.library.findByEditionGreaterThanEqual(2);

        assertThat(editions)
                .isNotEmpty()
                .hasSize(2)
                .map(Book::edition)
                .allMatch(p -> p >= 2);

    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldReturnErrorWhenFindByEditionBetween(List<Book> books) {
        this.library.saveAll(books);
        assertThrows(UnsupportedOperationException.class, () ->
                this.library.findByEditionBetween(1, 2));
    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldFindByEditionIn(List<Book> books) {
        this.library.saveAll(books);
        List<Book> editions = this.library.findByEditionIn(List.of(3, 2));

        assertThat(editions)
                .isNotEmpty()
                .hasSize(2)
                .map(Book::edition)
                .allMatch(p -> p >= 2);
    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldReturnErrorWhenFindByEditionIn(List<Book> books) {
        this.library.saveAll(books);
        assertThrows(MappingException.class, () -> this.library.findByEditionIn(1));

    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldFindByTitleAndEdition(List<Book> books) {
        this.library.saveAll(books);
        List<Book> result = this.library.findByTitleAndEdition("Effective Java", 2);

        assertThat(result)
                .isNotEmpty()
                .hasSize(1)
                .allMatch(b -> b.title().equals("Effective Java"))
                .allMatch(b -> b.edition() == 2);
    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldFindByTitleOrEdition(List<Book> books) {
        this.library.saveAll(books);
        List<Book> result = this.library.findByTitleOrEdition("Effective Java", 1);
        Predicate<Book> isEffectiveJava = b -> b.title().equals("Effective Java");
        Predicate<Book> firstEdition = b -> b.edition() == 1;
        assertThat(result)
                .isNotEmpty()
                .hasSize(5)
                .allMatch(isEffectiveJava.or(firstEdition))
                .anyMatch(b -> b.edition() == 1);
    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldFindByEditionNot(List<Book> books) {
        this.library.saveAll(books);
        List<Book> result = this.library.findByEditionNot(1);
        assertThat(result)
                .isNotEmpty()
                .hasSize(2)
                .allMatch(b -> b.edition()> 1);
    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldFindByActiveTrue(List<Book> books) {
        this.library.saveAll(books);
        List<Book> result = this.library.findByActiveTrue();
        assertThat(result)
                .isNotEmpty()
                .hasSize(1)
                .allMatch(b -> b.active());
    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldFindByActiveFalse(List<Book> books) {
        this.library.saveAll(books);
        List<Book> result = this.library.findByActiveFalse();
        assertThat(result)
                .isNotEmpty()
                .hasSize(1)
                .allMatch(b -> b.active());
    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldFindByTitlePageable(List<Book> books) {
        this.library.saveAll(books);
        String title = "Effective Java";
        Pageable pageable = Pageable.ofSize(1).sortBy(Sort.asc("edition"));
        Page<Book> page = this.library.findByTitle(title, pageable);

        assertThat(page.content())
                .isNotEmpty()
                .hasSize(1)
                .allMatch(b -> b.title().equals(title))
                .allMatch(b -> b.edition() == 1);

        Page<Book> second = this.library.findByTitle(title, pageable.next());

        assertThat(second.content())
                .isNotEmpty()
                .hasSize(1)
                .allMatch(b -> b.title().equals(title))
                .allMatch(b -> b.edition() == 2);
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
        books.add(createBookActive(integer.incrementAndGet(), "Effective Java", "Joshua Bloch", 3,
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

    static Book createBookActive(Integer isbn, String title, String author, int edition, int year) {
        return Book.builder().isbn(isbn.toString()).title(title)
                .author(author).edition(edition)
                .release(Year.of(year)).active().build();
    }
}
