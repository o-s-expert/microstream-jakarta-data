package os.expert.integration.microstream;


import jakarta.inject.Inject;
import jakarta.nosql.Template;
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
        books.add(book);
        this.template.insert(books);
    }

    @Test
    public void shouldSave() {
        Book book = Book.builder().isbn("1231").title("Clean Code").author("Robert Martin")
                .edition(1).release(Year.of(2020)).build();
        this.library.save(book);
    }
}
