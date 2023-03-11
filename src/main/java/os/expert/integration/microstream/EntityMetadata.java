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

package os.expert.integration.microstream;

import jakarta.data.exceptions.MappingException;
import jakarta.nosql.Column;
import jakarta.nosql.Id;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

final class EntityMetadata {

    private final FieldMetadata id;
    private final List<FieldMetadata> fields;
    private final Class<?> type;

    private EntityMetadata(FieldMetadata id, List<FieldMetadata> fields, Class<?> type) {
        this.id = id;
        this.fields = fields;
        this.type = type;
    }

    List<FieldMetadata> fields() {
        return Collections.unmodifiableList(fields);
    }

    FieldMetadata id() {
        return id;
    }

    Class<?> type() {
        return type;
    }

    Optional<FieldMetadata> field(String name) {
        if (id.name().equals(name)) {
            return Optional.ofNullable(id);
        }
        return this.fields.stream().filter(f -> f.name().equals(name))
                .findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityMetadata that = (EntityMetadata) o;
        return Objects.equals(id, that.id)
                && Objects.equals(fields, that.fields)
                && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fields, type);
    }

    @Override
    public String toString() {
        return "EntityMetadata{" +
                "id=" + id +
                ", fields=" + fields +
                ", type=" + type +
                '}';
    }

    static EntityMetadata of(Class<?> type) {
        Objects.requireNonNull(type, "type is required");
        List<FieldMetadata> fields = new ArrayList<>();
        FieldMetadata id = null;
        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getAnnotation(Id.class) != null) {
                id = FieldMetadata.of(field);
            } else if (field.getAnnotation(Column.class) != null) {
                fields.add(FieldMetadata.of(field));
            }
        }
        if (id == null) {
            throw new MappingException("The entity " + type + " requires at least a field with " +
                    "@jakarta.nosql.Id annotation");
        }
        return new EntityMetadata(id, fields, type);
    }


}
