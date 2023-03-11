package os.expert.integration.microstream;


import jakarta.inject.Inject;
import jakarta.nosql.Template;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class MapperSelectTest {

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
    public void shouldExecuteSelectFrom() {
        List<Book> result = this.template.select(Book.class).result();
        assertThat(result).isNotEmpty()
                .containsAll(library());
    }

    @Test
    public void shouldSelectOrderAsc() {
        List<Book> result = this.template.select(Book.class).orderBy("isbn")
                .asc().result();

        assertThat(result).isNotEmpty()
                .containsExactly(library().toArray(Book[]::new));
    }

    @Test
    public void shouldSelectOrderDesc() {
        List<Book> result = this.template.select(Book.class).orderBy("isbn")
                .desc().result();
        List<Book> expected = library().stream().sorted(Comparator.comparing(Book::isbn).reversed())
                .collect(Collectors.toUnmodifiableList());
        assertThat(result).isNotEmpty()
                .containsExactly(expected.toArray(Book[]::new));
    }

    @Test
    public void shouldSelectLimit() {
        List<Book> result = this.template.select(Book.class).orderBy("isbn")
                .desc().limit(2).result();
        List<Book> expected = library().stream().sorted(Comparator.comparing(Book::isbn).reversed())
                .limit(2)
                .collect(Collectors.toUnmodifiableList());
        assertThat(result).isNotEmpty()
                .containsExactly(expected.toArray(Book[]::new));
    }

    @Test
    public void shouldSelectStart() {
        List<Book> result = this.template.select(Book.class).orderBy("isbn")
                .desc().limit(3).result();
        List<Book> expected = library().stream().sorted(Comparator.comparing(Book::isbn).reversed())
                .limit(3)
                .collect(Collectors.toUnmodifiableList());
        assertThat(result).isNotEmpty()
                .containsExactly(expected.toArray(Book[]::new));
    }


    @Test
    public void shouldSelectWhereEq() {
        List<Book> result = this.template.select(Book.class).where("title")
                .eq("Effective Java").result();
        List<Book> expected = library().stream().filter(b -> b.title().equals("Effective Java"))
                .collect(Collectors.toUnmodifiableList());
        assertThat(result).isNotEmpty().containsAll(expected);
    }


    @Test
    public void shouldSelectWhereGt() {
        List<Book> result = this.template.select(Book.class).where("edition")
                .gt(2).result();
        List<Book> expected = library().stream().filter(b -> b.edition()> 2)
                .collect(Collectors.toUnmodifiableList());
        assertThat(result).isNotEmpty().containsAll(expected);
    }

    @Test
    public void shouldSelectWhereGte() {
        List<Book> result = this.template.select(Book.class).where("edition")
                .gte(2).result();
        List<Book> expected = library().stream().filter(b -> b.edition()>= 2)
                .collect(Collectors.toUnmodifiableList());
        assertThat(result).isNotEmpty().containsAll(expected);
    }


    @Test
    public void shouldSelectWhereLt() {
        List<Book> result = this.template.select(Book.class).where("edition")
                .lt(2).result();
        List<Book> expected = library().stream().filter(b -> b.edition()< 2)
                .collect(Collectors.toUnmodifiableList());
        assertThat(result).isNotEmpty().containsAll(expected);
    }

    @Test
    public void shouldSelectWhereLte() {
        List<Book> result = this.template.select(Book.class).where("edition")
                .lte(2).result();
        List<Book> expected = library().stream().filter(b -> b.edition()<= 2)
                .collect(Collectors.toUnmodifiableList());
        assertThat(result).isNotEmpty().containsAll(expected);
    }


    @Test
    public void shouldSelectWhereNot() {
        List<Book> result = this.template.select(Book.class).where("title")
                .not().eq("Effective Java").result();
        List<Book> expected = library().stream().filter(b -> !b.title().equals("Effective Java"))
                .collect(Collectors.toUnmodifiableList());
        assertThat(result).isNotEmpty().containsAll(expected);
    }


    @Test
    public void shouldSelectWhereAnd() {
        List<Book> result = this.template.select(Book.class).where("title")
                .eq("Effective Java").and("edition").gt(2).result();

        Predicate<Book> effective = b -> b.title().equals("Effective Java");
        Predicate<Book> edition = b -> b.edition() > 2;
        List<Book> expected = library().stream().filter(effective.and(edition))
                .collect(Collectors.toUnmodifiableList());
        assertThat(result).isNotEmpty().containsAll(expected);
    }

    @Test
    public void shouldSelectWhereOr() {
        List<Book> result = this.template.select(Book.class).where("title")
                .eq("Effective Java").or("edition").gt(2).result();

        Predicate<Book> effective = b -> b.title().equals("Effective Java");
        Predicate<Book> edition = b -> b.edition() > 2;
        List<Book> expected = library().stream().filter(effective.or(edition))
                .collect(Collectors.toUnmodifiableList());
        assertThat(result).isNotEmpty().containsAll(expected);
    }

    @Test
    public void shouldResult() {
    }


    @Test
    public void shouldStream() {

    }

    @Test
    public void shouldSingleResult() {

    }

    @Test
    public void shouldReturnErrorSelectWhenOrderIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> template.select(Book.class).orderBy(null));
    }

    @Test
    public void shouldReturnErrorWhenDifferentType() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> template.select(String.class).orderBy(null));
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
