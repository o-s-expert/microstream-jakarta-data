/*
 *  Copyright (c) 2023 Otavio
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

package expert.os.integration.microstream;

import jakarta.nosql.Template;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

public class MapperDeleteTest {

    private DataStructure data;

    private Template template;

    @BeforeEach
    public void setUp() {
        this.data = new DataStructure();
        Entities entities = Entities.of(Set.of(Book.class, Car.class));
        this.template = new MicrostreamTemplate(data, entities);
        this.template.insert(library());
        this.template.insert(garage());
    }

    @Test
    public void shouldReturnDeleteFrom() {
        this.template.delete(Book.class).execute();
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(this.data.isEmpty()).isFalse();
            soft.assertThat(this.data.values()).noneMatch(Book.class::isInstance);
        });
    }

    @Test
    public void shouldDeleteWhereEq() {
        this.template.delete(Book.class).where("title")
                .eq("Effective Java").execute();
        List<Book> expected = library();
        expected.removeIf(b -> b.title().equals("Effective Java"));
        List<Book> result = this.template.select(Book.class).result();
        Assertions.assertThat(expected).isNotEmpty()
                .containsAll(result);
    }

    @Test
    public void shouldDeleteWhereGt() {
        this.template.delete(Book.class).where("edition")
                .gt(2).execute();
        List<Book> expected = library();
        expected.removeIf(b -> b.edition() > 2);
        List<Book> result = this.template.select(Book.class).result();
        Assertions.assertThat(expected).isNotEmpty()
                .containsAll(result);
    }

    @Test
    public void shouldDeleteWhereGte() {
        this.template.delete(Book.class).where("edition")
                .gte(2).execute();
        List<Book> expected = library();
        expected.removeIf(b -> b.edition() >= 2);
        List<Book> result = this.template.select(Book.class).result();
        Assertions.assertThat(expected).isNotEmpty()
                .containsAll(result);
    }

    @Test
    public void shouldDeleteWhereLt() {
        this.template.delete(Book.class).where("edition")
                .lt(2).execute();
        List<Book> expected = library();
        expected.removeIf(b -> b.edition() < 2);
        List<Book> result = this.template.select(Book.class).result();
        Assertions.assertThat(expected).isNotEmpty()
                .containsAll(result);
    }

    @Test
    public void shouldDeleteWhereLte() {
        this.template.delete(Book.class).where("edition")
                .lte(2).execute();
        List<Book> expected = library();
        expected.removeIf(b -> b.edition() <= 2);
        List<Book> result = this.template.select(Book.class).result();
        Assertions.assertThat(expected).isNotEmpty()
                .containsAll(result);
    }

    @Test
    public void shouldDeleteWhereNot() {
        this.template.delete(Book.class).where("edition")
                .not().lte(2).execute();
        List<Book> expected = library();
        expected.removeIf(b -> b.edition() > 2);
        List<Book> result = this.template.select(Book.class).result();
        Assertions.assertThat(expected).isNotEmpty()
                .containsAll(result);
    }


    @Test
    public void shouldDeleteWhereAnd() {
        this.template.delete(Book.class).where("title")
                .eq("Effective Java").and("active")
                .eq(true).execute();
        Predicate<Book> effectiveJava = b -> b.title().equals("Effective Java");
        Predicate<Book> active = Book::active;
        List<Book> expected = library();
        expected.removeIf(effectiveJava.and(active));
        List<Book> result = this.template.select(Book.class).result();
        Assertions.assertThat(expected).isNotEmpty()
                .containsAll(result);
    }

    @Test
    public void shouldDeleteWhereOr() {
        this.template.delete(Book.class).where("title")
                .eq("Effective Java").or("active")
                .eq(true).execute();
        Predicate<Book> effectiveJava = b -> b.title().equals("Effective Java");
        Predicate<Book> active = Book::active;
        List<Book> expected = library();
        expected.removeIf(effectiveJava.or(active));
        List<Book> result = this.template.select(Book.class).result();
        Assertions.assertThat(expected).isNotEmpty()
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

    private List<Car> garage() {
        List<Car> garage = new ArrayList<>();
        garage.add(Car.of("A10", "Ferrari", Year.of(1980)));
        garage.add(Car.of("B11", "Ferrari", Year.of(1980)));
        garage.add(Car.of("C12", "Ferrari", Year.of(1980)));
        garage.add(Car.of("D13", "Ferrari", Year.of(1980)));
        garage.add(Car.of("E14", "Ferrari", Year.of(1980)));
        return garage;
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
