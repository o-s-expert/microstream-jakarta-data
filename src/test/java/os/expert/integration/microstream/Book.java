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

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.time.Year;
import java.util.Objects;

@Entity
public class Book {


    @Id
    private String isbn;
    @Column
    private String title;

    @Column
    private Integer edition;

    @Column
    private Year release;

    @Column
    private String author;

    @Column
    private boolean active;

    Book(String isbn, String title, Integer edition, Year release, String author, boolean active) {
        this.isbn = isbn;
        this.title = title;
        this.edition = edition;
        this.release = release;
        this.author = author;
        this.active = active;
    }

    Book() {
    }

    public String isbn() {
        return isbn;
    }

    public String title() {
        return title;
    }

    public Integer edition() {
        return edition;
    }

    public Year release() {
        return release;
    }

    public String author() {
        return author;
    }

    public boolean active() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(isbn);
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", edition=" + edition +
                ", release=" + release +
                ", author='" + author + '\'' +
                ", active=" + active +
                '}';
    }

    public static BookBuilder builder() {
        return new BookBuilder();
    }
}
