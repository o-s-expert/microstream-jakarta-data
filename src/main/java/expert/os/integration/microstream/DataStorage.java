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
public class DataStorage {

    private final Map<Object, Object> data = new LazyHashMap<>();


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
        if (data.isEmpty()) {
            return Stream.empty();
        }
        return (Stream<V>) this.data.values().stream();
    }

    /**
     * Removes all entities from this structure .
     * The map will be empty after this call returns.
     */
    public void clear() {
        this.data.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataStorage that = (DataStorage) o;
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
