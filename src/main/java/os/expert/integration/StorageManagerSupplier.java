package os.expert.integration;


import jakarta.enterprise.context.ApplicationScoped;
import one.microstream.persistence.binary.one.microstream.collections.lazy.BinaryHandlerLazyHashMap;
import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.types.StorageManager;

import java.util.function.Supplier;

@ApplicationScoped
public class StorageManagerSupplier implements Supplier<StorageManager> {

    @Override
    public StorageManager get() {
        EmbeddedStorageFoundation<?> storageFoundation = EmbeddedStorageConfiguration.Builder()
                .setStorageDirectory("target/data").createEmbeddedStorageFoundation();
        storageFoundation.registerTypeHandler(new BinaryHandlerLazyHashMap());

        return storageFoundation.createEmbeddedStorageManager();
    }
}
