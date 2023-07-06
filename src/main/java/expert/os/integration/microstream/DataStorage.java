/*
 *  Copyright (c) 2023 Otavio & Rudy
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
import one.microstream.persistence.types.Persister;
import one.microstream.storage.types.StorageManager;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;


/**
 * The data structure used at Microstream on both {@link jakarta.nosql.Template} and any {@link jakarta.data.repository.DataRepository}
 * implementation.
 * <p>
 * It is a wrapper of {@link LazyHashMap}
 */
class DataStorage {

    private final Map<Object, Object> data;
    private final Persister persister;

    DataStorage(Map<Object, Object> data, Persister persister) {
        this.data = data;
        this.persister = persister;
    }

    DataStorage() {
        this(null, null);
    }

    /**
     * Associates the specified value with the specified key in this map.
     *
     * @param key   the key
     * @param value the entity
     * @param <K>   the key type
     * @param <V>   the entity type
     */
    public synchronized <K, V> void put(K key, V value) {
        Objects.requireNonNull(key, "key is required");
        Objects.requireNonNull(value, "value is required");
        Object oldValue = this.data.put(key, value);
        if (oldValue == value) {
            commitEntity(value);
        } else {
            commitMap();
        }
    }

    /**
     * Inserts multiples entries on the data storage
     *
     * @param entries the entries
     */
    public synchronized void put(List<Entry> entries) {
        Objects.requireNonNull(entries, "entries is required");
        Map<Object, Object> entities = entries.stream().collect(toMap(Entry::key, Entry::value,
                (a, b) -> a));
        // This is a little bit more complex when we want to avoid EagerStorer.

        List<Object> updatedInstances = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : entities.entrySet()) {
            if (entry.getValue() == this.data.put(entry.getKey(), entry.getValue())) {
                updatedInstances.add(entry.getValue());
            }
        }
        if (!updatedInstances.isEmpty()) {
            persister.storeAll(updatedInstances);
        }
        if (updatedInstances.size() != entities.size()) {
            // Commit entire Map as not all Put operations are pure (same instance)
            this.commitMap();
        }
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
    public synchronized <K, V> Optional<V> get(K key) {
        Objects.requireNonNull(key, "key is required");
        return (Optional<V>) Optional.ofNullable(this.data.get(key));
    }

    /**
     * Removes the mapping for a key from this map if it is present
     *
     * @param key the key
     * @param <K> the key type
     */
    public synchronized <K> void remove(K key) {
        Objects.requireNonNull(key, "key is required");
        this.data.remove(key);
        this.commitMap();
    }

    /**
     * Removes the mapping for a key from this map if it is present as Bulk operation.
     *
     * @param keys the keys entries
     * @param <K>  the key type
     */
    public synchronized <K> void remove(Iterable<K> keys) {
        Objects.requireNonNull(keys, "keys is required");
        keys.forEach(this.data::remove);
        this.commitMap();
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public synchronized int size() {
        return this.data.size();
    }

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map contains no key-value mappings
     */
    public synchronized boolean isEmpty() {
        return this.data.isEmpty();
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     *
     * @param <V> the entity type
     * @return a collection view of the values contained in this map
     */
    synchronized <V> Stream<V> values() {
        if (data.isEmpty()) {
            return Stream.empty();
        }
        List<V> entries = new ArrayList<>();
        entries.addAll((Collection<? extends V>) this.data.values());
        return entries.stream();
    }

    synchronized <V> Stream<V> values(Predicate<Object> predicate, List<Comparator<?>> sorts,
                                      long start, long limit) {

        if (data.isEmpty()) {
            return Stream.empty();
        }

        Stream<V> values = (Stream<V>) this.data.values().stream()
                .filter(predicate);

        if (!sorts.isEmpty()) {
            Comparator<V> comparator = sorts.stream()
                    .map(c -> (Comparator<V>) c).reduce(Comparator::thenComparing)
                    .orElseThrow();
            values = values.sorted(comparator);
        }
        if (start > 0) {
            values = values.skip(start);
        }
        if (limit > 0) {
            values = values.limit(limit);
        }

        List<V> entries = new ArrayList<>();
        entries.addAll(values.toList());
        return entries.stream();

    }

    /**
     * Remove items from the Map with the predicate as filter
     *
     * @param predicate the filter
     */
    synchronized void remove(Predicate<Object> predicate) {
        List<Object> keys = this.data.entrySet().stream()
                .filter(e -> predicate.test(e.getValue()))
                .map(Map.Entry::getKey).toList();
        this.remove(keys);
    }

    /**
     * Removes all entities from this structure .
     * The map will be empty after this call returns.
     */
    public void clear() {
        this.data.clear();
        this.commitMap();
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

    static DataStorage of(Map<Object, Object> data, StorageManager manager) {
        Objects.requireNonNull(data, "data is required");
        Objects.requireNonNull(manager, "manager is required");
        return new DataStorage(data, manager);
    }

    private void commitMap() {
        persister.store(this.data);
    }

    private void commitEntity(Object entity) {
        persister.store(entity);
    }

}
