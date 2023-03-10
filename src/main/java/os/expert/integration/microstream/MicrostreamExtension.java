package os.expert.integration.microstream;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;

import java.util.logging.Logger;

public class MicrostreamExtension implements Extension {

    private static final Logger LOGGER = Logger.getLogger(MicrostreamExtension.class.getName());

    void onAfterBeanDiscovery(@Observes final AfterBeanDiscovery afterBeanDiscovery) {
        LOGGER.fine("Starting Microstream extention to check entity and repository classes");
        ClassScanner scanner = ClassScanner.INSTANCE;
        scanner.repository().stream().forEach(type -> {
            afterBeanDiscovery.addBean(new RepositoryBean(type));
        });
    }
}
