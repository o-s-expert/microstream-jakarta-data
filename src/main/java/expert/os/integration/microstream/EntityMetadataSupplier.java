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
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.util.function.Supplier;

@ApplicationScoped
class EntityMetadataSupplier implements Supplier<EntityMetadata> {


    @Override
    @Produces
    public EntityMetadata get() {
        Class<?> entity = ClassScanner.INSTANCE.entity()
                .orElseThrow(() -> new MappingException("For the integration you need one entity with @Entity annotation"));
        return EntityMetadata.of(entity);
    }
}
