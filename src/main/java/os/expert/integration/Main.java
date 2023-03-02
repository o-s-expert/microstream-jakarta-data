package os.expert.integration;


import one.microstream.persistence.binary.one.microstream.collections.lazy.BinaryHandlerLazyArrayList;
import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.types.StorageManager;

import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        EmbeddedStorageFoundation<?> storageFoundation = EmbeddedStorageConfiguration.Builder()
                .setStorageDirectory("target/data").createEmbeddedStorageFoundation();
        storageFoundation.registerTypeHandler(new BinaryHandlerLazyArrayList());

        StorageManager manager = storageFoundation.createEmbeddedStorageManager();
        manager.start();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Object root = manager.root();
        DataStructure<Long> values;
        if (root == null) {
            values = new DataStructure<>();
            values.add(random.nextLong(0, 10));
            values.add(random.nextLong(0, 10));
            values.add(random.nextLong(0, 10));
            storageFoundation.setRoot(values);
        } else {
            values = (DataStructure<Long>) manager.root();
        }
        values.add(random.nextLong(0, 10));
        values.add(random.nextLong(0, 10));
        values.add(random.nextLong(0, 10));
        manager.storeRoot();
        System.out.println("the root: " + root);
        manager.shutdown();

    }
}