package be.sgerard.i18n.model.security.auth;

import be.sgerard.i18n.service.security.UserRole;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link RepositoryCredentials Repository credentials} based on a token.
 *
 * @author Sebastien Gerard
 */
public class RepositoryTokenCredentials implements RepositoryCredentials {

    private final String repository;
    private final Set<UserRole> sessionRoles;
    private final String token;

    public RepositoryTokenCredentials(String repository, Collection<UserRole> sessionRoles, String token) {
        this.repository = repository;
        this.sessionRoles = new HashSet<>(sessionRoles);
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
}
