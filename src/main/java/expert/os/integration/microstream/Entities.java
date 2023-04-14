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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * Represents a collection of {@link EntityMetadata}
 */
record Entities(Map<Class<?>, EntityMetadata> entities) {

    Optional<EntityMetadata> findType(Class<?> type) {
        return Optional.ofNullable(this.entities.get(type));
    }

    static Entities of(Set<Class<?>> entities) {
        Objects.requireNonNull(entities, "entities is required");
        return new Entities(entities.stream()
                .collect(toMap(Function.identity(), EntityMetadata::of)));
    }
}
