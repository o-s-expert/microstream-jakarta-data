package os.expert.integration.microstream;

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
