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
