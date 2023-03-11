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

import jakarta.data.repository.CrudRepository;

import java.lang.reflect.Proxy;

enum RepositoryProxySupplier {

    INSTANCE;

    /**
     * Produces a Repository class from repository class and {@link MicrostreamTemplate}
     *
     * @param repositoryClass the repository class
     * @param template        the template
     * @param <T>             the entity of repository
     * @param <K>             the K of the entity
     * @param <R>             the repository type
     * @return a Repository interface
     */
    <T, K, R extends CrudRepository<T, K>> R get(Class<R> repositoryClass, MicrostreamTemplate template) {
        MicrostreamRepository<T, K> repository = new MicrostreamRepository<>(template);
        RepositoryProxy<T, K> handler = new RepositoryProxy<>(repository, template);
        return (R) Proxy.newProxyInstance(repositoryClass.getClassLoader(),
                new Class[]{repositoryClass},
                handler);
    }
}
