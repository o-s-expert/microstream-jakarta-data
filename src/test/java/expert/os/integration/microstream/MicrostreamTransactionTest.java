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


import jakarta.inject.Inject;
import jakarta.nosql.Template;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@EnableAutoWeld
@AddPackages(value = ReturnType.class)
@AddPackages(Animal.class)
@AddExtensions(MicrostreamExtension.class)
public class MicrostreamTransactionTest {

    @Inject
    private Template template;

    @Inject
    private Library library;

    @Test
    public void shouldInsert() {
        Book book = Book.builder().isbn("1231").title("Clean Code").author("Robert Martin")
                .edition(1).release(Year.of(2020)).build();
        this.template.insert(book);
    }

    @Test
    public void shouldInsertList() {
        List<Book> books = new ArrayList<>();
        Book book = Book.builder().isbn("1231").title("Clean Code").author("Robert Martin")
                .edition(1).release(Year.of(2020)).build();
        books.add(book);
        books.add(Book.builder().isbn("12312342").title("Clean Code").author("Robert Martin")
                .edition(1).release(Year.of(2020)).build());
        books.add(book);
        this.template.insert(books);

        List<Book> result = this.template.select(Book.class).where("isbn")
                .in(List.of("1231", "12312342"))
                .result();

        Assertions.assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .extracting(Book::isbn)
                .contains("1231", "12312342");

    }

    @Test
    public void shouldSave() {
        Book book = Book.builder().isbn("1231").title("Clean Code").author("Robert Martin")
                .edition(1).release(Year.of(2020)).build();
        this.library.save(book);
    }
}
