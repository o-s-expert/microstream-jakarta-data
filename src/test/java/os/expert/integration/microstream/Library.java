package os.expert.integration.microstream;

import jakarta.data.repository.PageableRepository;
import jakarta.data.repository.Repository;


@Repository
public interface Library  extends PageableRepository<Book, String> {
}
