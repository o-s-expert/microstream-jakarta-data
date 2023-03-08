package os.expert.integration.microstream;

import one.microstream.collections.lazy.LazyHashMap;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * The data structure used at Microstream on both {@link jakarta.nosql.Template} and any {@link jakarta.data.repository.DataRepository}
 * implementation.
 * <p>
 * It is a wrapper of {@link LazyHashMap}
 */
public class DataStructure {

    private final Map<Object, Object> data = new LazyHashMap();


    /**
     * Associates the specified value with the specified key in this map.
     *
     * @param key   the key
     * @param value the entity
     * @param <K>   the key type
     * @param <V>   the entity type
     */
    public <K, V> void put(K key, V value) {
        Objects.requireNonNull(key, "key is required");
        Objects.requireNonNull(value, "value is required");
        this.data.put(key, value);
    }

    /**
     * * Returns the value to which the specified key is mapped,
     * or {@code Optional#empty()} if this map contains no mapping for the key.
     *
     * @param key the key or ID
     * @param <K> the key type
     * @param <V> the entity type
     * @return the entity of {@link Optional#empty()}
     */
    public <K, V> Optional<V> get(K key) {
        Objects.requireNonNull(key, "key is required");
        return (Optional<V>) Optional.ofNullable(this.data.get(key));
    }

    /**
     * Removes the mapping for a key from this map if it is present
     *
     * @param key the key
     * @param <K> the key type
     */
    public <K> void remove(K key) {
        Objects.requireNonNull(key, "key is required");
        this.data.remove(key);
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public int size() {
        return this.data.size();
    }

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map contains no key-value mappings
     */
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     *
     * @param <V> the entity type
     * @return a collection view of the values contained in this map
     */
    public <V> Stream<V> values() {
        return (Stream<V>) this.data.values().stream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataStructure that = (DataStructure) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data);
    }

    @Override
    public String toString() {
        return "DataStructure{" +
                "data=" + data +
                '}';
    }
}
