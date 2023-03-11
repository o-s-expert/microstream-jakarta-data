package os.expert.integration.microstream;

import jakarta.data.exceptions.MappingException;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.PageableRepository;
import jakarta.data.repository.Sort;
import org.eclipse.jnosql.communication.query.ConditionQueryValue;
import org.eclipse.jnosql.communication.query.DeleteQuery;
import org.eclipse.jnosql.communication.query.QueryCondition;
import org.eclipse.jnosql.communication.query.QueryValue;
import org.eclipse.jnosql.communication.query.SelectQuery;
import org.eclipse.jnosql.communication.query.ValueType;
import org.eclipse.jnosql.communication.query.Where;
import org.eclipse.jnosql.communication.query.method.DeleteMethodProvider;
import org.eclipse.jnosql.communication.query.method.SelectMethodProvider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static os.expert.integration.microstream.CompareCondition.of;
import static os.expert.integration.microstream.ReturnType.pageable;

class RepositoryProxy<T, K> implements InvocationHandler {

    private final PageableRepository<T, K> repository;

    private final MicrostreamTemplate template;

    RepositoryProxy(PageableRepository<T, K> repository, MicrostreamTemplate template) {
        this.repository = repository;
        this.template = template;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
        RepositoryType type = RepositoryType.of(method);
        switch (type) {
            case DEFAULT:
                return method.invoke(repository, params);
            case FIND_BY:
                return ReturnType.of(method.getReturnType())
                        .convert(query(method, params), pageable(params));
            case COUNT_BY:
                return query(method, params).count();
            case EXISTS_BY:
                return query(method, params).findFirst().isPresent();
            case DELETE_BY:
                delete(method, params);
                return Void.class;
            case FIND_ALL:
            case ORDER_BY:
            case QUERY:
            default:
                throw new MappingException("There is not support for Microstream for feature of the type: " + type);
            case OBJECT_METHOD:
                return method.invoke(this, params);
        }
    }

    private <T> Predicate<T> predicate(Where where, Method method, Object[] params, EntityMetadata metadata) {
        QueryCondition condition = where.condition();

        AtomicInteger paramIndex = new AtomicInteger(0);
        Predicate<T> predicate = Predicates.condition(condition, metadata, method, params, paramIndex);
        return predicate;
    }


    private void delete(Method method, Object[] params) {
        EntityMetadata metadata = template.metadata();
        DeleteMethodProvider provider = DeleteMethodProvider.INSTANCE;
        DeleteQuery query = provider.apply(method, "");
        Predicate<T> predicate = query
                .where()
                .map(w -> {
                    Predicate<T> p = predicate(w, method, params, metadata);
                    return p;
                }).orElse(null);

        Stream<T> values = repository.findAll();
        if (predicate != null) {
            values = values.filter(predicate);
        }
        this.repository.deleteAll(values.collect(Collectors.toUnmodifiableList()));
    }

    private Stream<T> query(Method method, Object[] params) {
        EntityMetadata metadata = template.metadata();
        SelectMethodProvider provider = SelectMethodProvider.INSTANCE;
        SelectQuery query = provider.apply(method, "");
        Predicate<T> predicate = query
                .where()
                .map(w -> {
                    Predicate<T> p = predicate(w, method, params, metadata);
                    return p;
                }).orElse(null);

        Stream<T> values = repository.findAll();
        if (predicate != null) {
            values = values.filter(predicate);
        }
        Pageable pageable = pageable(params);
        long skip = pageable == null ? query.skip() : MicrostreamPage.skip(pageable);
        long limit = pageable == null ? query.limit() : pageable.size();

        if (skip > 0) {
            values = values.skip(skip);
        }
        if (limit > 0) {
            values = values.limit(limit);
        }

        Comparator<T> comparator = comparator(ReturnType.sort(query.orderBy(), params), metadata);
        if (comparator != null) {
            values = values.sorted(comparator);
        }
        return values;
    }

    private <T> Comparator<T> comparator(List<Sort> sorts, EntityMetadata metadata) {
        Comparator<T> comparator = null;
        for (Sort sort : sorts) {
            Optional<FieldMetadata> field = metadata.field(sort.property());
            Comparator comparator1 = field.map(f -> sort.isAscending() ? f.comparator() : f.reversed())
                    .orElseThrow(() -> new MappingException("There is not field with the name " + sort.property() +
                            " to order"));
            if (comparator == null) {
                comparator = comparator1;
            } else {
                comparator = comparator.thenComparing(comparator1);
            }
        }
        return comparator;
    }


}
