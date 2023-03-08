package os.expert.integration.microstream;

import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.PageableRepository;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class MicrostreamRepository<T, K> implements PageableRepository<T, K> {

    private final MicrostreamTemplate template;

    MicrostreamRepository(MicrostreamTemplate template) {
        this.template = template;
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return null;
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
                .filter(Optional::isPresent);
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

}
