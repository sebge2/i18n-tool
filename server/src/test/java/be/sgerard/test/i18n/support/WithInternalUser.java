package be.sgerard.test.i18n.support;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static be.sgerard.test.i18n.model.UserDtoTestUtils.JOHN_DOE;

/**
 * @author Sebastien Gerard
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithInternalUserSecurityContextFactory.class)
public @interface WithInternalUser {

    /**
     * The username to be used.
     */
    String username() default JOHN_DOE;

    /**
     * The {@link be.sgerard.i18n.service.security.UserRole user roles} to use.
     */
    String[] roles() default {};

}
