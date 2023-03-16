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


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.nosql.QueryMapper;
import jakarta.nosql.Template;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The Microstream implementation of {@link Template}
 * It uses a {@link DataStructure} as root graph at Microstream.
 * It does not implement {@link Template#select(Class)} and {@link Template#delete(Class)}
 */
@ApplicationScoped
@Typed({Template.class, MicrostreamTemplate.class})
@Default
@Microstream
class MicrostreamTemplate implements Template {

    private DataStructure data;

    private EntityMetadata metadata;

    @Inject
    MicrostreamTemplate(DataStructure data, EntityMetadata metadata) {
        this.data = data;
        this.metadata = metadata;
    }

    @Deprecated
    MicrostreamTemplate() {
    }

    @Override
    @Transaction
    public <T> T insert(T entity) {
        return save(entity);
    }


    @Override
    public <T> T insert(T entity, Duration ttl) {
        throw new UnsupportedOperationException("The insert with duration is unsupported");
    }

    @Override
    @Transaction
    public <T> Iterable<T> insert(Iterable<T> entities) {
        return save(entities);
    }

    @Override
    public <T> Iterable<T> insert(Iterable<T> entities, Duration ttl) {
        throw new UnsupportedOperationException("The insert with duration is unsupported");
    }

    @Override
    @Transaction
    public <T> T update(T entity) {
        return save(entity);
    }

    @Override
    @Transaction
    public <T> Iterable<T> update(Iterable<T> entities) {
        return save(entities);
    }

    @Override
    public <T, K> Optional<T> find(Class<T> type, K id) {
        Objects.requireNonNull(type, "type is required");
        Objects.requireNonNull(id, "id is required");
        return data.get(id);
    }

    @Override
    @Transaction
    public <T, K> void delete(Class<T> type, K id) {
        Objects.requireNonNull(type, "type is required");
        Objects.requireNonNull(id, "id is required");
        this.data.remove(id);
    }

    @Transaction
    <T, K> void delete(Iterable<K> ids) {
        Objects.requireNonNull(ids, "ids is required");
        ids.forEach(this.data::remove);
    }

    @Transaction
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
        if (metadata.type().equals(type)) {
            return new MapperSelect(metadata, this);
        }

        throw new IllegalArgumentException("The type is not the same of the class annotated with @Entity. Param class "
                + type + " @Entity class " + metadata.type());

    }

    @Override
    public <T> QueryMapper.MapperDeleteFrom delete(Class<T> type) {
        Objects.requireNonNull(type, "type is required");
        if (metadata.type().equals(type)) {
            return new MapperDelete(metadata, this);
        }

        throw new IllegalArgumentException("The type is not the same of the class annotated with @Entity. Param class "
                + type + " @Entity class " + metadata.type());
    }

    EntityMetadata metadata() {
        return metadata;
    }

    private <T> T save(T entity) {
        Objects.requireNonNull(entity, "entity is required");
        Object id = this.metadata.id().get(entity);
        this.data.put(id, entity);
        return entity;
    }

    private <T> Iterable<T> save(Iterable<T> entities) {
        Objects.requireNonNull(entities, "entities is required");
        entities.forEach(this::save);
        return entities;
    }

}
