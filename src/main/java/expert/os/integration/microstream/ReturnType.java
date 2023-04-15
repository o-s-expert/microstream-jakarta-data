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

import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.Sort;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

enum ReturnType {

    STREAM {
        @Override
        boolean isCompatible(Class<?> type) {
            return Stream.class.equals(type);
        }

        @Override
        <T> Object convert(Stream<T> stream, Pageable pageable) {
            return stream;
        }
    }, SET {
        @Override
        boolean isCompatible(Class<?> type) {
            return Set.class.equals(type);
        }

        @Override
        <T> Object convert(Stream<T> stream, Pageable pageable) {
            return stream.collect(Collectors.toUnmodifiableSet());
        }
    }, SORTED_SET {
        @Override
        boolean isCompatible(Class<?> type) {
            return NavigableSet.class.equals(type)
                    || SortedSet.class.equals(type);
        }

        @Override
        <T> Object convert(Stream<T> stream, Pageable pageable) {
            return stream.collect(Collectors.toCollection(TreeSet::new));
        }
    }, QUEUE {
        @Override
        boolean isCompatible(Class<?> type) {
            return Queue.class.equals(type) ||
                    Deque.class.equals(type);
        }

        @Override
        <T> Object convert(Stream<T> stream, Pageable pageable) {
            return stream.collect(Collectors.toCollection(ArrayDeque::new));
        }
    },
    LIST {
        @Override
        boolean isCompatible(Class<?> type) {
            return List.class.equals(type)
                    || Iterable.class.equals(type)
                    || Collection.class.equals(type);
        }

        @Override
        <T> Object convert(Stream<T> stream, Pageable pageable) {
            return stream.collect(Collectors.toUnmodifiableList());
        }
    }, PAGE {
        @Override
        boolean isCompatible(Class<?> type) {
            return Page.class.isAssignableFrom(type);
        }

        @Override
        <T> Object convert(Stream<T> stream, Pageable pageable) {
            List<T> entities = stream.collect(Collectors.toUnmodifiableList());
            return MicrostreamPage.of(entities, Objects.requireNonNullElseGet(pageable, () -> Pageable.ofSize(entities.size())));
        }
    }, OPTIONAL {
        @Override
        boolean isCompatible(Class<?> type) {
            return Optional.class.isAssignableFrom(type);
        }

        @Override
        <T> Object convert(Stream<T> stream, Pageable pageable) {
            return stream.findFirst();
        }
    }, DEFAULT {
        @Override
        boolean isCompatible(Class<?> type) {
            return false;
        }

        @Override
        <T> Object convert(Stream<T> stream, Pageable pageable) {
            return stream.findFirst().orElse(null);
        }
    };

    abstract boolean isCompatible(Class<?> type);

    abstract <T> Object convert(Stream<T> stream, Pageable pageable);

    static ReturnType of(Class<?> type) {
        return Arrays.stream(ReturnType.values())
                .filter(r -> r.isCompatible(type))
                .findFirst()
                .orElse(DEFAULT);
    }

    static Pageable pageable(Object[] params) {
        if (params == null) {
            return null;
        }
        for (Object param : params) {
            if (param instanceof Pageable pageable) {
                return pageable;
            }
        }
        return null;
    }

    static List<Sort> sort(List<Sort> sorts, Object[] params) {
        List<Sort> orderBy = new ArrayList<>(sorts);
        orderBy.addAll(sorts(params));
        return orderBy;
    }

    private static List<Sort> sorts(Object[] params) {
        List<Sort> orderBy = new ArrayList<>();
        if(params == null){
            return orderBy;
        }
        for (Object param : params) {
            if (param instanceof Sort sort) {
                orderBy.add(sort);
            } else if (param instanceof Pageable pageable) {
                orderBy.addAll(pageable.sorts());
            }
        }
        return orderBy;
    }
}
