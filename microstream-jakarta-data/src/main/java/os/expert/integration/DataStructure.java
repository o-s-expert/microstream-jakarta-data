package os.expert.integration;

import one.microstream.collections.lazy.LazyHashSet;

import java.util.Objects;
import java.util.Set;

public class DataStructure<T> {

    private Set<T> data = new LazyHashSet<>();


    public void add(T entity) {
        Objects.requireNonNull(entity, "entity is required");
        this.data.add(entity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataStructure<?> that = (DataStructure<?>) o;
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
