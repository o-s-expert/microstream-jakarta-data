package os.expert.integration.microstream;

import java.util.Collections;
import java.util.List;

record EntityMetadata(FieldMetadata id, List<FieldMetadata> fields, Class<?> type) {

    @Override
    public List<FieldMetadata> fields() {
        return Collections.unmodifiableList(fields);
    }
}
