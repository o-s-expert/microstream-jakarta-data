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


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import one.microstream.storage.types.StorageManager;

import java.util.function.Supplier;

@ApplicationScoped
class DataStructureSupplier implements Supplier<DataStorage> {

    @Inject
    private StorageManager manager;

    @Override
    @Produces
    @ApplicationScoped
    public DataStorage get() {

        Object root = manager.root();
        DataStorage data;
        if (root == null) {
            data = new DataStorage(data, persister);
            manager.setRoot(data);
            manager.storeRoot();
        } else if (root instanceof DataStorage dataStorage) {
            data = dataStorage;
        } else {
            throw new IllegalArgumentException("The current root structure is incompatible with DataStructure. " +
                    "The current structure class: " + root.getClass());
        }
        return data;
    }
}
