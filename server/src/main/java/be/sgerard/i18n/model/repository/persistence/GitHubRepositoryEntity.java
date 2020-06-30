package be.sgerard.i18n.model.repository.persistence;

import be.sgerard.i18n.model.repository.RepositoryType;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.regex.Pattern;

import static be.sgerard.i18n.support.GitHubUtils.createGitHubUrl;

/**
 * Github {@link GitRepositoryEntity repository}.
 *
 * @author Sebastien Gerard
 */
public class GitHubRepositoryEntity extends BaseGitRepositoryEntity {

    @NotNull
    private String username;

    @NotNull
    private String repository;

    private String accessKey;

    private String webHookSecret;

    @PersistenceConstructor
    GitHubRepositoryEntity() {
        super();
    }

    public GitHubRepositoryEntity(String username, String repository) {
        super(username + "/" + repository);

        this.username = username;
        this.repository = repository;

        setLocation(createGitHubUrl(username, repository).toString());
    }

    @Override
    public RepositoryType getType() {
        return RepositoryType.GITHUB;
    }

    /**
     * Returns the GitHub username owner of the repository.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the GitHub username owner of the repository.
     */
    public GitHubRepositoryEntity setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * Returns the GitHub repository name.
     */
    public String getRepository() {
        return repository;
    }

    /**
     * Sets the GitHub repository name.
     */
    public GitHubRepositoryEntity setRepository(String repository) {
        this.repository = repository;
        return this;
    }

    /**
     * Returns the access key to use to access this repository.
     */
    public Optional<String> getAccessKey() {
        return Optional.ofNullable(accessKey);
    }

    /**
     * Sets the access key to use to access this repository.
     */
    public GitHubRepositoryEntity setAccessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    /**
     * Returns the secret shared when GitHub access this app via a web-hook.
     */
    public Optional<String> getWebHookSecret() {
        return Optional.ofNullable(webHookSecret);
    }

    /**
     * Sets the secret shared when GitHub access this app via a web-hook.
     */
    public GitHubRepositoryEntity setWebHookSecret(String webHookSecret) {
        this.webHookSecret = webHookSecret;
        return this;
    }

    @Override
    public GitHubRepositoryEntity setLocation(String location) {
        return (GitHubRepositoryEntity) super.setLocation(location);
    }


    @Override
    public GitHubRepositoryEntity setAllowedBranches(Pattern allowedBranches) {
        return (GitHubRepositoryEntity) super.setAllowedBranches(allowedBranches);
    }
}
