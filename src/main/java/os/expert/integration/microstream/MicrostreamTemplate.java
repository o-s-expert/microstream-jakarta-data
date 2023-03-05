package os.expert.integration.microstream;


import jakarta.nosql.QueryMapper;
import jakarta.nosql.Template;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

class MicrostreamTemplate implements Template {


    private DataStructure data;

    private EntityMetadata metadata;

    @Override
    public <T> T insert(T entity) {
        Objects.requireNonNull(entity, "entity is required");
        Object id = this.metadata.id().get(entity);
        this.data.put(id, entity);
        return entity;
    }

    @Override
    public <T> T insert(T entity, Duration ttl) {
        throw new UnsupportedOperationException("The insert with duration is unsupported");
    }

    @Override
    public <T> Iterable<T> insert(Iterable<T> entities) {
        Objects.requireNonNull(entities, "entities is required");
        entities.forEach(this::insert);
        return entities;
    }

    @Override
    public <T> Iterable<T> insert(Iterable<T> entities, Duration ttl) {
        throw new UnsupportedOperationException("The insert with duration is unsupported");
    }

    @Override
    public <T> T update(T entity) {
        return insert(entity);
    }

    @Override
    public <T> Iterable<T> update(Iterable<T> entities) {
        return insert(entities);
    }

    @Override
    public <T, K> Optional<T> find(Class<T> type, K id) {
        Objects.requireNonNull(type, "type is required");
        Objects.requireNonNull(id, "id is required");
        return data.get(id);
    }

    @Override
    public <T, K> void delete(Class<T> type, K id) {
        Objects.requireNonNull(type, "type is required");
        Objects.requireNonNull(id, "id is required");
        this.data.remove(id);
    }

    @Override
    public <T> QueryMapper.MapperFrom select(Class<T> type) {
        throw new UnsupportedOperationException("The select is unsupported");
    }

    @Override
    public <T> QueryMapper.MapperDeleteFrom delete(Class<T> type) {
        throw new UnsupportedOperationException("The delete is unsupported");
    }
}
