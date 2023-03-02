package os.expert.integration;

import one.microstream.collections.lazy.LazyHashMap;

import java.util.Map;
import java.util.Objects;

public class DataStructure<K, T> {

    private final Map<K, T> data = new LazyHashMap();


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataStructure<?, ?> that = (DataStructure<?, ?>) o;
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
