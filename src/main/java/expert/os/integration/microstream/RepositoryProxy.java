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

import jakarta.data.exceptions.MappingException;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.PageableRepository;
import jakarta.data.repository.Sort;
import org.eclipse.jnosql.communication.query.DeleteQuery;
import org.eclipse.jnosql.communication.query.QueryCondition;
import org.eclipse.jnosql.communication.query.SelectQuery;
import org.eclipse.jnosql.communication.query.Where;
import org.eclipse.jnosql.communication.query.method.DeleteMethodProvider;
import org.eclipse.jnosql.communication.query.method.SelectMethodProvider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

class RepositoryProxy<T, K> implements InvocationHandler {

    private final PageableRepository<T, K> repository;

    private final MicrostreamTemplate template;

    private final Class<T> type;

    RepositoryProxy(PageableRepository<T, K> repository, MicrostreamTemplate template, Class<T> type) {
        this.repository = repository;
        this.template = template;
        this.type = type;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
        RepositoryType type = RepositoryType.of(method);
        switch (type) {
            case DEFAULT -> {
                return method.invoke(repository, params);
            }
            case FIND_BY -> {
                return ReturnType.of(method.getReturnType())
                        .convert(query(method, params), ReturnType.pageable(params));
            }
            case COUNT_BY -> {
                return query(method, params).count();
            }
            case EXISTS_BY -> {
                return query(method, params).findFirst().isPresent();
            }
            case DELETE_BY -> {
                delete(method, params);
                return Void.class;
            }
            case OBJECT_METHOD -> {
                return method.invoke(this, params);
            }
            default ->
                    throw new MappingException("There is not support for Microstream for feature of the type: " + type);
        }
    }

    private Predicate<T> predicate(Where where, Method method, Object[] params, EntityMetadata metadata) {
        QueryCondition condition = where.condition();
        AtomicInteger paramIndex = new AtomicInteger(0);
        Predicate<T> predicate = Predicates.condition(condition, metadata, method, params, paramIndex);
        return metadata.<T>isInstance().and(predicate);
    }


    private void delete(Method method, Object[] params) {
        EntityMetadata metadata = template.metadata(this.type);
        DeleteMethodProvider provider = DeleteMethodProvider.INSTANCE;
        DeleteQuery query = provider.apply(method, "");
        Predicate<T> predicate = query
                .where()
                .map(w -> {
                    Predicate<T> p = predicate(w, method, params, metadata);
                    return p;
                }).orElse(metadata.isInstance());

        this.template.remove((Predicate<Object>) predicate);
    }

    private Stream<T> query(Method method, Object[] params) {
        EntityMetadata metadata = template.metadata(this.type);
        SelectMethodProvider provider = SelectMethodProvider.INSTANCE;
        SelectQuery query = provider.apply(method, "");
        Predicate<T> predicate = query
                .where()
                .map(w -> {
                    Predicate<T> p = predicate(w, method, params, metadata);
                    return p;
                }).orElse(metadata.isInstance());
        Pageable pageable = ReturnType.pageable(params);
        long skip = pageable == null ? query.skip() : MicrostreamPage.skip(pageable);
        long limit = pageable == null ? query.limit() : pageable.size();

        List<Comparator<?>> comparators = comparator(ReturnType.sort(query.orderBy(), params), metadata);
        return this.template.entities(predicate, comparators, skip, limit);
    }


    private List<Comparator<?>> comparator(List<Sort> sorts, EntityMetadata metadata) {
        Comparator<T> comparator = null;
        for (Sort sort : sorts) {
            Optional<FieldMetadata> field = metadata.field(sort.property());
            Comparator<T> comparator1 = field.map(f -> sort.isAscending() ? f.comparator() : f.reversed())
                    .orElseThrow(() -> new MappingException("There is not field with the name " + sort.property() +
                            " to order"));
            if (comparator == null) {
                comparator = comparator1;
            } else {
                comparator = comparator.thenComparing(comparator1);
            }
        }
        return comparator == null ? Collections.emptyList() :
                Collections.singletonList(comparator);
    }

}
