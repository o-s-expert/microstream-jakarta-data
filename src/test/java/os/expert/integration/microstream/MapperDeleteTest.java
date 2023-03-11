package os.expert.integration.microstream;

import jakarta.nosql.Template;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapperDeleteTest {

    private DataStructure data;

    private EntityMetadata metadata;

    private Template template;

    @BeforeEach
    public void setUp() {
        this.data = new DataStructure();
        this.metadata = EntityMetadata.of(Book.class);
        this.template = new MicrostreamTemplate(data, metadata);
        this.template.insert(library());
    }

    @Test
    public void shouldReturnDeleteFrom() {
        this.template.delete(Book.class).execute();
        assertThat(this.data.isEmpty()).isTrue();
    }


    @Test
    public void shouldDeleteWhereEq() {
        this.template.delete(Book.class).where("title")
                .eq("Effective Java").execute();
        List<Book> expected = library();
        expected.removeIf(b -> b.title().equals("Effective Java"));
        List<Book> result = this.template.select(Book.class).result();
        assertThat(expected).isNotEmpty()
                .containsAll(result);
    }

    @Test
    public void shouldDeleteWhereGt() {
        this.template.delete(Book.class).where("edition")
                .gt(2).execute();
        List<Book> expected = library();
        expected.removeIf(b -> b.edition() > 2);
        List<Book> result = this.template.select(Book.class).result();
        assertThat(expected).isNotEmpty()
                .containsAll(result);
    }

    @Test
    public void shouldDeleteWhereGte() {
        this.template.delete(Book.class).where("edition")
                .gte(2).execute();
        List<Book> expected = library();
        expected.removeIf(b -> b.edition() >= 2);
        List<Book> result = this.template.select(Book.class).result();
        assertThat(expected).isNotEmpty()
                .containsAll(result);
    }

    @Test
    public void shouldDeleteWhereLt() {
        this.template.delete(Book.class).where("edition")
                .lt(2).execute();
        List<Book> expected = library();
        expected.removeIf(b -> b.edition() < 2);
        List<Book> result = this.template.select(Book.class).result();
        assertThat(expected).isNotEmpty()
                .containsAll(result);
    }

    @Test
    public void shouldDeleteWhereLte() {
        this.template.delete(Book.class).where("edition")
                .lte(2).execute();
        List<Book> expected = library();
        expected.removeIf(b -> b.edition() <= 2);
        List<Book> result = this.template.select(Book.class).result();
        assertThat(expected).isNotEmpty()
                .containsAll(result);
    }

    @Test
    public void shouldDeleteWhereNot() {
        this.template.delete(Book.class).where("edition")
                .not().lte(2).execute();
        List<Book> expected = library();
        expected.removeIf(b -> b.edition() > 2);
        List<Book> result = this.template.select(Book.class).result();
        assertThat(expected).isNotEmpty()
                .containsAll(result);
    }


    @Test
    public void shouldDeleteWhereAnd() {
        this.template.delete(Book.class).where("title")
                .eq("Effective Java").and("active")
                .eq(true).execute();
        Predicate<Book> effectiveJava = b -> b.title().equals("Effective Java");
        Predicate<Book> active = b -> b.active();
        List<Book> expected = library();
        expected.removeIf(effectiveJava.and(active));
        List<Book> result = this.template.select(Book.class).result();
        assertThat(expected).isNotEmpty()
                .containsAll(result);
    }

    @Test
    public void shouldDeleteWhereOr() {
        this.template.delete(Book.class).where("title")
                .eq("Effective Java").or("active")
                .eq(true).execute();
        Predicate<Book> effectiveJava = b -> b.title().equals("Effective Java");
        Predicate<Book> active = b -> b.active();
        List<Book> expected = library();
        expected.removeIf(effectiveJava.or(active));
        List<Book> result = this.template.select(Book.class).result();
        assertThat(expected).isNotEmpty()
                .containsAll(result);
    }

    private List<Book> library() {
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

    private Book createBook(Integer isbn, String title, String author, int edition, int year) {
        return Book.builder().isbn(isbn.toString()).title(title)
                .author(author).edition(edition)
                .release(Year.of(year)).build();
    }

    private Book createBookActive(Integer isbn, String title, String author, int edition, int year) {
        return Book.builder().isbn(isbn.toString()).title(title)
                .author(author).edition(edition)
                .release(Year.of(year)).active().build();
    }
}
