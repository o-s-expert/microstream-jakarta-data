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

import jakarta.data.exceptions.MappingException;
import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.PageableRepository;
import jakarta.data.repository.Sort;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class MicrostreamRepository<T, K> implements PageableRepository<T, K> {

    private final MicrostreamTemplate template;

    MicrostreamRepository(MicrostreamTemplate template) {
        this.template = template;
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        Objects.requireNonNull(pageable, "pageable is required");
        EntityMetadata metadata = this.template.metadata();

        Comparator<T> comparator = comparator(pageable, metadata);
        DataStructure data = this.template.data();
        Stream<T> entities = data.values();

        if (Objects.nonNull(comparator)) {
            entities = entities.sorted(comparator);
        }

        long skip = MicrostreamPage.skip(pageable);
        if (skip > 1) {
            entities = entities.skip(skip);
        }
        if (pageable.size() >= 1) {
            entities = entities.limit(pageable.size());
        }
        List<T> collect = entities.collect(Collectors.toUnmodifiableList());
        return MicrostreamPage.of(collect, pageable);
    }

    @Override
    public <S extends T> S save(S entity) {
        Objects.requireNonNull(entity, "entity is required");
        return this.template.insert(entity);
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        Objects.requireNonNull(entities, "entities is required");
        return this.template.insert(entities);
    }

    @Override
    public Optional<T> findById(K id) {
        Objects.requireNonNull(id, "id is required");
        Class<?> type = type();
        return (Optional<T>) this.template.find(type, id);
    }

    @Override
    public boolean existsById(K id) {
        Objects.requireNonNull(id, "id is required");
        Class<?> type = type();
        return this.template.find(type, id).isPresent();
    }

    @Override
    public Stream<T> findAll() {
        return this.template.data().values();
    }

    @Override
    public Stream<T> findAllById(Iterable<K> ids) {
        Objects.requireNonNull(ids, "ids is required");
        Class<?> type = type();
        return (Stream<T>) StreamSupport.stream(ids.spliterator(), false)
                .map(k -> this.template.find(type, k))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    @Override
    public long count() {
        return this.template.data().size();
    }

    @Override
    public void deleteById(K id) {
        Objects.requireNonNull(id, "id is required");
        Class<?> type = type();
        this.template.delete(type, id);
    }

    @Override
    public void delete(T entity) {
        Objects.requireNonNull(entity, "entity is required");
        EntityMetadata metadata = this.template.metadata();
        FieldMetadata id = metadata.id();
        Object key = id.get(entity);
        Objects.requireNonNull(key, "The key is required at the entity "
                + entity + " of the type "
                + entity.getClass());
        this.template.delete(metadata.type(), key);

    }

    @Override
    public void deleteAllById(Iterable<K> ids) {
        Objects.requireNonNull(ids, "ids is required");
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        Objects.requireNonNull(entities, "entities is required");
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        DataStructure data = this.template.data();
        data.clear();
    }

    private Class<?> type() {
        return this.template.metadata().type();
    }

    private Comparator<T> comparator(Pageable pageable, EntityMetadata metadata) {
        Comparator<T> comparator = null;
        for (Sort sort : pageable.sorts()) {
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
