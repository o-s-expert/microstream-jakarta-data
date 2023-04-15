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

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.PageableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class RepositoryProxySupplierTest {


    private MicrostreamTemplate template;

    private final RepositoryProxySupplier supplier = RepositoryProxySupplier.INSTANCE;

    @BeforeEach
    public void setUp() {
        DataStorage data = new DataStorage();
        Entities entities = Entities.of(Collections.singleton(Book.class));
        this.template = new MicrostreamTemplate(data, entities);

    }


    @Test
    public void shouldCreateCrudRepository() {
        Library library = this.supplier.get(Library.class, template);
        assertThat(library).isNotNull();
    }

    @Test
    public void shouldCreatePageableRepository() {
        BookRepository repository = this.supplier.get(BookRepository.class, template);
        assertThat(repository).isNotNull();
    }

    interface Library extends CrudRepository<Book, String> {

    }

    interface BookRepository extends PageableRepository<Book, String> {

    }

}