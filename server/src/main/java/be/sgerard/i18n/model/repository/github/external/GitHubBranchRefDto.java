package be.sgerard.i18n.model.repository.github.external;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Reference of a branch on GitHub.
 *
 * @author Sebastien Gerard
 */
@Getter
public class GitHubBranchRefDto {

    /**
     * Branch name.
     */
    private final String ref;

    /**
     * The {@link GitHubRepositoryDto repository} associated to this request.
     */
    private final GitHubRepositoryDto repo;

    @JsonCreator
    public GitHubBranchRefDto(@JsonProperty("ref") String ref,
                              @JsonProperty("repo") GitHubRepositoryDto repo) {
        this.ref = ref;
        this.repo = repo;
    }
}
