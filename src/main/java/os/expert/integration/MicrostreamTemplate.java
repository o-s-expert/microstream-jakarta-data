package os.expert.integration;


import jakarta.nosql.QueryMapper;
import jakarta.nosql.Template;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

class MicrostreamTemplate implements Template {


    private DataStructure data;


    @Override
    public <T> T insert(T entity) {
        Objects.requireNonNull(entity, "entity is required");
        return null;
    }

    @Override
    public <T> T insert(T entity, Duration ttl) {
        throw new UnsupportedOperationException("The insert with duration is unsupported");
    }

    @Override
    public <T> Iterable<T> insert(Iterable<T> entities) {
        Objects.requireNonNull(entities, "entities is required");
        return null;
    }

    @Override
    public <T> Iterable<T> insert(Iterable<T> entities, Duration ttl) {
        throw new UnsupportedOperationException("The insert with duration is unsupported");
    }

    @Override
    public <T> T update(T entity) {
        return null;
    }

    @Override
    public <T> Iterable<T> update(Iterable<T> entities) {
        return null;
    }

    @Override
    public <T, K> Optional<T> find(Class<T> type, K id) {
        return Optional.empty();
    }

    @Override
    public <T, K> void delete(Class<T> type, K id) {

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
