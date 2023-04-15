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


import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.Sort;
import one.microstream.collections.lazy.LazyHashMap;
import one.microstream.persistence.types.Persister;
import one.microstream.persistence.types.Storer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("The Microstream's PageableRepository default methods")
public class RepositoryBasicOperationsTest {


    private Library library;
    private DataStorage data;

    private MicrostreamTemplate template;

    @BeforeEach
    public void setUp() {
        Entities entities = Entities.of(Collections.singleton(Book.class));
        Persister persister = Mockito.mock(Persister.class);
        Storer storer = Mockito.mock(Storer.class);
        Mockito.when(persister.createEagerStorer()).thenReturn(storer);
        this.data = new DataStorage(new HashMap<>(), persister);
        this.template = new MicrostreamTemplate(data, entities);
        this.library = RepositoryProxySupplier.INSTANCE.get(Library.class, template);
    }

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldSaveWhenDataDoesNotExist(Book book) {
        assertThat(data.isEmpty()).isTrue();
        this.library.save(book);
        assertThat(data.isEmpty()).isFalse();
    }

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldSaveWhenDataExist(Book book) {
        assertThat(data.isEmpty()).isTrue();
        this.library.save(book);
        this.library.save(book);
        assertThat(data.isEmpty()).isFalse();
        assertThat(data.size()).isEqualTo(1);
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldSaveWhenDataDoesNotExist(List<Book> books) {
        assertThat(data.isEmpty()).isTrue();
        this.library.saveAll(books);
        assertThat(data.isEmpty()).isFalse();
        assertThat(data.size()).isEqualTo(books.size());
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldSaveWhenDataExist(List<Book> books) {
        assertThat(data.isEmpty()).isTrue();
        this.library.saveAll(books);
        this.library.saveAll(books);
        assertThat(data.isEmpty()).isFalse();
        assertThat(data.size()).isEqualTo(books.size());
    }

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldFindById(Book book) {
        this.library.save(book);
        Optional<Book> result = this.library.findById(book.isbn());
        Assertions.assertThat(result)
                .isPresent()
                .contains(book);
    }

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldNotFindById(Book book) {
        Optional<Book> result = this.library.findById(book.isbn());
        Assertions.assertThat(result).isNotPresent();
    }

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldExistById(Book book) {
        this.library.save(book);
        assertThat(this.library.existsById(book.isbn())).isTrue();
    }

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldFalseExistById(Book book) {
        assertThat(this.library.existsById(book.isbn())).isFalse();
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldNotFindByAllId(List<Book> books) {
        this.library.saveAll(books);

        Stream<Book> found = this.library.findAllById(books.stream().map(Book::isbn)
                .collect(toUnmodifiableList()));

        Assertions.assertThat(found)
                .hasSize(3)
                .containsAll(books);
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldNotFindByAllIdWhereThereIsInvalidIds(List<Book> books) {

        List<String> ids = new ArrayList<>();
        ids.add("invalid");
        ids.add("invalid-2");
        books.stream().map(Book::isbn).forEach(ids::add);

        this.library.saveAll(books);
        Stream<Book> found = this.library.findAllById(books.stream().map(Book::isbn)
                .collect(toUnmodifiableList()));

        Assertions.assertThat(found)
                .hasSize(3)
                .containsAll(books);
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldCount(List<Book> books) {
        assertThat(this.library.count()).isEqualTo(0L);
        this.library.saveAll(books);
        assertThat(this.library.count()).isEqualTo(books.size());
    }

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldDeleteById(Book book) {
        this.library.save(book);
        this.library.deleteById(book.isbn());

        Assertions.assertThat(library.findById(book.isbn()))
                .isNotPresent();
    }

    @ParameterizedTest
    @ArgumentsSource(BookArgumentProvider.class)
    public void shouldDelete(Book book) {
        this.library.save(book);
        this.library.delete(book);

        Assertions.assertThat(library.findById(book.isbn()))
                .isNotPresent();
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldDeleteAllById(List<Book> books) {
        this.library.saveAll(books);

        List<String> ids = books.stream().map(Book::isbn)
                .collect(toUnmodifiableList());
        this.library.deleteAllById(ids);

        Stream<Book> result = this.library.findAllById(ids);
        Assertions.assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldDeleteAllById2(List<Book> books) {
        this.library.saveAll(books);
        List<String> ids = new ArrayList<>();
        books.stream().skip(1).map(Book::isbn).forEach(ids::add);
        this.library.deleteAllById(ids);

        Stream<Book> result = this.library.findAllById(books.stream().map(Book::isbn).collect(toUnmodifiableList()));
        Assertions.assertThat(result).isNotEmpty().hasSize(1);
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldDeleteAll(List<Book> books) {
        this.library.saveAll(books);

        List<String> ids = books.stream().map(Book::isbn)
                .collect(toUnmodifiableList());
        this.library.deleteAll(books);

        Stream<Book> result = this.library.findAllById(ids);
        Assertions.assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldDeleteId2(List<Book> books) {
        this.library.saveAll(books);
        List<String> ids = new ArrayList<>();
        books.stream().skip(1).map(Book::isbn).forEach(ids::add);
        this.library.deleteAll(books.stream().skip(1).collect(toUnmodifiableList()));

        Stream<Book> result = this.library.findAllById(books.stream().map(Book::isbn).collect(toUnmodifiableList()));
        Assertions.assertThat(result).isNotEmpty().hasSize(1);
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldDeleteAll2(List<Book> books) {
        this.library.saveAll(books);
        this.library.deleteAll();

        assertThat(this.data.isEmpty()).isTrue();
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldFindAll(List<Book> books) {
        this.library.saveAll(books);
        Stream<Book> result = this.library.findAll();
        Assertions.assertThat(result).hasSize(books.size())
                .containsAll(books);
    }


    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldFindAllPagination(List<Book> books) {
        this.library.saveAll(books);
        Pageable pageable = Pageable.ofSize(1);
        Page<Book> page = this.library.findAll(pageable);
        assertThat(page.numberOfElements()).isEqualTo(1);
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldFindAllPaginationOrder(List<Book> books) {
        this.library.saveAll(books);
        Pageable pageable = Pageable.ofSize(3).sortBy(Sort.asc("title"));
        Page<Book> page = this.library.findAll(pageable);
        assertThat(page.numberOfElements()).isEqualTo(3);
        List<Book> result = books.stream().sorted(Comparator.comparing(Book::title))
                .collect(toUnmodifiableList());
        Assertions.assertThat(page.stream()).containsAll(result);
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldFindAllPaginationOrderReversed(List<Book> books) {
        this.library.saveAll(books);
        Pageable pageable = Pageable.ofSize(3).sortBy(Sort.desc("title"));
        Page<Book> page = this.library.findAll(pageable);
        assertThat(page.numberOfElements()).isEqualTo(3);
        List<Book> result = books.stream().sorted(Comparator.comparing(Book::title).reversed())
                .collect(toUnmodifiableList());
        Assertions.assertThat(page.stream()).containsAll(result);
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldFindAllPaginationOrderSkip(List<Book> books) {
        this.library.saveAll(books);
        Pageable pageable = Pageable.ofSize(2).page(2L).sortBy(Sort.desc("title"));
        Page<Book> page = this.library.findAll(pageable);
        assertThat(page.numberOfElements()).isEqualTo(1);
        List<Book> result = books.stream().sorted(Comparator.comparing(Book::title).reversed())
                .skip(2L)
                .collect(toUnmodifiableList());
        Assertions.assertThat(page.stream()).containsAll(result);
    }

    @ParameterizedTest
    @ArgumentsSource(BooksArgumentProvider.class)
    public void shouldFindAllPaginationDoubleOrder(List<Book> books) {
        this.library.saveAll(books);
        Pageable pageable = Pageable.ofSize(3).sortBy(Sort.asc("title"),
                Sort.asc("isbn"));
        Page<Book> page = this.library.findAll(pageable);
        assertThat(page.numberOfElements()).isEqualTo(3);
        List<Book> result = books.stream().sorted(Comparator.comparing(Book::title)
                        .thenComparing(Book::isbn))
                .collect(toUnmodifiableList());
        Assertions.assertThat(page.stream()).containsAll(result);
    }
}
