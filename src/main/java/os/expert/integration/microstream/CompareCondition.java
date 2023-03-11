package os.expert.integration.microstream;

import jakarta.data.exceptions.MappingException;

import java.util.function.Predicate;


enum CompareCondition {

    COMPARABLE {
        @Override
        <T> Predicate<T> gt(Object param, FieldMetadata field) {
            return t -> ((Comparable) param).compareTo(checkTypes(field, t, param))  <= -1;
        }

        @Override
        <T> Predicate<T> lt(Object param, FieldMetadata field) {
            return t -> ((Comparable) param).compareTo(checkTypes(field, t, param))>= 1;
        }

        @Override
        <T> Predicate<T> gte(Object param, FieldMetadata field) {
            return t -> ((Comparable) param).compareTo(checkTypes(field, t, param))<= 0;
        }

        @Override
        <T> Predicate<T> lte(Object param, FieldMetadata field) {
            return t -> ((Comparable) param).compareTo(checkTypes(field, t, param))  >= 0;
        }
    };


    abstract <T> Predicate<T> gt(Object param, FieldMetadata field);

    abstract <T> Predicate<T> lt(Object param, FieldMetadata field);

    abstract <T> Predicate<T> gte(Object param, FieldMetadata field);

    abstract <T> Predicate<T> lte(Object param, FieldMetadata field);

    private static <T> Object checkTypes(FieldMetadata field, T entity, Object param) {
        Object value = field.get(entity);
        if (param.getClass().equals(value.getClass())) {
            return value;
        }
        throw new MappingException("The types are no compatible between the field: " + field.field() +
                " and the param: " + param);
    }

    static CompareCondition of(Class<?> type) {
        if (Comparable.class.isAssignableFrom(type)) {
            return COMPARABLE;
        }
        throw new UnsupportedOperationException("There is not support to the type: " + type + " to execute comparable" +
                " predicates, such as lesser, greater");
    }

}
