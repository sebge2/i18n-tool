package be.sgerard.i18n.model.workspace.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * {@link ReviewDto Review} of a workspace based on a GitHub repository. The review is thanks to a pull request.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "WorkspaceGitHubReview", description = "Review of a workspace based on a GitHub repository. The review is thanks to a pull request.")
@JsonDeserialize(builder = GitHubReviewDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class GitHubReviewDto implements ReviewDto {

    @Schema(description = "The branch used by the pull request.", required = true)
    private final String pullRequestBranch;

    @Schema(description = "The GitHub pull request number of this review.", required = true)
    private final int pullRequestNumber;

    @Override
    public ReviewType getType() {
        return ReviewType.GIT_HUB;
    }

    /**
     * Builder of {@link GitHubReviewDto GitHub review DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
