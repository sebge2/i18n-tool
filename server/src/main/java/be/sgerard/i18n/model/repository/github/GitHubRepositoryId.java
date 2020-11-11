package be.sgerard.i18n.model.repository.github;

import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import lombok.Value;

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
}
