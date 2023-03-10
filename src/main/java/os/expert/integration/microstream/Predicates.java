package os.expert.integration.microstream;

import jakarta.data.exceptions.MappingException;
import org.eclipse.jnosql.communication.query.QueryValue;
import org.eclipse.jnosql.communication.query.ValueType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static os.expert.integration.microstream.CompareCondition.of;

final class Predicates {
    private Predicates() {
    }
    static <T> Predicate<T> lte(FieldMetadata field, Method method, Object[] params, QueryValue<?> value,
                                AtomicInteger paramIndex) {
        Object param = param(method, params, value, paramIndex);
        return of(param.getClass()).lesserEquals(param, field);
    }

    static <T> Predicate<T> lt(FieldMetadata field, Method method, Object[] params, QueryValue<?> value,
                                       AtomicInteger paramIndex) {
        Object param = param(method, params, value, paramIndex);
        return of(param.getClass()).lesser(param, field);
    }

    static <T> Predicate<T> gte(FieldMetadata field, Method method, Object[] params, QueryValue<?> value,
                                        AtomicInteger paramIndex) {
        Object param = param(method, params, value, paramIndex);
        return of(param.getClass()).greaterEquals(param, field);
    }

    static <T> Predicate<T> gt(FieldMetadata field, Method method, Object[] params, QueryValue<?> value,
                                       AtomicInteger paramIndex) {
        Object param = param(method, params, value, paramIndex);
        return of(param.getClass()).greater(param, field);
    }

    static <T> Predicate<T> eq(FieldMetadata field, Method method, Object[] params, QueryValue<?> value,
                                       AtomicInteger paramIndex) {
        Object param = param(method, params, value, paramIndex);
        return t -> param.equals(field.get(t));
    }

    static <T> Predicate<T> in(FieldMetadata field, Method method, Object[] params, QueryValue<?> value,
                                       AtomicInteger paramIndex) {
        Object param = param(method, params, value, paramIndex);
        if (param instanceof Iterable<?> iterable) {
            List<Object> items = new ArrayList<>();
            iterable.forEach(items::add);
            return t -> items.contains(field.get(t));
        }
        throw new MappingException("The IN condition at method query works with Iterable implementations");
    }

    static Object param(Method method, Object[] params, QueryValue<?> value, AtomicInteger paramIndex) {

        if (value.type().equals(ValueType.PARAMETER)) {
            if (paramIndex.get() > params.length - 1) {
                throw new MappingException("There is arguments missing at the method repository: "
                        + method);
            }
            return requireNonNull(params[paramIndex.getAndIncrement()], "parameter cannot be null at repository");
        } else {
            return value.get();
        }
    }
}
