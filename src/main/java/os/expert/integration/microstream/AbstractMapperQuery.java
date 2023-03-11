package os.expert.integration.microstream;

import jakarta.data.exceptions.MappingException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static os.expert.integration.microstream.CompareCondition.of;

abstract class AbstractMapperQuery {

    protected boolean negate;

    protected Predicate<?> condition;

    protected boolean and;

    protected String name;

    protected transient final EntityMetadata mapping;

    protected transient final MicrostreamTemplate template;

    protected long start;

    protected long limit;


    AbstractMapperQuery(EntityMetadata mapping, MicrostreamTemplate template) {
        this.mapping = mapping;
        this.template = template;
    }

    protected void appendCondition(Predicate<?> newCondition) {
        Predicate predicate = getCondition(newCondition);
        if (nonNull(condition)) {
            if (and) {
                this.condition = condition.and(predicate);
            } else {
                this.condition = condition.or(predicate);
            }
        } else {
            this.condition = predicate;
        }
        this.negate = false;
        this.name = null;
    }



    protected <T> void inImpl(Iterable<T> values) {
        requireNonNull(values, "values is required");

        List<T> items = new ArrayList<>();
        values.forEach(items::add);
        FieldMetadata field = field();
        appendCondition(t -> items.contains(field.get(t)));
    }

    protected <T> void eqImpl(T value) {
        requireNonNull(value, "value is required");
        FieldMetadata field = field();
        appendCondition(e ->  value.equals(field.get(e)));
    }


    protected <T> void gteImpl(T value) {
        requireNonNull(value, "value is required");
        FieldMetadata field = field();
        appendCondition(of(value.getClass()).gte(value, field));
    }

    protected <T> void gtImpl(T value) {
        requireNonNull(value, "value is required");
        FieldMetadata field = field();
        appendCondition(of(value.getClass()).gt(value, field));
    }

    protected <T> void ltImpl(T value) {
        requireNonNull(value, "value is required");
        FieldMetadata field = field();
        appendCondition(of(value.getClass()).lt(value, field));
    }

    protected <T> void lteImpl(T value) {
        requireNonNull(value, "value is required");
        FieldMetadata field = field();
        appendCondition(of(value.getClass()).lte(value, field));
    }


    protected FieldMetadata field() {
        FieldMetadata field = mapping.field(name)
                .orElseThrow(() -> new MappingException("The field " +name+  " does not exist at the entity "
                        + mapping.type()));
        return field;
    }

    private Predicate<?> getCondition(Predicate<?> newCondition) {
        if (negate) {
            return newCondition.negate();
        } else {
            return newCondition;
        }
    }
}
