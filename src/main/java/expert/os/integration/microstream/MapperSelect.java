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

import jakarta.data.exceptions.NonUniqueResultException;
import jakarta.nosql.QueryMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

class MapperSelect extends AbstractMapperQuery implements QueryMapper.MapperFrom, QueryMapper.MapperLimit,
        QueryMapper.MapperSkip, QueryMapper.MapperOrder, QueryMapper.MapperNameCondition,
        QueryMapper.MapperNotCondition, QueryMapper.MapperNameOrder, QueryMapper.MapperWhere {

    private final List<Comparator<?>> sorts = new ArrayList<>();

    MapperSelect(EntityMetadata mapping, MicrostreamTemplate template) {
        super(mapping, template);
    }

    @Override
    public QueryMapper.MapperNameCondition and(String name) {
        requireNonNull(name, "name is required");
        this.name = name;
        this.and = true;
        return this;
    }

    @Override
    public QueryMapper.MapperNameCondition or(String name) {
        requireNonNull(name, "name is required");
        this.name = name;
        this.and = false;
        return this;
    }

    @Override
    public QueryMapper.MapperNameCondition where(String name) {
        requireNonNull(name, "name is required");
        this.name = name;
        return this;
    }

    @Override
    public QueryMapper.MapperSkip skip(long start) {
        this.start = start;
        return this;
    }

    @Override
    public QueryMapper.MapperLimit limit(long limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public QueryMapper.MapperOrder orderBy(String name) {
        requireNonNull(name, "name is required");
        this.name = name;
        return this;
    }

    @Override
    public QueryMapper.MapperNotCondition not() {
        this.negate = true;
        return this;
    }

    @Override
    public <T> QueryMapper.MapperWhere eq(T value) {
        eqImpl(value);
        return this;
    }


    @Override
    public QueryMapper.MapperWhere like(String value) {
        throw new UnsupportedOperationException("There is no support for like condition");
    }

    @Override
    public <T> QueryMapper.MapperWhere gt(T value) {
        gtImpl(value);
        return this;
    }

    @Override
    public <T> QueryMapper.MapperWhere gte(T value) {
        gteImpl(value);
        return this;
    }

    @Override
    public <T> QueryMapper.MapperWhere lt(T value) {
        ltImpl(value);
        return this;
    }


    @Override
    public <T> QueryMapper.MapperWhere lte(T value) {
        lteImpl(value);
        return this;
    }

    @Override
    public <T> QueryMapper.MapperWhere between(T valueA, T valueB) {
        throw new UnsupportedOperationException("There is no support for between condition");
    }

    @Override
    public <T> QueryMapper.MapperWhere in(Iterable<T> values) {
        inImpl(values);
        return this;
    }

    @Override
    public QueryMapper.MapperNameOrder asc() {
        this.sorts.add(field().comparator());
        return this;
    }

    @Override
    public QueryMapper.MapperNameOrder desc() {
        this.sorts.add(field().reversed());
        return this;
    }

    @Override
    public <T> Stream<T> stream() {
        return this.template.entities(filter(), sorts, start, limit);
    }

    @Override
    public <T> List<T> result() {
        return (List<T>) stream().toList();
    }

    @Override
    public <T> Optional<T> singleResult() {
        List<T> entities = result();
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        if (entities.size() > 1) {
            throw new NonUniqueResultException("The single result can return zero or one, but it is returning " + entities.size());
        }
        return Optional.of(entities.get(0));
    }

    private <T> Predicate<?> filter() {
        Predicate<T> isInstance = this.mapping.isInstance();
        if (condition != null) {
            return isInstance.and((Predicate<? super T>) condition);
        } else {
            return isInstance;
        }
    }
}
