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


import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import jakarta.data.exceptions.MappingException;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Repository;
import jakarta.nosql.Entity;

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

    private final Class<?> entity;
    private final Class<?> repository;


    ClassScanner() {
        Set<Class<?>> entities = new HashSet<>();
        Set<Class<?>> repositories = new HashSet<>();

        Logger logger = Logger.getLogger(ClassScanner.class.getName());
        logger.fine("Starting scan class to find entities, embeddable and repositories.");
        try (ScanResult result = new ClassGraph().enableAllInfo().scan()) {
            entities.addAll(result.getClassesWithAnnotation(Entity.class).loadClasses());
            repositories.addAll(result.getClassesWithAnnotation(Repository.class)
                    .getInterfaces().loadClasses(DataRepository.class));
        }
        logger.fine(String.format("Finished the class scan with entities %d, and repositories: %d"
                , entities.size(), repositories.size()));

        if (entities.size() > 1) {
            throw new MappingException("Microstream Jakarta Data does not support more than one entity using jakarta.nosql.Entity");
        }

        if (repositories.size() > 1) {
            throw new MappingException("Microstream Jakarta Data does not support more than one Repository");
        }

        this.entity = entities.stream().findFirst().orElse(null);
        this.repository = repositories.stream().findFirst().orElse(null);
    }


    /**
     * Returns the class that that has the {@link Entity} annotation
     *
     * @return the class with {@link Entity} annotation or {@link Optional#empty()}
     */
    public Optional<Class<?>> entity() {
        return Optional.ofNullable(entity);
    }

    /**
     * Returns repository: interface that extend DataRepository and has the Repository annotation.
     *
     * @return the repository or the {@link Optional#empty()}
     */
    public Optional<Class<?>> repository() {
        return Optional.ofNullable(repository);
    }

}