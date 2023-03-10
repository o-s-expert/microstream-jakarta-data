package os.expert.integration.microstream;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.nosql.Template;
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
        } else if (root instanceof DataStructure dataStructure) {
            data = dataStructure;
        } else {
            throw new IllegalArgumentException("The current root structure is incompatible with DataStructure. " +
                    "The current structure class: " + root.getClass());
        }
        return data;
    }
}
