package os.expert.integration.microstream;

import jakarta.nosql.Template;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.Year;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.junit.jupiter.api.Assertions.*;

class MicrostreamTemplateTest {

    private DataStructure data;

    private EntityMetadata metadata;

    private Template template;

    @BeforeEach
    public void setUp() {
        this.data = new DataStructure();
        this.metadata = EntityMetadata.of(Book.class);
        this.template = new MicrostreamTemplate(data, metadata);

    }

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldInsert(Book book) {
        Book insert = this.template.insert(book);
        assertThat(insert)
                .isNotNull()
                .isEqualTo(book);
    }

    @ParameterizedTest
    @MethodSource("books")
    public void shouldInsert(List<Book> books) {
        Iterable<Book> insert = this.template.insert(books);
        assertThat(insert).hasSize(3)
                .isNotEmpty()
                .contains(books.toArray(Book[]::new));
    }

    @Test
    public void shouldReturnErrorWhenInsert() {
        Assertions.assertThrows(NullPointerException.class, () -> template.insert((Object) null));
        Assertions.assertThrows(NullPointerException.class, () -> template.insert((Iterable<? extends Object>) null));
    }

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldUpdate(Book book) {
        Book insert = this.template.update(book);
        assertThat(insert)
                .isNotNull()
                .isEqualTo(book);
    }

    @ParameterizedTest
    @MethodSource("books")
    public void shouldUpdate(List<Book> books) {
        Iterable<Book> insert = this.template.update(books);
        assertThat(insert).hasSize(3)
                .isNotEmpty()
                .contains(books.toArray(Book[]::new));
    }

    @Test
    public void shouldReturnErrorWhenUpdate() {
        Assertions.assertThrows(NullPointerException.class, () -> template.update((Object) null));
        Assertions.assertThrows(NullPointerException.class, () -> template.update((Iterable<? extends Object>) null));
    }

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldUnsupportedWhenItHasTtl(Book book) {
        Assertions.assertThrows(UnsupportedOperationException.class, () ->
                this.template.insert(book, Duration.ofSeconds(2L)));

        Assertions.assertThrows(UnsupportedOperationException.class, () ->
                this.template.insert(Collections.singleton(book), Duration.ofSeconds(2L)));
    }

    @Test
    public void shouldUnsupportedWhenItUsesCriteria() {
        Assertions.assertThrows(UnsupportedOperationException.class, () ->
                this.template.select(Book.class));

        Assertions.assertThrows(UnsupportedOperationException.class, () ->
                this.template.delete(Book.class));
    }

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldFindId(Book book) {
        this.template.insert(book);
        Optional<Book> optional = this.template.find(Book.class, book.isbn());
        assertThat(optional)
                .isPresent()
                .contains(book);
    }

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldFindNotId(Book book) {
        this.template.insert(book);
        Optional<Book> optional = this.template.find(Book.class, "no-isbn");
        assertThat(optional)
                .isNotPresent();
    }

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldDeleteId(Book book) {
        this.template.insert(book);
        Optional<Book> optional = this.template.find(Book.class, book.isbn());
        assertThat(optional)
                .isPresent()
                .contains(book);

        template.delete(Book.class, book.isbn());

        optional = this.template.find(Book.class, book.isbn());
        assertThat(optional)
                .isNotPresent();

    }

    static Stream<Arguments> books() {
        return Stream.of(Arguments.of(List.of(Book.builder().isbn("1231").title("Clean Code").author("Robert Martin")
                .edition(1).release(Year.of(2020)).build(),
                Book.builder().isbn("1232").title("Effective Java").author("Joshua Bloch")
                        .edition(1).release(Year.of(2001)).build(),
                Book.builder().isbn("1233").title("Modern Software Engineering").author("David Farley")
                        .edition(1).release(Year.of(2020)).build())));
    }
}