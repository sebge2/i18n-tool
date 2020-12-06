package be.sgerard.test.i18n.support.auth.external.github;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Sebastien Gerard
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithGitHubExternalUserSecurityContextFactory.class)
public @interface WithGitHubExternalUser {

    /**
     * The username to be used.
     */
    String username();

    /**
     * The unique token to be used.
     */
    String token();

    /**
     * The user display name to be used.
     */
    String displayName();

    /**
     * The email to be used.
     */
    String email();

    /**
     * The {@link be.sgerard.i18n.service.security.UserRole user roles} to use.
     */
    String[] roles() default {};

    /**
     * Flag indicating whether the user has been authorized to access this app.
     */
    boolean authorized() default true;

}
