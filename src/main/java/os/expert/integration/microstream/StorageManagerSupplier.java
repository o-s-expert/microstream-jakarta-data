/*
 *  Copyright (c) 2023 Otavio Santana
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


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import one.microstream.persistence.binary.one.microstream.collections.lazy.BinaryHandlerLazyHashMap;
import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.types.StorageManager;

import java.util.function.Supplier;
import java.util.logging.Logger;

@ApplicationScoped
class StorageManagerSupplier implements Supplier<StorageManager> {

    private static final Logger LOGGER = Logger.getLogger(StorageManagerSupplier.class.getName());

    private static final String DEFAULT_PATH = "target/data";

    @Override
    @Produces
    @ApplicationScoped
    public StorageManager get() {
        LOGGER.warning("Starting the default Storage Manager where it will use the path " + DEFAULT_PATH +
                " overwrite on production");
        EmbeddedStorageFoundation<?> storageFoundation = EmbeddedStorageConfiguration.Builder()
                .setStorageDirectory(DEFAULT_PATH).createEmbeddedStorageFoundation();
        storageFoundation.registerTypeHandler(new BinaryHandlerLazyHashMap());

        StorageManager manager = storageFoundation.createEmbeddedStorageManager();
        return manager.start();
    }

    public void dispose(@Disposes StorageManager manager) {
        manager.shutdown();
    }
}
