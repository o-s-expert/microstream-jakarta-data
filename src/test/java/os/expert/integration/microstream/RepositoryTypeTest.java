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

package os.expert.integration.microstream;

import static org.junit.jupiter.api.Assertions.*;


import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.PageableRepository;
import jakarta.data.repository.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.stream.Stream;

class RepositoryTypeTest {


    @Test
    public void shouldReturnDefault() throws NoSuchMethodException {
        Assertions.assertEquals(RepositoryType.DEFAULT, RepositoryType.of(getMethod(CrudRepository.class, "save")));
        Assertions.assertEquals(RepositoryType.DEFAULT, RepositoryType.of(getMethod(CrudRepository.class, "deleteById")));
        Assertions.assertEquals(RepositoryType.DEFAULT, RepositoryType.of(getMethod(CrudRepository.class, "findById")));
        Assertions.assertEquals(RepositoryType.DEFAULT, RepositoryType.of(getMethod(CrudRepository.class, "existsById")));
        Assertions.assertEquals(RepositoryType.DEFAULT, RepositoryType.of(getMethod(CrudRepository.class, "count")));
        Assertions.assertEquals(RepositoryType.DEFAULT, RepositoryType.of(getMethod(PageableRepository.class, "findAll")));
    }


    @Test
    public void shouldReturnObjectMethod() throws NoSuchMethodException {
        Assertions.assertEquals(RepositoryType.OBJECT_METHOD, RepositoryType.of(getMethod(Object.class, "equals")));
        Assertions.assertEquals(RepositoryType.OBJECT_METHOD, RepositoryType.of(getMethod(Object.class, "hashCode")));
    }


    @Test
    public void shouldReturnFindBy() throws NoSuchMethodException {
        Assertions.assertEquals(RepositoryType.FIND_BY, RepositoryType.of(getMethod(DevRepository.class, "findByName")));
    }

    @Test
    public void shouldReturnDeleteBy() throws NoSuchMethodException {
        Assertions.assertEquals(RepositoryType.DELETE_BY, RepositoryType.of(getMethod(DevRepository.class, "deleteByName")));
    }

    @Test
    public void shouldReturnFindAllBy() throws NoSuchMethodException {
        Assertions.assertEquals(RepositoryType.FIND_ALL, RepositoryType.of(getMethod(DevRepository.class, "findAll")));
    }

    @Test
    public void shouldReturnJNoSQLQuery() throws NoSuchMethodException {
        Assertions.assertEquals(RepositoryType.QUERY, RepositoryType.of(getMethod(DevRepository.class, "query")));
    }

    @Test
    public void shouldReturnUnknown() throws NoSuchMethodException {
        Assertions.assertEquals(RepositoryType.UNKNOWN, RepositoryType.of(getMethod(DevRepository.class, "nope")));
    }

    @Test
    public void shouldReturnCountBy() throws NoSuchMethodException {
        Assertions.assertEquals(RepositoryType.COUNT_BY, RepositoryType.of(getMethod(DevRepository.class, "countByName")));
    }

    @Test
    public void shouldReturnExistsBy() throws NoSuchMethodException {
        Assertions.assertEquals(RepositoryType.EXISTS_BY, RepositoryType.of(getMethod(DevRepository.class, "existsByName")));
    }

    @Test
    public void shouldReturnOrder() throws NoSuchMethodException {
        Assertions.assertEquals(RepositoryType.ORDER_BY, RepositoryType.of(getMethod(DevRepository.class,
                "order")));

        Assertions.assertEquals(RepositoryType.ORDER_BY, RepositoryType.of(getMethod(DevRepository.class,
                "order2")));
    }

    private Method getMethod(Class<?> repository, String methodName) throws NoSuchMethodException {
        return Stream.of(repository.getDeclaredMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst().get();

    }

    interface DevRepository extends CrudRepository {

        String findByName(String name);

        String deleteByName(String name);

        Stream<String> findAll();

        @Query("query")
        String query(String query);

        Long countByName(String name);

        Long existsByName(String name);

        void nope();

        @OrderBy("sample")
        String order();

        @OrderBy("sample")
        @OrderBy("test")
        String order2();
    }

}