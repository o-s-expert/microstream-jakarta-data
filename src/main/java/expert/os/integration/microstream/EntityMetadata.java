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

import jakarta.data.exceptions.MappingException;
import jakarta.nosql.Column;
import jakarta.nosql.Id;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * This instance is the meta-info of a loaded class that has the  {@link jakarta.nosql.Entity} annotation.
 * It represents the information of an entity on Jakarta NoSQL as metadata.
 */
record EntityMetadata(FieldMetadata id, List<FieldMetadata> fields, Class<?> type) {


    @Override
    public List<FieldMetadata> fields() {
        return Collections.unmodifiableList(fields);
    }


    Optional<FieldMetadata> field(String name) {
        if (id.name().equals(name)) {
            return Optional.ofNullable(id);
        }
        return this.fields.stream().filter(f -> f.name().equals(name))
                .findFirst();
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
