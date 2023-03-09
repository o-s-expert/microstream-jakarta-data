package os.expert.integration.microstream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.time.Year;
import java.util.List;
import java.util.stream.Stream;

public class BooksArgumentProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(Arguments.of(List.of(Book.builder().isbn("1231").title("Clean Code").author("Robert Martin")
                        .edition(1).release(Year.of(2020)).build(),
                Book.builder().isbn("1232").title("Effective Java").author("Joshua Bloch")
                        .edition(1).release(Year.of(2001)).build(),
                Book.builder().isbn("1233").title("Modern Software Engineering").author("David Farley")
                        .edition(1).release(Year.of(2020)).build())));
    }
}
