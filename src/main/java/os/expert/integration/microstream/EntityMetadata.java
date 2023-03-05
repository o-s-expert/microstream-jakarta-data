package os.expert.integration.microstream;

import java.util.Collections;
import java.util.List;

final class EntityMetadata {

    private final FieldMetadata id;
    private final List<FieldMetadata> fields;
    private final Class<?> type;

    EntityMetadata(FieldMetadata id, List<FieldMetadata> fields, Class<?> type) {
        this.id = id;
        this.fields = fields;
        this.type = type;
    }

    List<FieldMetadata> fields() {
        return Collections.unmodifiableList(fields);
    }
}
