package os.expert.integration.microstream;

import jakarta.data.repository.Pageable;
import jakarta.data.repository.PageableRepository;
import jakarta.data.repository.Repository;

import java.util.List;


@Repository
public interface Library  extends PageableRepository<Book, String> {

    List<Book> findByTitle(String title);

    List<Book> findByTitleOrderByIsbn(String title);

    List<Book> findByEditionLessThan(Integer edition);

    List<Book> findByEditionLessThanEqual(Integer edition);

    List<Book> findByEditionGreaterThan(Integer edition);

    List<Book> findByEditionGreaterThanEqual(Integer edition);

    List<Book> findByEditionBetween(Integer edition, Integer editionB);

    List<Book> findByEditionIn(Iterable<Integer> editions);

    List<Book> findByEditionIn(Integer edition);

    List<Book> findByEdition(Integer edition, Pageable pageable);
}

