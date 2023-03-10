package os.expert.integration.microstream;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import one.microstream.persistence.binary.one.microstream.collections.lazy.BinaryHandlerLazyHashMap;
import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import one.microstream.storage.types.StorageManager;

import java.util.function.Supplier;
import java.util.logging.Logger;

@ApplicationScoped
class StorageManagerSupplier implements Supplier<StorageManager> {

    private static final Logger LOGGER = Logger.getLogger(StorageManagerSupplier.class.getName());

    private static final String DEFAULT_PATH = "target/data";

    @Override
    @Produces
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
