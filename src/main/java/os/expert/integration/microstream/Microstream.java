package os.expert.integration.microstream;


import jakarta.inject.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * The Microstream Qualifier where you can specify for both {@link jakarta.nosql.Template}
 * and {@link jakarta.data.repository.PageableRepository}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Qualifier
public @interface Microstream {
}
