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
public class GitRepositoryUserPasswordCredentials implements RepositoryCredentials {

    private final String repository;
    private final String username;
    private final String password;
    private final String userDisplayName;
    private final String userEmail;
    private final Set<UserRole> sessionRoles;

    public GitRepositoryUserPasswordCredentials(String repository,
                                                String username,
                                                String password,
                                                String userDisplayName,
                                                String userEmail) {
        this.repository = repository;
        this.username = username;
        this.password = password;
        this.userDisplayName = userDisplayName;
        this.userEmail = userEmail;
        this.sessionRoles = new HashSet<>(singletonList(UserRole.MEMBER_OF_REPOSITORY));
    }

    public GitRepositoryUserPasswordCredentials(String repository, String username, String password) {
        this(repository, username, password, null, null);
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
     * Returns the username to use to connect to the Git repository.
     */
    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    /**
     * Returns the password to connect to the Git repository.
     */
    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
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

    /**
     * Returns whether credentials are present.
     */
    public boolean hasCredentials(){
        return getUsername().isPresent() || getPassword().isPresent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GitRepositoryUserPasswordCredentials that = (GitRepositoryUserPasswordCredentials) o;

        return Objects.equals(repository, that.repository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repository);
    }
}
