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


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import one.microstream.storage.types.StorageManager;

import java.util.function.Supplier;

@ApplicationScoped
class DataStructureSupplier implements Supplier<DataStructure> {

    @Inject
    private StorageManager manager;

    @Inject
    private EntityMetadata metadata;

    @Override
    @Produces
    @ApplicationScoped
    public DataStructure get() {

        Object root = manager.root();
        DataStructure data;
        if (root == null) {
            data = new DataStructure();
            manager.setRoot(data);
            manager.storeRoot();
        } else if (root instanceof DataStructure dataStructure) {
            data = dataStructure;
        } else {
            throw new IllegalArgumentException("The current root structure is incompatible with DataStructure. " +
                    "The current structure class: " + root.getClass());
        }
        return data;
    }
}
