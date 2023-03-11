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


import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * A Microstream implementation of {@link  Page}
 *
 * @param <T> the entity type
 */
class MicrostreamPage<T> implements Page<T> {

    private final List<T> entities;

    private final Pageable pageable;

    private MicrostreamPage(List<T> entities, Pageable pageable) {
        this.entities = entities;
        this.pageable = pageable;
    }

    @Override
    public long totalElements() {
        throw new UnsupportedOperationException("JNoSQL has no support for this feature yet");
    }

    @Override
    public long totalPages() {
        throw new UnsupportedOperationException("JNoSQL has no support for this feature yet");
    }

    @Override
    public List<T> content() {
        return Collections.unmodifiableList(entities);
    }

    @Override
    public boolean hasContent() {
        return !this.entities.isEmpty();
    }

    @Override
    public int numberOfElements() {
        return this.entities.size();
    }

    @Override
    public Pageable pageable() {
        return this.pageable;
    }

    @Override
    public Pageable nextPageable() {
        return this.pageable.next();
    }

    @Override
    public Iterator<T> iterator() {
        return this.entities.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MicrostreamPage<?> page = (MicrostreamPage<?>) o;
        return Objects.equals(entities, page.entities) && Objects.equals(pageable, page.pageable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entities, pageable);
    }

    @Override
    public String toString() {
        return "NoSQLPage{" +
                "entities=" + entities +
                ", pageable=" + pageable +
                '}';
    }

    /**
     * Creates a {@link  Page} implementation from entities and a pageable
     * @param entities the entities
     * @param pageable the pageable
     * @return a {@link Page} instance
     * @param <T> the entity type
     */
    static <T> Page<T> of(List<T> entities, Pageable pageable) {
        Objects.requireNonNull(entities, "entities is required");
        Objects.requireNonNull(pageable, "pageable is required");
        return new MicrostreamPage<>(entities, pageable);
    }

    /**
     * Create skip formula from pageable instance
     * @param pageable the pageable
     * @return the skip
     * @throws NullPointerException when parameter is null
     */
    static long skip(Pageable pageable) {
        Objects.requireNonNull(pageable, "pageable is required");
        return pageable.size() * (pageable.page() - 1);
    }
}
