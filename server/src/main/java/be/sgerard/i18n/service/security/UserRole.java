package be.sgerard.i18n.service.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * All possible security roles for a user.
 *
 * @author Sebastien Gerard
 */
public enum UserRole {

    /**
     * The user is allowed to access the application.
     * <p>
     * This role will be automatically assigned by the authentication mechanism.
     */
    MEMBER_OF_ORGANIZATION(false),

    /**
     * The user is allowed to access and modify the repository.
     * <p>
     * This role will be automatically assigned by the authentication mechanism.
     */
    MEMBER_OF_REPOSITORY(false),

    /**
     * Administrator role with all privileges.
     */
    ADMIN(true);

    public static final String ROLE_PREFIX = "ROLE_";

    private final boolean assignableByEndUser;

    UserRole(boolean assignableByEndUser) {
        this.assignableByEndUser = assignableByEndUser;
    }

    /**
     * Returns the associated {@link GrantedAuthority security authority}.
     */
    public GrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority(ROLE_PREFIX + name());
    }

    /**
     * Returns whether this role can be assigned by a user, or if it's a technical one.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isAssignableByEndUser() {
        return assignableByEndUser;
    }
}
