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


import jakarta.data.exceptions.MappingException;
import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.Sort;
import one.microstream.collections.lazy.LazyHashMap;
import one.microstream.persistence.types.Persister;
import one.microstream.persistence.types.Storer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

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


    @BeforeEach
    public void setUp() {
        Entities entities = Entities.of(Set.of(Book.class, Car.class));
        Persister persister = Mockito.mock(Persister.class);
        Storer storer = Mockito.mock(Storer.class);
        Mockito.when(persister.createEagerStorer()).thenReturn(storer);
        DataStorage data = new DataStorage(new LazyHashMap<>(), persister);
        MicrostreamTemplate template = new MicrostreamTemplate(data, entities);
        this.library = RepositoryProxySupplier.INSTANCE.get(Library.class, template);
        template.insert(garage());
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

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldFindByTitleOrderByIsbn(List<Book> books) {
        this.library.saveAll(books);
        Set<Book> effectiveJava = this.library.findByTitleOrderByIsbn("Effective Java");

        Assertions.assertThat(effectiveJava)
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
        Assertions.assertThat(firstEdition)
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

        Assertions.assertThat(editions)
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

        Assertions.assertThat(thirdEditions)
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

        Assertions.assertThat(editions)
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

        Assertions.assertThat(editions)
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

        Assertions.assertThat(result)
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
        Assertions.assertThat(result)
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
        Assertions.assertThat(result)
                .isNotEmpty()
                .hasSize(2)
                .allMatch(b -> b.edition()> 1);
    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldFindByActiveTrue(List<Book> books) {
        this.library.saveAll(books);
        List<Book> result = this.library.findByActiveTrue();
        Assertions.assertThat(result)
                .isNotEmpty()
                .hasSize(1)
                .allMatch(Book::active);
    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldFindByActiveFalse(List<Book> books) {
        this.library.saveAll(books);
        List<Book> result = this.library.findByActiveFalse();
        Assertions.assertThat(result)
                .isNotEmpty()
                .hasSize(4)
                .noneMatch(Book::active);
    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldCountByActiveTrue(List<Book> books) {
        this.library.saveAll(books);
        Long result = this.library.countByActiveTrue();
        assertThat(result).isEqualTo(1L);
        this.library.deleteAll();
        assertThat(this.library.countByActiveTrue()).isEqualTo(0L);
    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldExistsByActiveTrue(List<Book> books) {
        this.library.saveAll(books);
        boolean result = this.library.existsByActiveTrue();
        assertThat(result).isTrue();
        this.library.deleteAll();
        assertThat(this.library.existsByActiveTrue()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldDeleteByActiveTrue(List<Book> books) {
        this.library.saveAll(books);
        this.library.deleteByActiveTrue();
        boolean result = this.library.existsByActiveTrue();
        assertThat(result).isFalse();

    }

    @ParameterizedTest
    @MethodSource("arguments")
    public void shouldFindByTitlePageable(List<Book> books) {
        this.library.saveAll(books);
        String title = "Effective Java";
        Pageable pageable = Pageable.ofSize(1).sortBy(Sort.asc("edition"));
        Page<Book> page = this.library.findByTitle(title, pageable);

        Assertions.assertThat(page.content())
                .isNotEmpty()
                .hasSize(1)
                .allMatch(b -> b.title().equals(title))
                .allMatch(b -> b.edition() == 1);

        Page<Book> second = this.library.findByTitle(title, pageable.next());

        Assertions.assertThat(second.content())
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

    private List<Car> garage() {
        List<Car> garage = new ArrayList<>();
        garage.add(Car.of("A10", "Ferrari", Year.of(1980)));
        garage.add(Car.of("B11", "Ferrari", Year.of(1980)));
        garage.add(Car.of("C12", "Ferrari", Year.of(1980)));
        garage.add(Car.of("D13", "Ferrari", Year.of(1980)));
        garage.add(Car.of("E14", "Ferrari", Year.of(1980)));
        return garage;
    }
}
