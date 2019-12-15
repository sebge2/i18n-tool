package be.sgerard.test.i18n.support;

import org.springframework.security.core.GrantedAuthority;
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
     * The roles to use. The default is "ROLE_USER". A {@link GrantedAuthority} will be created
     * for each value within roles. Each value in roles will automatically be prefixed
     * with "ROLE_". For example, the default will result in "ROLE_USER" being used.
     */
    String[] roles() default {"USER"};

}
