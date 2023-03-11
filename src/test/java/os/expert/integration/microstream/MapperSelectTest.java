package os.expert.integration.microstream;


import jakarta.inject.Inject;
import jakarta.nosql.Template;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class MapperSelectTest {

    private DataStructure data;

    private EntityMetadata metadata;

    private Template template;

    @BeforeEach
    public void setUp() {
        this.data = new DataStructure();
        this.metadata = EntityMetadata.of(Book.class);
        this.template = new MicrostreamTemplate(data, metadata);

    }



    @Test
    public void shouldExecuteSelectFrom() {
        this.template.insert(library());
        List<Book> result = this.template.select(Book.class).result();
        org.assertj.core.api.Assertions.assertThat()
    }

    @Test
    public void shouldSelectOrderAsc() {
    }

    @Test
    public void shouldSelectOrderDesc() {
    }

    @Test
    public void shouldSelectLimit() {
    }

    @Test
    public void shouldSelectStart() {
    }


    @Test
    public void shouldSelectWhereEq() {
    }


    @Test
    public void shouldSelectWhereGt() {
    }

    @Test
    public void shouldSelectWhereGte() {
    }


    @Test
    public void shouldSelectWhereLt() {
    }

    @Test
    public void shouldSelectWhereLte() {
    }

    @Test
    public void shouldSelectWhereBetween() {
    }

    @Test
    public void shouldSelectWhereNot() {
    }


    @Test
    public void shouldSelectWhereAnd() {
    }

    @Test
    public void shouldSelectWhereOr() {
    }

    @Test
    public void shouldConvertField() {
    }

    @Test
    public void shouldUseAttributeConverter() {
    }

    @Test
    public void shouldQueryByEmbeddable() {
    }

    @Test
    public void shouldQueryBySubEntity() {
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
