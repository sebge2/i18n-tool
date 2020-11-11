package be.sgerard.i18n.model.repository.github.dto;

import be.sgerard.i18n.model.repository.github.external.GitHubPullRequestStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Pull-request of a GitHub repository.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "GitHubPullRequest", description = "Pull-request on GitHub.")
@JsonDeserialize(builder = GitHubPullRequestDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class GitHubPullRequestDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "Name of the repository associated to this request", required = true)
    private final String repositoryName;

    @Schema(description = "Unique id number of this request", required = true)
    private final int number;

    @Schema(description = "The name of the branch containing changes", required = true)
    private final String currentBranch;

    @Schema(description = "The name of the branch where changes should be applied", required = true)
    private final String targetBranch;

    @Schema(description = "Current status of this request", required = true)
    private final GitHubPullRequestStatus status;

    /**
     * Builder of {@link GitHubPullRequestDto pull-request DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
