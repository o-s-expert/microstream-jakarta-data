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

    Book(String isbn, String title, Integer edition, Year release, String author) {
        this.isbn = isbn;
        this.title = title;
        this.edition = edition;
        this.release = release;
        this.author = author;
    }

    Book() {
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
                '}';
    }

    public static BookBuilder builder() {
        return new BookBuilder();
    }
}
