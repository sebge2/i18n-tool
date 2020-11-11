package be.sgerard.i18n.model.repository.github.external;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Repository in GitHub.
 *
 * @author Sebastien Gerard
 */
@Getter
public class GitHubRepositoryDto {

    /**
     * Unique id of the repository.
     */
    private final String id;

    /**
     * {@link GitHubRepositoryOwnerDto Owner} of this repository.
     */
    private final GitHubRepositoryOwnerDto owner;

    /**
     * Unique name of the repository.
     */
    private final String name;

    /**
     * Full name of the repository ([login]/[name]).
     */
    private final String fullName;

    /**
     * Flag indicating whether the repository is private (or public).
     */
    private final boolean privateRepo;

    @JsonCreator
    public GitHubRepositoryDto(@JsonProperty("id") String id,
                               @JsonProperty("owner") GitHubRepositoryOwnerDto owner,
                               @JsonProperty("name") String name,
                               @JsonProperty("full_name") String fullName,
                               @JsonProperty("private") boolean privateRepo) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.fullName = fullName;
        this.privateRepo = privateRepo;
    }
}
