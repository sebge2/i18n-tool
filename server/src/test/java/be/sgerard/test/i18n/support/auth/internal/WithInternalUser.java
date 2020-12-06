package be.sgerard.test.i18n.support.auth.internal;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Sebastien Gerard
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithInternalUserSecurityContextFactory.class)
public @interface WithInternalUser {

    /**
     * The username to be used.
     */
    String username();

    /**
     * The user display name to be used.
     */
    String displayName();

    /**
     * The password to be used.
     */
    String password();

    /**
     * The email to be used.
     */
    String email();

    /**
     * The {@link be.sgerard.i18n.service.security.UserRole user roles} to use.
     */
    String[] roles() default {};

}
