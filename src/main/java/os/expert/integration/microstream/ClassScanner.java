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
     * Returns the classes that that has the {@link Entity} annotation
     *
     * @return classes with {@link Entity} annotation
     */
    public Optional<Class<?>> entity() {
        return Optional.ofNullable(entity);
    }

    /**
     * Returns repositories: interfaces that extend DataRepository and has the Repository annotation.
     *
     * @return the repositories items
     */
    public Optional<Class<?>> repository() {
        return Optional.ofNullable(repository);
    }

}