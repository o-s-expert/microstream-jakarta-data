package os.expert.integration.microstream;


import jakarta.inject.Inject;
import jakarta.nosql.Template;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@EnableAutoWeld
@AddPackages(value = ReturnType.class)
@AddPackages(Animal.class)
@AddExtensions(MicrostreamExtension.class)
public class MicrostreamExtensionTest {
    @Inject
    private Template template;

    @Inject
    private MicrostreamTemplate microstreamTemplate;


    @Test
    public void shouldIntegrate() {
        Assertions.assertNotNull(template);
        Assertions.assertNotNull(microstreamTemplate);
    }
}

