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

    @Inject
    @Microstream
    private Template microTemplate;

    @Inject
    private Library library;

    @Inject
    @Microstream
    private Library libraryMicro;

    @Test
    public void shouldIntegrate() {
        Assertions.assertNotNull(template);
        Assertions.assertNotNull(microstreamTemplate);
        Assertions.assertNotNull(library);
        Assertions.assertNotNull(libraryMicro);
        Assertions.assertNotNull(microTemplate);
    }
}


