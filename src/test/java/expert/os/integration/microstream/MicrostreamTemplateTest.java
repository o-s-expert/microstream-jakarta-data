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

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MicrostreamTemplateTest {

    private MicrostreamTemplate template;

    @BeforeEach
    public void setUp() {
        DataStructure data = new DataStructure();
        Entities entities = Entities.of(Set.of(Book.class, Car.class));
        this.template = new MicrostreamTemplate(data, entities);

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
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldInsert(List<Book> books) {
        Iterable<Book> insert = this.template.insert(books);
        org.assertj.core.api.Assertions.assertThat(insert).hasSize(3)
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
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldUpdate(List<Book> books) {
        Iterable<Book> insert = this.template.update(books);
        org.assertj.core.api.Assertions.assertThat(insert).hasSize(3)
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

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldFindId(Book book) {
        this.template.insert(book);
        Optional<Book> optional = this.template.find(Book.class, book.isbn());
        org.assertj.core.api.Assertions.assertThat(optional)
                .isPresent()
                .contains(book);
    }

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldFindNotId(Book book) {
        this.template.insert(book);
        Optional<Book> optional = this.template.find(Book.class, "no-isbn");
        org.assertj.core.api.Assertions.assertThat(optional)
                .isNotPresent();
    }

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldDeleteId(Book book) {
        this.template.insert(book);
        Optional<Book> optional = this.template.find(Book.class, book.isbn());
        org.assertj.core.api.Assertions.assertThat(optional)
                .isPresent()
                .contains(book);

        template.delete(Book.class, book.isbn());

        optional = this.template.find(Book.class, book.isbn());
        org.assertj.core.api.Assertions.assertThat(optional)
                .isNotPresent();
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldDeleteAll(List<Book> books) {
        this.template.insert(books);
        assertThat(this.template.isEmpty()).isFalse();
        this.template.deleteAll();
        assertThat(this.template.isEmpty()).isTrue();
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldReturnEntities(List<Book> books) {
        this.template.insert(books);
        Stream<Book> entities = this.template.entities();
        org.assertj.core.api.Assertions.assertThat(entities).containsAll(books);
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldIsEmpty(List<Book> books) {
        assertThat(this.template.isEmpty()).isTrue();
        this.template.insert(books);
        assertThat(this.template.isEmpty()).isFalse();
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldSize(List<Book> books) {
        assertThat(this.template.isEmpty()).isTrue();
        assertThat(this.template.size()).isEqualTo(0);
        this.template.insert(books);
        assertThat(this.template.isEmpty()).isFalse();
        assertThat(this.template.size()).isEqualTo(books.size());
    }

    @ParameterizedTest
    @ArgumentsSource(BookCarArgumentProvider.class)
    public void shouldSaveSeveralEntities(Book book, Car car) {
        this.template.insert(book);
        this.template.insert(car);

        Optional<Book> bookOptional = this.template.find(Book.class, book.isbn());
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(bookOptional).isNotNull().isNotEmpty();
            bookOptional.ifPresent(b -> {
                soft.assertThat(b.isbn()).isEqualTo(book.isbn());
                soft.assertThat(b.title()).isEqualTo(book.title());
                soft.assertThat(b.active()).isEqualTo(book.active());
                soft.assertThat(b.release()).isEqualTo(book.release());
            });
        });

        Optional<Car> carOptional = this.template.find(Car.class, car.plate());
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(carOptional).isNotNull().isNotEmpty();
            carOptional.ifPresent(b -> {
                soft.assertThat(b.model()).isEqualTo(car.model());
                soft.assertThat(b.plate()).isEqualTo(car.plate());
                soft.assertThat(b.release()).isEqualTo(car.release());
            });
        });
    }

    //should save both books and cars
    //should save with conflifict
    //find when there id with car, but the id is Book
    //delete when there id with car, but the id is Book
}