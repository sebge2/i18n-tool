package be.sgerard.test.i18n.support;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static be.sgerard.test.i18n.model.UserDtoTestUtils.JOHN_DOE;
import static be.sgerard.test.i18n.model.UserDtoTestUtils.JOHN_DOE_PASSWORD;

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
     * The password to be used.
     */
    String password() default JOHN_DOE_PASSWORD;

    /**
     * The email to be used.
     */
    String email() default JOHN_DOE_PASSWORD;

    /**
     * The {@link be.sgerard.i18n.service.security.UserRole user roles} to use.
     */
    String[] roles() default {};

}
