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

package os.expert.integration.microstream;

import jakarta.nosql.Template;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
    @ArgumentsSource(BooksArgumentProvider.class)
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
    @ArgumentsSource(BooksArgumentProvider.class)
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

}