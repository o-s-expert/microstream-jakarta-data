package os.expert.integration.microstream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class LibraryArgumentProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(Arguments.of(library());
    }

    private List<Book> library() {
        List<Book> books = new ArrayList<>();
        AtomicInteger integer = new AtomicInteger(10);

        books.add(createBook(integer.incrementAndGet(),"Clean Code","Robert Martin",1,
                2020));
        books.add(createBook(integer.incrementAndGet(),"Effective Java","Joshua Bloch",1,
                2001));
        books.add(createBook(integer.incrementAndGet(),"Effective Java","Joshua Bloch",2,
                2008));
        books.add(createBook(integer.incrementAndGet(),"Effective Java","Joshua Bloch",3,
                2017));
        books.add(createBook(integer.incrementAndGet(),"Modern Software Engineering","David Farley",1,
                2020));

        return books;
    }
    private Book createBook(Integer isbn, String title, String author, int edition, int year) {
        return Book.builder().isbn(isbn.toString()).title(title)
                .author(author).edition(edition)
                .release(Year.of(year)).build();
    }
}
