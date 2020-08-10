package be.sgerard.i18n.model.security.auth;

import be.sgerard.i18n.service.security.UserRole;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Collections.singletonList;

/**
 * {@link RepositoryCredentials Repository credentials} based on a token.
 *
 * @author Sebastien Gerard
 */
public class RepositoryTokenCredentials implements RepositoryCredentials {

    private final String repository;
    private final Set<UserRole> sessionRoles;
    private final String token;

    public RepositoryTokenCredentials(String repository, String token) {
        this.repository = repository;
        this.sessionRoles = new HashSet<>(singletonList(UserRole.MEMBER_OF_REPOSITORY));
        this.token = token;
    }

    @Override
    public String getRepository() {
        return repository;
    }

    @Override
    public Collection<UserRole> getSessionRoles() {
        return sessionRoles;
    }

    /**
     * Returns the token to use to interact with the repository.
     */
    public String getToken() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final RepositoryTokenCredentials that = (RepositoryTokenCredentials) o;

        return Objects.equals(repository, that.repository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repository);
    }
}
