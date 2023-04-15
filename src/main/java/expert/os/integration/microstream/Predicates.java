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
import org.eclipse.jnosql.communication.query.ConditionQueryValue;
import org.eclipse.jnosql.communication.query.QueryCondition;
import org.eclipse.jnosql.communication.query.QueryValue;
import org.eclipse.jnosql.communication.query.ValueType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static expert.os.integration.microstream.CompareCondition.of;

final class Predicates {
    private Predicates() {
    }
    static <T> Predicate<T> lte(EntityMetadata metadata, Method method, Object[] params,
                                AtomicInteger paramIndex, QueryCondition condition) {

        QueryValue<?> value = condition.value();
        FieldMetadata field = metadata.field(condition.name())
                .orElseThrow(() -> new MappingException("The the entity " + metadata.type() + " " +
                        "there is no field with the name: " + condition.name()));
        Object param = param(method, params, value, paramIndex);
        return of(param.getClass()).lte(param, field);
    }

    static <T> Predicate<T> lt(EntityMetadata metadata, Method method, Object[] params,
                                       AtomicInteger paramIndex, QueryCondition condition) {

        QueryValue<?> value = condition.value();
        FieldMetadata field = metadata.field(condition.name())
                .orElseThrow(() -> new MappingException("The the entity " + metadata.type() + " " +
                        "there is no field with the name: " + condition.name()));
        Object param = param(method, params, value, paramIndex);
        return of(param.getClass()).lt(param, field);
    }

    static <T> Predicate<T> gte(EntityMetadata metadata, Method method, Object[] params,
                                        AtomicInteger paramIndex, QueryCondition condition) {

        QueryValue<?> value = condition.value();
        FieldMetadata field = metadata.field(condition.name())
                .orElseThrow(() -> new MappingException("The the entity " + metadata.type() + " " +
                        "there is no field with the name: " + condition.name()));
        Object param = param(method, params, value, paramIndex);
        return of(param.getClass()).gte(param, field);
    }

    static <T> Predicate<T> gt(EntityMetadata metadata, Method method, Object[] params,
                                       AtomicInteger paramIndex, QueryCondition condition) {

        QueryValue<?> value = condition.value();
        FieldMetadata field = metadata.field(condition.name())
                .orElseThrow(() -> new MappingException("The the entity " + metadata.type() + " " +
                        "there is no field with the name: " + condition.name()));
        Object param = param(method, params, value, paramIndex);
        return of(param.getClass()).gt(param, field);
    }

    static <T> Predicate<T> eq(EntityMetadata metadata, Method method, Object[] params,
                               AtomicInteger paramIndex, QueryCondition condition) {

        QueryValue<?> value = condition.value();
        FieldMetadata field = metadata.field(condition.name())
                .orElseThrow(() -> new MappingException("The the entity " + metadata.type() + " " +
                        "there is no field with the name: " + condition.name()));
        Object param = param(method, params, value, paramIndex);
        return t -> param.equals(field.get(t));
    }

    static <T> Predicate<T> in(EntityMetadata metadata, Method method, Object[] params,
                                       AtomicInteger paramIndex, QueryCondition condition) {

        QueryValue<?> value = condition.value();
        FieldMetadata field = metadata.field(condition.name())
                .orElseThrow(() -> new MappingException("The the entity " + metadata.type() + " " +
                        "there is no field with the name: " + condition.name()));
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

    static <T> Predicate<T> condition(QueryCondition condition, EntityMetadata metadata, Method method,
                                              Object[] params, AtomicInteger paramIndex) {
        switch (condition.condition()) {
            case EQUALS -> {
                return Predicates.eq(metadata, method, params, paramIndex, condition);
            }
            case GREATER_THAN -> {
                return Predicates.gt(metadata, method, params, paramIndex, condition);
            }
            case GREATER_EQUALS_THAN -> {
                return Predicates.gte(metadata, method, params, paramIndex, condition);
            }
            case LESSER_THAN -> {
                return Predicates.lt(metadata, method, params, paramIndex, condition);
            }
            case LESSER_EQUALS_THAN -> {
                return Predicates.lte(metadata, method, params, paramIndex, condition);
            }
            case IN -> {
                return Predicates.in(metadata, method, params, paramIndex, condition);
            }
            case AND -> {
                List<QueryCondition> andConditions = ((ConditionQueryValue) condition.value()).get();
                Predicate<T> and = (Predicate<T>) andConditions.stream().map(c -> condition(c, metadata, method, params, paramIndex))
                        .reduce(Predicate::and).orElseThrow();
                return and;
            }
            case OR -> {
                List<QueryCondition> orConditions = ((ConditionQueryValue) condition.value()).get();
                Predicate<T> or = (Predicate<T>) orConditions.stream().map(c -> condition(c, metadata, method, params, paramIndex))
                        .reduce(Predicate::or).orElseThrow();
                return or;
            }
            case NOT -> {
                List<QueryCondition> notConditions = ((ConditionQueryValue) condition.value()).get();
                QueryCondition notCondition = notConditions.get(0);
                return Predicate.not(condition(notCondition, metadata, method, params, paramIndex));
            }
            default ->
                    throw new UnsupportedOperationException("There is no support to method query using the condition: "
                            + condition.condition());
        }
    }
}
