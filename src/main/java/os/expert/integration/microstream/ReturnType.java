package os.expert.integration.microstream;

import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.Sort;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

enum ReturnType {

    STREAM {
        @Override
        boolean isCompatible(Class<?> type) {
            return Stream.class.isAssignableFrom(type);
        }

        @Override
        <T> Object convert(Stream<T> stream, Pageable pageable) {
            return stream;
        }
    }, SET {
        @Override
        boolean isCompatible(Class<?> type) {
            return Set.class.isAssignableFrom(type);
        }

        @Override
        <T> Object convert(Stream<T> stream, Pageable pageable) {
            return stream.collect(Collectors.toUnmodifiableSet());
        }
    }, QUEUE {
        @Override
        boolean isCompatible(Class<?> type) {
            return Queue.class.isAssignableFrom(type) ||
                    Deque.class.isAssignableFrom(type);
        }

        @Override
        <T> Object convert(Stream<T> stream, Pageable pageable) {
            return stream.collect(Collectors.toCollection(ArrayDeque::new));
        }
    },
    LIST {
        @Override
        boolean isCompatible(Class<?> type) {
            return Iterable.class.isAssignableFrom(type);
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
            return MicrostreamPage.of(entities, pageable);
        }
    };

    abstract boolean isCompatible(Class<?> type);

    abstract <T> Object convert(Stream<T> stream, Pageable pageable);

    static ReturnType of(Class<?> type) {
        return Arrays.stream(ReturnType.values())
                .filter(r -> r.isCompatible(type))
                .findFirst()
                .orElse(STREAM);
    }

    static Pageable pageable(Object[] params) {
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
