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

import static org.junit.jupiter.api.Assertions.*;


import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.PageableRepository;
import jakarta.data.repository.Query;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.stream.Stream;

class RepositoryTypeTest {


    @Test
    public void shouldReturnDefault() throws NoSuchMethodException {
        assertEquals(RepositoryType.DEFAULT, RepositoryType.of(getMethod(CrudRepository.class, "save")));
        assertEquals(RepositoryType.DEFAULT, RepositoryType.of(getMethod(CrudRepository.class, "deleteById")));
        assertEquals(RepositoryType.DEFAULT, RepositoryType.of(getMethod(CrudRepository.class, "findById")));
        assertEquals(RepositoryType.DEFAULT, RepositoryType.of(getMethod(CrudRepository.class, "existsById")));
        assertEquals(RepositoryType.DEFAULT, RepositoryType.of(getMethod(CrudRepository.class, "count")));
        assertEquals(RepositoryType.DEFAULT, RepositoryType.of(getMethod(PageableRepository.class, "findAll")));
    }


    @Test
    public void shouldReturnObjectMethod() throws NoSuchMethodException {
        assertEquals(RepositoryType.OBJECT_METHOD, RepositoryType.of(getMethod(Object.class, "equals")));
        assertEquals(RepositoryType.OBJECT_METHOD, RepositoryType.of(getMethod(Object.class, "hashCode")));
    }


    @Test
    public void shouldReturnFindBy() throws NoSuchMethodException {
        assertEquals(RepositoryType.FIND_BY, RepositoryType.of(getMethod(DevRepository.class, "findByName")));
    }

    @Test
    public void shouldReturnDeleteBy() throws NoSuchMethodException {
        assertEquals(RepositoryType.DELETE_BY, RepositoryType.of(getMethod(DevRepository.class, "deleteByName")));
    }

    @Test
    public void shouldReturnFindAllBy() throws NoSuchMethodException {
        assertEquals(RepositoryType.FIND_ALL, RepositoryType.of(getMethod(DevRepository.class, "findAll")));
    }

    @Test
    public void shouldReturnJNoSQLQuery() throws NoSuchMethodException {
        assertEquals(RepositoryType.QUERY, RepositoryType.of(getMethod(DevRepository.class, "query")));
    }

    @Test
    public void shouldReturnUnknown() throws NoSuchMethodException {
        assertEquals(RepositoryType.UNKNOWN, RepositoryType.of(getMethod(DevRepository.class, "nope")));
    }

    @Test
    public void shouldReturnCountBy() throws NoSuchMethodException {
        assertEquals(RepositoryType.COUNT_BY, RepositoryType.of(getMethod(DevRepository.class, "countByName")));
    }

    @Test
    public void shouldReturnExistsBy() throws NoSuchMethodException {
        assertEquals(RepositoryType.EXISTS_BY, RepositoryType.of(getMethod(DevRepository.class, "existsByName")));
    }

    @Test
    public void shouldReturnOrder() throws NoSuchMethodException {
        assertEquals(RepositoryType.ORDER_BY, RepositoryType.of(getMethod(DevRepository.class,
                "order")));

        assertEquals(RepositoryType.ORDER_BY, RepositoryType.of(getMethod(DevRepository.class,
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