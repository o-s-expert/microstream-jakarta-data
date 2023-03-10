package os.expert.integration.microstream;

import jakarta.data.exceptions.MappingException;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.PageableRepository;
import jakarta.data.repository.Sort;
import org.eclipse.jnosql.communication.query.ConditionQueryValue;
import org.eclipse.jnosql.communication.query.QueryCondition;
import org.eclipse.jnosql.communication.query.QueryValue;
import org.eclipse.jnosql.communication.query.SelectQuery;
import org.eclipse.jnosql.communication.query.ValueType;
import org.eclipse.jnosql.communication.query.Where;
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
                Stream<T> values = query(method, params);
                return ReturnType.of(method.getReturnType()).convert(values, pageable(params));
            case COUNT_BY:
            case EXISTS_BY:
            case FIND_ALL:
            case ORDER_BY:
            case DELETE_BY:
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
        Predicate<T> predicate = condition(condition, metadata, method, params, paramIndex);
        return predicate;
    }


    private static <T> Predicate<T> condition(QueryCondition condition, EntityMetadata metadata, Method method,
                                              Object[] params, AtomicInteger paramIndex) {


        switch (condition.condition()) {
            case EQUALS:
                return Predicates.eq(metadata, method, params, paramIndex, condition);
            case GREATER_THAN:
                return Predicates.gt(metadata, method, params, paramIndex, condition);
            case GREATER_EQUALS_THAN:
                return Predicates.gte(metadata, method, params, paramIndex, condition);
            case LESSER_THAN:
                return Predicates.lt(metadata, method, params, paramIndex, condition);
            case LESSER_EQUALS_THAN:
                return Predicates.lte(metadata, method, params, paramIndex, condition);
            case IN:
                return Predicates.in(metadata, method, params, paramIndex, condition);
            case AND:
                List<QueryCondition> andConditions = ((ConditionQueryValue) condition.value()).get();
                Predicate<T> and = (Predicate<T>) andConditions.stream().map(c -> condition(c, metadata, method, params, paramIndex))
                        .reduce(Predicate::and).orElseThrow();
                return and;
            case OR:
                List<QueryCondition> orConditions = ((ConditionQueryValue) condition.value()).get();
                Predicate<T> or = (Predicate<T>) orConditions.stream().map(c -> condition(c, metadata, method, params, paramIndex))
                        .reduce(Predicate::or).orElseThrow();
                return or;
            case NOT:
                List<QueryCondition> notConditions = ((ConditionQueryValue) condition.value()).get();
                QueryCondition notCondition = notConditions.get(0);
                return Predicate.not(condition(notCondition, metadata, method, params, paramIndex));
            case LIKE:
            case BETWEEN:
            default:
                throw new UnsupportedOperationException("There is no support to method query using the condition: "
                        + condition.condition());


        }
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
            values = values.skip(limit);
        }
        Comparator<T> comparator = comparator(query.orderBy(), metadata);
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
