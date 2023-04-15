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

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EntitiesTest {


    @Test
    public void shouldCreateEntities() {
        Entities entities = Entities.of(Collections.singleton(Book.class));
        assertThat(entities)
                .isNotNull()
                .isInstanceOf(Entities.class);
    }

    @Test
    public void shouldGetErrorWhenParameterIsNull() {
        assertThrows(NullPointerException.class, () -> Entities.of(null));
    }

    @Test
    public void shouldReturnEntity() {
        Entities entities = Entities.of(Collections.singleton(Book.class));
        Optional<EntityMetadata> metadata = entities.findType(Book.class);
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(metadata).isNotNull().isNotEmpty();
            soft.assertThat(metadata).get()
                    .extracting(EntityMetadata::type)
                    .isEqualTo(Book.class);
        });
    }

    @Test
    public void shouldReturnEmpty() {
        Entities entities = Entities.of(Collections.singleton(Book.class));
        Optional<EntityMetadata> metadata = entities.findType(Animal.class);
        Assertions.assertThat(metadata).isNotNull().isEmpty();
    }

    @Test
    public void shouldReturnErrorWhenFindIsNull() {

    }
}