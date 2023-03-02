package os.expert.integration;


import jakarta.nosql.QueryMapper;
import jakarta.nosql.Template;

import java.time.Duration;
import java.util.Optional;

class MicrostreamTemplate implements Template {


    @Override
    public <T> T insert(T entity) {
        return null;
    }

    @Override
    public <T> T insert(T entity, Duration ttl) {
        return null;
    }

    @Override
    public <T> Iterable<T> insert(Iterable<T> entities) {
        return null;
    }

    @Override
    public <T> Iterable<T> insert(Iterable<T> entities, Duration ttl) {
        return null;
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
        return null;
    }

    @Override
    public <T> QueryMapper.MapperDeleteFrom delete(Class<T> type) {
        return null;
    }
}
