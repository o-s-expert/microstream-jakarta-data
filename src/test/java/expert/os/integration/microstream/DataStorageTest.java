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

import one.microstream.persistence.types.Persister;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DataStorageTest {

    private static final String CHANGED_VALUE_TWO = "Changed value two";
    private static final String CHANGED_VALUE_ONE = "Changed value one";

    private DataStorage data;

    private Persister persister;

    @BeforeEach
    public void setUp() {
        this.persister = Mockito.mock(Persister.class);
        this.data = new DataStorage(new HashMap<>(), persister);
    }

    @Test
    public void shouldPut() {
        this.data.put("one", 1);
        this.data.put("two", 2);
        this.data.put("four", 4);

        Assertions.assertThat(this.data)
                .isNotNull()
                .matches(p -> p.size() == 3);

        ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(Object.class);

        Mockito.verify(this.persister, Mockito.times(3))
                .store(argumentCaptor.capture());
        Map<? extends Class<?>, Long> storedTypes = argumentCaptor.getAllValues().stream()
                .collect(Collectors.groupingBy(Object::getClass, Collectors.counting()));
        // We stored 3 times the entire map (initial 3 Put)
        Assertions.assertThat(storedTypes.get(HashMap.class)).isEqualTo(3L);

        // We should not use EagerStorer
        Mockito.verify(this.persister, Mockito.never())
                .createEagerStorer();
    }

    @Test
    public void shouldPut2() {

        MutableEntity one = MutableEntity.of("one", "original one");
        MutableEntity two = MutableEntity.of("two", "original two");
        MutableEntity four = MutableEntity.of("four", "original four");
        this.data.put(one.getId(), one);
        this.data.put(two.getId(), two);
        this.data.put(four.getId(), four);

        Assertions.assertThat(this.data)
                .isNotNull()
                .matches(p -> p.size() == 3);

        two.setValue(CHANGED_VALUE_TWO);
        this.data.put(two.getId(), two);
        Assertions.assertThat(this.data)
                .isNotNull()
                .matches(p -> p.size() == 3);

        Optional<MutableEntity> optional = this.data.get(two.getId());
        Assertions.assertThat(optional).isNotEmpty().get()
                .satisfies(new Condition<>(mu -> mu.getValue().equals(CHANGED_VALUE_TWO)
                        , "Updated value not stored"));

        ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(Object.class);

        Mockito.verify(this.persister, Mockito.times(4))
                .store(argumentCaptor.capture());
        Map<? extends Class<?>, Long> storedTypes = argumentCaptor.getAllValues().stream()
                .collect(Collectors.groupingBy(Object::getClass, Collectors.counting()));
        // We stored 3 times the entire map (initial 3 Put)
        Assertions.assertThat(storedTypes.get(HashMap.class)).isEqualTo(3L);
        // And once the MutableEntity
        Assertions.assertThat(storedTypes.get(MutableEntity.class)).isEqualTo(1L);


        // We should not use EagerStorer
        Mockito.verify(this.persister, Mockito.never())
                .createEagerStorer();
    }

    @Test
    public void shouldPut3() {

        MutableEntity one = MutableEntity.of("one", "original one");
        MutableEntity two = MutableEntity.of("two", "original two");
        MutableEntity four = MutableEntity.of("four", "original four");
        this.data.put(one.getId(), one);
        this.data.put(two.getId(), two);
        this.data.put(four.getId(), four);

        Assertions.assertThat(this.data)
                .isNotNull()
                .matches(p -> p.size() == 3);

        // Instead of the same instance, we create now a new instance with same Id.
        MutableEntity twoUpdated = MutableEntity.of("two", CHANGED_VALUE_TWO);
        this.data.put(twoUpdated.getId(), twoUpdated);

        Assertions.assertThat(this.data)
                .isNotNull()
                .matches(p -> p.size() == 3);  // Still 3

        Optional<MutableEntity> optional = this.data.get(two.getId());
        Assertions.assertThat(optional).isNotEmpty().get()
                .satisfies(new Condition<>(mu -> mu.getValue().equals(CHANGED_VALUE_TWO)
                        , "Updated value not stored"));

        ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(Object.class);

        Mockito.verify(this.persister, Mockito.times(4))
                .store(argumentCaptor.capture());
        Map<? extends Class<?>, Long> storedTypes = argumentCaptor.getAllValues().stream()
                .collect(Collectors.groupingBy(Object::getClass, Collectors.counting()));
        // We stored 3 times the entire map (initial 3 Put) and once for the put due to different instance and same id.
        Assertions.assertThat(storedTypes.get(HashMap.class)).isEqualTo(4L);

        // We should not use EagerStorer
        Mockito.verify(this.persister, Mockito.never())
                .createEagerStorer();
    }

    @Test
    public void shouldGet() {
        this.data.put("one", 1);
        Optional<Object> one = this.data.get("one");
        Assertions.assertThat(one)
                .isPresent()
                .get()
                .isEqualTo(1);

        Optional<Object> two = this.data.get("two");
        Assertions.assertThat(two)
                .isNotPresent();
    }

    @Test
    public void shouldRemove() {
        this.data.put("one", 1);
        Optional<Object> one = this.data.get("one");
        Assertions.assertThat(one)
                .isPresent()
                .get()
                .isEqualTo(1);

        this.data.remove("one");

        Mockito.verify(this.persister, Mockito.times(2))
                .store(ArgumentMatchers.<DataStorage>any());
        Assertions.assertThat(this.data.get("one"))
                .isNotPresent();
        // We should not use EagerStorer
        Mockito.verify(this.persister, Mockito.never())
                .createEagerStorer();
    }

    @Test
    public void shouldSize() {
        this.data.put("one", 1);
        this.data.put("two", 2);
        this.data.put("four", 4);

        Assertions.assertThat(this.data.size())
                .isEqualTo(3);

        Mockito.verify(this.persister, Mockito.times(3))
                .store(ArgumentMatchers.<DataStorage>any());
        // We should not use EagerStorer
        Mockito.verify(this.persister, Mockito.never())
                .createEagerStorer();
    }

    @Test
    public void shouldIsEmpty() {
        Assertions.assertThat(this.data.isEmpty())
                .isTrue();

        this.data.put("one", 1);
        this.data.put("two", 2);
        this.data.put("four", 4);

        Assertions.assertThat(this.data.isEmpty())
                .isFalse();

        Mockito.verify(this.persister, Mockito.times(3))
                .store(ArgumentMatchers.<DataStorage>any());
        // We should not use EagerStorer
        Mockito.verify(this.persister, Mockito.never())
                .createEagerStorer();
    }

    @Test
    public void shouldValue() {
        this.data.put("one", 1);
        this.data.put("two", 2);
        this.data.put("four", 4);

        Assertions.assertThat(this.data.values())
                .hasSize(3)
                .contains(1, 2, 4);

        Mockito.verify(this.persister, Mockito.times(3))
                .store(ArgumentMatchers.<DataStorage>any());
        // We should not use EagerStorer
        Mockito.verify(this.persister, Mockito.never())
                .createEagerStorer();

    }

    @Test
    public void shouldPutEntries() {
        List<Entry> entries = List.of(Entry.of("one", 1), Entry.of("two", 2), Entry.of("four", 4));
        this.data.put(entries);
        Assertions.assertThat(this.data.values())
                .hasSize(3)
                .contains(1, 2, 4);
        Mockito.verify(this.persister, Mockito.only())
                .store(ArgumentMatchers.<DataStorage>any());
        Mockito.verify(this.persister, Mockito.never())
                .storeAll(ArgumentMatchers.<Object>any());
        // We should not use EagerStorer
        Mockito.verify(this.persister, Mockito.never())
                .createEagerStorer();
    }

    @Test
    public void shouldPutEntries2() {
        MutableEntity one = MutableEntity.of("one", "original one");
        MutableEntity two = MutableEntity.of("two", "original two");
        MutableEntity four = MutableEntity.of("four", "original four");

        List<Entry> entries = List.of(Entry.of(one.getId(), one), Entry.of(two.getId(), two), Entry.of(four.getId(), four));
        this.data.put(entries);
        Assertions.assertThat(this.data.values())
                .hasSize(3)
                .contains(one, two, four);


        two.setValue(CHANGED_VALUE_TWO);
        // recreate one, so different instance
        one = MutableEntity.of("one", CHANGED_VALUE_ONE);

        entries = List.of(Entry.of(one.getId(), one), Entry.of(two.getId(), two));
        this.data.put(entries);
        Assertions.assertThat(this.data.values())
                .hasSize(3)  // Still 3
                .contains(one, two, four);

        Mockito.verify(this.persister, Mockito.times(2))  // We called twice put and each time called store(map)
                .store(ArgumentMatchers.<DataStorage>any());
        Mockito.verify(this.persister)  // For the updated instance
                .storeAll(ArgumentMatchers.<Iterable<MutableEntity>>any());
        // We should not use EagerStorer
        Mockito.verify(this.persister, Mockito.never())
                .createEagerStorer();
    }

    @Test
    public void shouldRemoveMultipleIds() {
        List<Entry> entries = List.of(Entry.of("one", 1), Entry.of("two", 2), Entry.of("four", 4));
        this.data.put(entries);
        this.data.remove(List.of("one", "two", "four"));

        Assertions.assertThat(this.data.values())
                .isEmpty();
        Mockito.verify(this.persister, Mockito.times(2))
                .store(ArgumentMatchers.<DataStorage>any());
        // We should not use EagerStorer
        Mockito.verify(this.persister, Mockito.never())
                .createEagerStorer();
    }

    @Test
    public void shouldReturnValueFromPredicate() {
        List<Entry> entries = List.of(Entry.of("one", 1), Entry.of("two", 2), Entry.of("four", 4));
        this.data.put(entries);
        Predicate<Object> predicate = e -> e.equals(1);
        Stream<Integer> values = this.data.values(predicate, Collections.emptyList(), 0, 0);
        Assertions.assertThat(values)
                .isNotEmpty()
                .isNotNull().hasSize(1)
                .contains(1);
    }
}