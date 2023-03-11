package os.expert.integration.microstream;

import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.PageableRepository;
import jakarta.data.repository.Repository;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;


@Repository
public interface Library  extends PageableRepository<Book, String> {

    List<Book> findByTitle(String title);

    Set<Book> findByTitleOrderByIsbn(String title);

    Queue<Book> findByEditionLessThan(Integer edition);

    Stream<Book> findByEditionLessThanEqual(Integer edition);

    List<Book> findByEditionGreaterThan(Integer edition);

    List<Book> findByEditionGreaterThanEqual(Integer edition);

    List<Book> findByEditionBetween(Integer edition, Integer editionB);

    List<Book> findByEditionIn(Iterable<Integer> editions);

    List<Book> findByEditionIn(Integer edition);

    List<Book> findByTitleAndEdition(String title, Integer edition);

    List<Book> findByTitleOrEdition(String title, Integer edition);

    Page<Book> findByTitle(String title, Pageable pageable);

    List<Book> findByEditionNot(Integer edition);

    List<Book> findByActiveTrue();

    List<Book> findByActiveFalse();

    Long countByActiveTrue();

    boolean existsByActiveTrue();
}

