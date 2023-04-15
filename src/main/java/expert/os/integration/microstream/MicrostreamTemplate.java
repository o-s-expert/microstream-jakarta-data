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
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.nosql.QueryMapper;
import jakarta.nosql.Template;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The Microstream implementation of {@link Template}
 * It uses a {@link one.microstream.collections.lazy.LazyHashMap} as root graph at Microstream.
 *
 * <p>This interface uses a {@link java.util.Map} as the data structure root on Microstream, the {@link one.microstream.collections.lazy.LazyHashMap} 
 * provided by Microstream.</p>
 * <p>It is crucial to the Id, annotated with a field with {@link jakarta.nosql.Id},
 * implements the {@link Object#equals(Object)} and {@link Object#hashCode()} methods.</p>
 * <p>You can have several entities from different instances; however, the id is unique.</p>
 * <p>So, given the id: "any-id" it will belong to an entity, two entities with the same id will keep the last one updated.</p>
 * 
 * <p>The {@link Template#find(Class, Object)} method will use the {@link Class#isInstance(Object)}
 * is instance to return if the entity is the same instance, avoiding {@link ClassCastException}.</p>
 * The {@link Template#select(Class)} method has the same approach. 
 */
@ApplicationScoped
@Typed({Template.class, MicrostreamTemplate.class})
@Default
@Microstream
class MicrostreamTemplate implements Template {

    private DataStorage data;

    private Entities entities;

    @Inject
    MicrostreamTemplate(DataStorage data, Entities entities) {
        this.data = data;
        this.entities = entities;
    }

    @Deprecated
    MicrostreamTemplate() {
    }

    @Override
    public <T> T insert(T entity) {
        return save(entity);
    }


    @Override
    public <T> T insert(T entity, Duration ttl) {
        throw new UnsupportedOperationException("The insert with duration is unsupported");
    }

    @Override
    public <T> Iterable<T> insert(Iterable<T> entities) {
        return save(entities);
    }

    @Override
    public <T> Iterable<T> insert(Iterable<T> entities, Duration ttl) {
        throw new UnsupportedOperationException("The insert with duration is unsupported");
    }

    @Override
    public <T> T update(T entity) {
        return save(entity);
    }

    @Override
    public <T> Iterable<T> update(Iterable<T> entities) {
        return save(entities);
    }

    @Override
    public <T, K> Optional<T> find(Class<T> type, K id) {
        Objects.requireNonNull(type, "type is required");
        Objects.requireNonNull(id, "id is required");
        Optional<T> entity = data.get(id);
        return entity.filter(type::isInstance);
    }

    @Override
    public <T, K> void delete(Class<T> type, K id) {
        Objects.requireNonNull(type, "type is required");
        Objects.requireNonNull(id, "id is required");
        this.data.remove(id);
    }

    <T, K> void delete(Iterable<K> ids) {
        Objects.requireNonNull(ids, "ids is required");
        this.data.remove(ids);
    }

    void deleteAll() {
        this.data.clear();
    }

    <T> Stream<T> entities() {
        return this.data.values();
    }

    boolean isEmpty() {
        return this.data.isEmpty();
    }

    long size() {
        return this.data.size();
    }

    @Override
    public <T> QueryMapper.MapperFrom select(Class<T> type) {
        Objects.requireNonNull(type, "type is required");
        EntityMetadata metadata = metadata(type);
        return new MapperSelect(metadata, this);
    }

    @Override
    public <T> QueryMapper.MapperDeleteFrom delete(Class<T> type) {
        Objects.requireNonNull(type, "type is required");
        EntityMetadata metadata = metadata(type);
        return new MapperDelete(metadata, this);
    }


    private <T> T save(T entity) {
        Objects.requireNonNull(entity, "entity is required");
        Entry entry = entry(entity);
        this.data.put(entry.key(), entry.value());
        return entity;
    }

    private <T> Iterable<T> save(Iterable<T> entities) {
        Objects.requireNonNull(entities, "entities is required");

        List<Entry> entries = StreamSupport.stream(entities.spliterator(), false)
                .map(this::entry).toList();
        this.data.put(entries);
        return entities;
    }

    <T> EntityMetadata metadata(Class<T> type) {
        Optional<EntityMetadata> metadata = entities.findType(type);
        return metadata
                .orElseThrow(() -> new MappingException("The enity type is not found on mapping: "
                        + type + " The type most annotated with @Entity"));
    }

    private <T> Entry entry(T entity) {
        EntityMetadata metadata = metadata(entity.getClass());
        return metadata.entry(entity);
    }


}
