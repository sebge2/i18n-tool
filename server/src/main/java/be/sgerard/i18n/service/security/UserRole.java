package be.sgerard.i18n.service.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * @author Sebastien Gerard
 */
public enum UserRole {

    MEMBER_OF_ORGANIZATION(false),

    REPO_MEMBER(false),

    ADMIN(true);

    public static final String ROLE_PREFIX = "ROLE_";

    private final boolean assignableByEndUser;

    UserRole(boolean assignableByEndUser) {
        this.assignableByEndUser = assignableByEndUser;
    }

    public GrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority(ROLE_PREFIX + name());
    }

    public boolean isAssignableByEndUser() {
        return assignableByEndUser;
    }
}
