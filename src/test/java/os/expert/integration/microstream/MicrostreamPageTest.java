package os.expert.integration.microstream;

import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;

class MicrostreamPageTest {

    private Person otavio = Person.of("otaviojava","Otavio",
            LocalDate.of(1988, Month.JANUARY, 9));
    @Test
    public void shouldReturnErrorWhenNull() {
        Assertions.assertThrows(NullPointerException.class, ()->
                MicrostreamPage.of(Collections.emptyList(), null));

        Assertions.assertThrows(NullPointerException.class, ()->
                MicrostreamPage.of(null, Pageable.ofPage(2)));
    }


    @Test
    public void shouldReturnUnsupportedOperation() {
        Page<Person> page = MicrostreamPage.of(Collections.singletonList(otavio),
                Pageable.ofPage(2));

        Assertions.assertThrows(UnsupportedOperationException.class, page::totalPages);

        Assertions.assertThrows(UnsupportedOperationException.class, page::totalElements);
    }

    @Test
    public void shouldReturnHasContent() {

        Page<Person> page = MicrostreamPage.of(Collections.singletonList(otavio),
                Pageable.ofPage(2));

        Assertions.assertTrue(page.hasContent());
        page = MicrostreamPage.of(Collections.emptyList(),
                Pageable.ofPage(2));
        Assertions.assertFalse(page.hasContent());
    }

    @Test
    public void shouldNumberOfElements() {

        Page<Person> page = MicrostreamPage.of(Collections.singletonList(otavio),
                Pageable.ofPage(2));

        Assertions.assertEquals(1, page.numberOfElements());
    }

    @Test
    public void shouldIterator() {
        Page<Person> page = MicrostreamPage.of(Collections.singletonList(otavio),
                Pageable.ofPage(2));
        Assertions.assertNotNull(page.iterator());
    }

    @Test
    public void shouldPageable() {
        Page<Person> page = MicrostreamPage.of(Collections.singletonList(otavio),
                Pageable.ofPage(2));
        Pageable pageable = page.pageable();
        Assertions.assertNotNull(pageable);
        Assertions.assertEquals(Pageable.ofPage(2), pageable);
    }

    @Test
    public void shouldNextPageable() {
        Page<Person> page = MicrostreamPage.of(Collections.singletonList(otavio),
                Pageable.ofPage(2));
        Pageable pageable = page.nextPageable();
        Assertions.assertEquals(Pageable.ofPage(3), pageable);
    }
}