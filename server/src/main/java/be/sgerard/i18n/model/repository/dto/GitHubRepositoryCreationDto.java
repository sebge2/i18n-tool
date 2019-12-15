package be.sgerard.i18n.model.repository.dto;

import be.sgerard.i18n.model.repository.RepositoryType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

/**
 * Request asking the creation of a GitHub repository.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "GitHubRepositoryCreationRequest", description = "Request asking the creation of a GitHub repository")
public class GitHubRepositoryCreationDto extends RepositoryCreationDto {

    @Schema(description = "GitHub username of this repository", required = true)
    private final String username;

    @Schema(description = "Repository name", required = true)
    private final String repository;

    @Schema(description = "Access key to use to access this repository")
    private final String accessKey;

    @JsonCreator
    public GitHubRepositoryCreationDto(@JsonProperty("username") String username,
                                       @JsonProperty("repository") String repository,
                                       @JsonProperty("accessKey") String accessKey) {
        this.username = username;
        this.repository = repository;
        this.accessKey = accessKey;
    }

    @Override
    public RepositoryType getType() {
        return RepositoryType.GITHUB;
    }

    /**
     * Returns the GitHub username of this repository.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the repository name of GitHub.
     */
    public String getRepository() {
        return repository;
    }

    /**
     * Returns the access key to use to access this repository.
     */
    public Optional<String> getAccessKey() {
        return Optional.ofNullable(accessKey);
    }
}
