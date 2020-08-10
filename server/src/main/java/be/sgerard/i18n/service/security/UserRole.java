package be.sgerard.i18n.service.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;

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

    /**
     * Maps the specified roles to authorities.
     */
    public static Set<GrantedAuthority> mapToAuthorities(Collection<UserRole> roles, Collection<GrantedAuthority> additionalAuthorities) {
        return Stream
                .concat(roles.stream().map(UserRole::toAuthority), additionalAuthorities.stream())
                .collect(toSet());
    }

    /**
     * Maps the specified roles to authorities.
     */
    public static Set<GrantedAuthority> mapToAuthorities(Collection<UserRole> roles) {
        return mapToAuthorities(roles, emptyList());
    }

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
    public boolean isAssignableByEndUser() {
        return assignableByEndUser;
    }
}
