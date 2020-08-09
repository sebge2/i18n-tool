package be.sgerard.i18n.model.security.auth.internal;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.service.security.UserRole;

import java.util.*;

/**
 * {@link AuthenticatedUser Authenticated internal user}.
 *
 * @author Sebastien Gerard
 */
public class InternalAuthenticatedUser implements AuthenticatedUser {

    private final String id;
    private final String userId;
    private final Set<UserRole> roles;

    public InternalAuthenticatedUser(String id, String userId, Collection<UserRole> roles) {
        this.id = id;
        this.userId = userId;
        this.roles = Set.copyOf(roles);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return getUserId();
    }

    @Override
    public Collection<UserRole> getSessionRoles() {
        return roles;
    }

    @Override
    public InternalAuthenticatedUser updateSessionRoles(List<UserRole> sessionRoles) {
        return new InternalAuthenticatedUser(id, userId, sessionRoles);
    }

    @Override
    public <A extends RepositoryCredentials> Optional<A> getCredentials(String repository, Class<A> expectedType) {
        return Optional.empty();
    }

    @Override
    public Collection<RepositoryCredentials> getRepositoryCredentials() {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) return false;

        final InternalAuthenticatedUser that = (InternalAuthenticatedUser) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
