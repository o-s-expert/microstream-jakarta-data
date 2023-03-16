/*
 *  Copyright (c) 2023 Otavio Santana
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

import jakarta.nosql.QueryMapper;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

class MapperDelete extends AbstractMapperQuery implements QueryMapper.MapperDeleteFrom,
        QueryMapper.MapperDeleteWhere, QueryMapper.MapperDeleteNameCondition, QueryMapper.MapperDeleteNotCondition {


    MapperDelete(EntityMetadata mapping, MicrostreamTemplate template) {
        super(mapping, template);
    }


    @Override
    public QueryMapper.MapperDeleteNameCondition where(String name) {
        requireNonNull(name, "name is required");
        this.name = name;
        return this;
    }


    @Override
    public QueryMapper.MapperDeleteNameCondition and(String name) {
        requireNonNull(name, "name is required");
        this.name = name;
        this.and = true;
        return this;
    }

    @Override
    public QueryMapper.MapperDeleteNameCondition or(String name) {
        requireNonNull(name, "name is required");
        this.name = name;
        this.and = false;
        return this;
    }


    @Override
    public QueryMapper.MapperDeleteNotCondition not() {
        this.negate = true;
        return this;
    }

    @Override
    public <T> QueryMapper.MapperDeleteWhere eq(T value) {
        eqImpl(value);
        return this;
    }

    @Override
    public QueryMapper.MapperDeleteWhere like(String value) {
        throw new UnsupportedOperationException("There is no support for like condition");
    }

    @Override
    public <T> QueryMapper.MapperDeleteWhere gt(T value) {
        gtImpl(value);
        return this;
    }

    @Override
    public <T> QueryMapper.MapperDeleteWhere gte(T value) {
        gteImpl(value);
        return this;
    }

    @Override
    public <T> QueryMapper.MapperDeleteWhere lt(T value) {
        ltImpl(value);
        return this;
    }

    @Override
    public <T> QueryMapper.MapperDeleteWhere lte(T value) {
        lteImpl(value);
        return this;
    }

    @Override
    public <T> QueryMapper.MapperDeleteWhere between(T valueA, T valueB) {
        throw new UnsupportedOperationException("There is no support for between condition");
    }

    @Override
    public <T> QueryMapper.MapperDeleteWhere in(Iterable<T> values) {
        inImpl(values);
        return this;
    }

    @Override
    public void execute() {
        delete();
    }

    private <T> void delete() {
        Stream<T> values = this.template.entities();
        if (condition != null) {
            values = values.filter((Predicate<T>) condition);
        }
        FieldMetadata id = mapping.id();
        List<Object> ids = values.map(t -> id.get(t)).collect(Collectors.toUnmodifiableList());
        this.template.delete(ids);
    }
}
