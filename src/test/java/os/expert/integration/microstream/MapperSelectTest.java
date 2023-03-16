/*
 *  Copyright (c) 2023 Otavio Santana
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 *
 */

package os.expert.integration.microstream;


import jakarta.data.exceptions.NonUniqueResultException;
import jakarta.nosql.Template;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        List<Book> result = this.template.select(Book.class).result();
        assertThat(result).isNotEmpty()
                .containsAll(library());
    }


    @Test
    public void shouldStream() {
        Stream<Book> result = this.template.select(Book.class).stream();
        assertThat(result).isNotEmpty()
                .containsAll(library());

    }

    @Test
    public void shouldSingleResult() {
        Optional<Book> book = this.template.select(Book.class).where("edition").eq(3).singleResult();
        assertThat(book).isPresent()
                .get().extracting(Book::edition)
                .isEqualTo(3);

        book = this.template.select(Book.class).where("edition").eq(5).singleResult();
        assertThat(book).isNotPresent();
    }

    @Test
    public void shouldReturnErrorWhenThereAreTwoResults() {
        Assertions.assertThrows(NonUniqueResultException.class,
                () ->  this.template.select(Book.class).singleResult());
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
