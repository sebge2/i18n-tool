package be.sgerard.i18n.model.security.auth;

import be.sgerard.i18n.service.security.UserRole;

import java.util.*;

import static java.util.Collections.singletonList;

/**
 * {@link RepositoryCredentials Repository credentials} based on a token that can be
 * either a token coming from the external authentication system, or from an access key.
 *
 * @author Sebastien Gerard
 */
public class GitHubRepositoryTokenCredentials implements RepositoryCredentials {

    private final String repository;
    private final Set<UserRole> sessionRoles;
    private final String userToken;
    private final String repositoryAccessKey;

    public GitHubRepositoryTokenCredentials(String repository, String userToken, String repositoryAccessKey) {
        this.repository = repository;
        this.sessionRoles = new HashSet<>(singletonList(UserRole.MEMBER_OF_REPOSITORY));

        if ((userToken == null) && (repositoryAccessKey == null)) {
            throw new IllegalArgumentException("No token has been defined.");
        }

        this.userToken = userToken;
        this.repositoryAccessKey = repositoryAccessKey;
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
     * Returns the current token associated to the user himself.
     */
    public Optional<String> getUserToken() {
        return Optional.ofNullable(userToken);
    }

    /**
     * Returns the access key associated to the repository itself.
     */
    public Optional<String> getRepositoryAccessKey() {
        return Optional.ofNullable(repositoryAccessKey);
    }

    /**
     * Returns the token to use to interact with the repository.
     */
    public String getToken() {
        return this
                .getUserToken()
                .or(this::getRepositoryAccessKey)
                .orElseThrow(() -> new IllegalStateException("No token defined."));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GitHubRepositoryTokenCredentials that = (GitHubRepositoryTokenCredentials) o;

        return Objects.equals(repository, that.repository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repository);
    }
}
