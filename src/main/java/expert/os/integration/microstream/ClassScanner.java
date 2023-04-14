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


import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import jakarta.data.exceptions.MappingException;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Repository;
import jakarta.nosql.Entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Scanner classes that will load entities with both Entity and Embeddable
 * annotations and repositories: interfaces that extend DataRepository
 * and has the Repository annotation.
 */
public enum ClassScanner {

    INSTANCE;

    private final Set<Class<?>> entities;
    private final Set<Class<?>> repositories;


    ClassScanner() {
        this.entities = new HashSet<>();
        this.repositories = new HashSet<>();

        Logger logger = Logger.getLogger(ClassScanner.class.getName());
        logger.fine("Starting scan class to find entities, embeddable and repositories.");
        try (ScanResult result = new ClassGraph().enableAllInfo().scan()) {
            entities.addAll(result.getClassesWithAnnotation(Entity.class).loadClasses());
            repositories.addAll(result.getClassesWithAnnotation(Repository.class)
                    .getInterfaces().loadClasses(DataRepository.class));
        }
        logger.fine(String.format("Finished the class scan with entities %d, and repositories: %d"
                , entities.size(), repositories.size()));

    }


    /**
     * Returns the classes that that has the {@link Entity} annotation
     *
     * @return the classes with {@link Entity}
     */
    public Set<Class<?>> entities() {
        return Collections.unmodifiableSet(this.entities);
    }

    /**
     * Returns repository: interface that extend DataRepository and has the Repository annotation.
     *
     * @return the repository or the {@link Optional#empty()}
     */
    public Set<Class<?>> repositories() {
        Collections.unmodifiableSet(this.repositories);
    }

}