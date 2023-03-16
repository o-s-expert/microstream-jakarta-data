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

    void  deleteByActiveTrue();
}

