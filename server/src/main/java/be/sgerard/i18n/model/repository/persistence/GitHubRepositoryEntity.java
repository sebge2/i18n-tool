package be.sgerard.i18n.model.repository.persistence;

import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.github.GitHubRepositoryId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Github {@link GitRepositoryEntity repository}.
 *
 * @author Sebastien Gerard
 */
@Getter
@Setter
@Accessors(chain = true)
public class GitHubRepositoryEntity extends BaseGitRepositoryEntity {

    /**
     * The GitHub username owner of the repository.
     */
    @NotNull
    private String username;

    /**
     * The GitHub repository name.
     */
    @NotNull
    private String repository;

    /**
     * The access key to use to access this repository.
     */
    private String accessKey;

    /**
     * The secret shared when GitHub access this app via a web-hook.
     */
    private String webHookSecret;

    @PersistenceConstructor
    GitHubRepositoryEntity() {
        super();
    }

    public GitHubRepositoryEntity(String username, String repository) {
        super(new GitHubRepositoryId(username, repository).toFullName());

        this.username = username;
        this.repository = repository;

        setLocation(getGlobalId().toURI().toString());
    }

    @Override
    public RepositoryType getType() {
        return RepositoryType.GITHUB;
    }

    /**
     * @see #accessKey
     */
    public Optional<String> getAccessKey() {
        return Optional.ofNullable(accessKey);
    }

    /**
     * @see #webHookSecret
     */
    public Optional<String> getWebHookSecret() {
        return Optional.ofNullable(webHookSecret);
    }

    /**
     * Returns the {@link GitHubRepositoryId id} of this repository among all GitHub repositories.
     */
    public GitHubRepositoryId getGlobalId(){
        return new GitHubRepositoryId(getUsername(), getRepository());
    }
}
