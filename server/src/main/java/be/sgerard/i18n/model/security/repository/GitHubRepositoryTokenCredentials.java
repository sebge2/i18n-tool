package be.sgerard.i18n.model.security.repository;

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
    private final String userToken;
    private final String repositoryAccessToken;
    private final String userDisplayName;
    private final String userEmail;
    private final Set<UserRole> sessionRoles;

    public GitHubRepositoryTokenCredentials(String repository,
                                            String userToken,
                                            String repositoryAccessToken,
                                            String userDisplayName,
                                            String userEmail) {
        this.repository = repository;
        this.userToken = userToken;
        this.repositoryAccessToken = repositoryAccessToken;
        this.userDisplayName = userDisplayName;
        this.userEmail = userEmail;

        this.sessionRoles = new HashSet<>(singletonList(UserRole.MEMBER_OF_REPOSITORY));
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
    public Optional<String> getRepositoryAccessToken() {
        return Optional.ofNullable(repositoryAccessToken);
    }

    /**
     * Returns the token to use to interact with the repository.
     */
    public Optional<String> getToken() {
        return this
                .getUserToken()
                .or(this::getRepositoryAccessToken);
    }

    /**
     * Returns whether credentials are present.
     */
    public boolean hasCredentials(){
        return getToken().isPresent();
    }

    /**
     * Returns the name to display for the associated user.
     */
    public Optional<String> getUserDisplayName() {
        return Optional.ofNullable(userDisplayName);
    }

    /**
     * Returns the email of the associated user.
     */
    public Optional<String> getUserEmail() {
        return Optional.ofNullable(userEmail);
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
