package os.expert.integration.microstream;

import jakarta.data.exceptions.MappingException;

import java.lang.reflect.Field;

final class FieldMetadata {

    private final Field field;
    private final String name;

    FieldMetadata(Field field, String name) {
        this.field = field;
        this.name = name;
    }

    <T> Object get(T entity) {
        try {
            return this.field.get(entity);
        } catch (IllegalAccessException e) {
            throw new MappingException("It cannot access the value from the field " + field + " at the entity "
            + entity.getClass());
        }
    }

}
