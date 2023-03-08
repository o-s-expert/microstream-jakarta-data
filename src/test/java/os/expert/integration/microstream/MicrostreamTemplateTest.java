package os.expert.integration.microstream;

import jakarta.nosql.Template;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Year;
import java.util.Collection;
import java.util.List;
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
    @MethodSource("book")
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
    @MethodSource("book")
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
        Assertions.assertThrows(NullPointerException.class, () -> template.insert((Object) null));
        Assertions.assertThrows(NullPointerException.class, () -> template.insert((Iterable<? extends Object>) null));
    }

    static Stream<Arguments> book() {
        return Stream.of(Arguments.of(Book.builder().isbn("1231").title("Clean Code").author("Robert Martin")
                .edition(1).release(Year.of(2020)).build()));
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