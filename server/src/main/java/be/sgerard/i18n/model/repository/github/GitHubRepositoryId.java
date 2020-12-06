package be.sgerard.i18n.model.repository.github;

import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import lombok.Value;

import java.net.URI;

/**
 * Id of a GitHub repository.
 *
 * @author Sebastien Gerard
 */
@Value
@SuppressWarnings("RedundantModifiersValueLombok")
public class GitHubRepositoryId {

    /**
     * GitHub user name owner of the repository.
     */
    private final String username;

    /**
     * Unique name of the repository.
     */
    private final String repositoryName;

    public GitHubRepositoryId(String username, String repositoryName) {
        this.username = username;
        this.repositoryName = repositoryName;
    }

    public GitHubRepositoryId(GitHubRepositoryEntity repository) {
        this(repository.getUsername(), repository.getRepository());
    }

    /**
     * Returns the full repository name (composed of the owner's login and repository name).
     */
    public String toFullName() {
        return String.format("%s/%s", getUsername(), getRepositoryName());
    }

    /**
     * Creates the GitHub repository URI based on the owner and the repository name.
     */
    public URI toURI() {
        return URI.create(String.format("https://github.com/%s/%s.git", getUsername(), getRepositoryName()));
    }

}
