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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityMetadataTest {


    @Test
    public void shouldReturnErrorWhenIsNull() {
        assertThrows(NullPointerException.class, () -> EntityMetadata.of(null));
    }

    @Test
    public void shouldCreateEntityMetadata() {
        EntityMetadata metadata = EntityMetadata.of(Person.class);

        assertThat(metadata)
                .isNotNull()
                .matches(m -> m.type().equals(Person.class))
                .matches(m -> m.id().name().equals("id"));

        assertThat(metadata.fields())
                .hasSize(2)
                .extracting(FieldMetadata::name)
                .contains("name", "birthday");

    }

    @Test
    public void shouldCreateEntityMetadata2() {
        EntityMetadata metadata = EntityMetadata.of(Animal.class);

        assertThat(metadata)
                .isNotNull()
                .matches(m -> m.type().equals(Animal.class))
                .matches(m -> m.id().name().equals("id"));

        assertThat(metadata.fields())
                .hasSize(2)
                .extracting(FieldMetadata::name)
                .contains("name", "year");
    }

    @Test
    public void shouldReturnErrorWhenThereIsNotId() {
        assertThrows(MappingException.class, ()
                -> EntityMetadata.of(Car.class));
    }

    @Test
    public void shouldGetField() {
        EntityMetadata metadata = EntityMetadata.of(Person.class);
        Optional<FieldMetadata> name = metadata.field("name");
        assertThat(name).isPresent()
                .get().extracting(FieldMetadata::name)
                .isEqualTo("name");
    }

    @Test
    public void shouldGetId() {
        EntityMetadata metadata = EntityMetadata.of(Book.class);
        Optional<FieldMetadata> name = metadata.field("isbn");
        assertThat(name).isPresent()
                .get().extracting(FieldMetadata::name)
                .isEqualTo("isbn");
    }

    @Test
    public void shouldReturnEmptyFieldIsNotFound() {
        EntityMetadata metadata = EntityMetadata.of(Book.class);
        Optional<FieldMetadata> name = metadata.field("not-found");
        assertThat(name).isNotPresent();
    }

    private static class Car {
        @Column
        private String plate;

        @Column
        private Year year;
    }
}