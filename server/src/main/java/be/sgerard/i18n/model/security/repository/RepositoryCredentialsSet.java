package be.sgerard.i18n.model.security.repository;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Set of {@link RepositoryCredentials repository credentials}.
 *
 * @author Sebastien Gerard
 */
public class RepositoryCredentialsSet {

    private final Map<String, RepositoryCredentials> repositoryCredentials;

    public RepositoryCredentialsSet() {
        this(new HashSet<>());
    }

    public RepositoryCredentialsSet(Collection<RepositoryCredentials> repositoryCredentials) {
        this.repositoryCredentials = repositoryCredentials.stream().collect(toMap(RepositoryCredentials::getRepository, auth -> auth));
    }

    /**
     * Returns the {@link RepositoryCredentials credentials} to use for the specified repository.
     */
    public <A extends RepositoryCredentials> Optional<A> getCredentials(String repository, Class<A> expectedType) {
        return Optional.ofNullable(repositoryCredentials.get(repository))
                .map(expectedType::cast);
    }

    /**
     * Returns all the available {@link RepositoryCredentials credentials}.
     */
    public Collection<RepositoryCredentials> getAllCredentials() {
        return repositoryCredentials.values();
    }

    /**
     * Updates credentials of a repository.
     */
    public RepositoryCredentialsSet update(RepositoryCredentials repositoryCredentials) {
        final Set<RepositoryCredentials> updated = new HashSet<>(this.repositoryCredentials.values());
        updated.add(repositoryCredentials);

        return new RepositoryCredentialsSet(updated);
    }

    /**
     * Removes credentials of a repository.
     */
    public RepositoryCredentialsSet remove(String repositoryId) {
        return new RepositoryCredentialsSet(
                repositoryCredentials.values().stream()
                        .filter(cred -> !Objects.equals(repositoryId, cred.getRepository()))
                        .collect(toList())
        );
    }
}
