package os.expert.integration.microstream;

import one.microstream.collections.lazy.LazyHashMap;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class DataStructure {

    private final Map<Object, Object> data = new LazyHashMap();


    public <K, V> void put(K key, V value) {
        Objects.requireNonNull(key, "key is required");
        Objects.requireNonNull(value, "value is required");
        this.data.put(key, value);
    }

    public <K, V> Optional<V> get(K key) {
        Objects.requireNonNull(key, "key is required");
        return (Optional<V>) Optional.ofNullable(this.data.get(key));
    }

    public <K> void remove(K key){
        Objects.requireNonNull(key, "key is required");
        this.data.remove(key);
    }

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
