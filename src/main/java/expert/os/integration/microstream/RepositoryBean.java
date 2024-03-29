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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.inject.spi.PassivationCapable;
import jakarta.enterprise.util.AnnotationLiteral;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RepositoryBean<T> implements Bean<T>, PassivationCapable {

    /**
     * Annotation literal {@link Default}
     */
    private static final AnnotationLiteral<Default> DEFAULT_ANNOTATION = new AnnotationLiteral<>() {
    };

    /**
     * Annotation literal {@link Any}
     */
    private static final AnnotationLiteral<Any> ANY_ANNOTATION = new AnnotationLiteral<>() {
    };

    private static final AnnotationLiteral<Microstream> MICROSTREAM = new AnnotationLiteral<>() {
    };

    private final Class type;

    private final Set<Type> types;
    private final Set<Annotation> qualifiers;

    /**
     * Constructor
     *
     * @param type the tye
     */
    public RepositoryBean(Class type) {
        this.type = type;
        this.types = Collections.singleton(type);
        this.qualifiers = new HashSet<>();
        qualifiers.add(DEFAULT_ANNOTATION);
        qualifiers.add(ANY_ANNOTATION);
        qualifiers.add(MICROSTREAM);
    }

    @Override
    public Class<?> getBeanClass() {
        return type;
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        MicrostreamTemplate template = getInstance(MicrostreamTemplate.class);
        return (T) RepositoryProxySupplier.INSTANCE.get(type, template);
    }

    @Override
    public Set<Type> getTypes() {
        return types;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    @Override
    public String getId() {
        return type.getName();
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    public boolean isNullable() {
        return false;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ApplicationScoped.class;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public void destroy(T instance, CreationalContext<T> context) {

    }

    private <T> T getInstance(Class<T> bean) {
        return CDI.current().select(bean).get();
    }
}
