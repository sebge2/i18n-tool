package be.sgerard.i18n.model.workspace.snapshot;

import be.sgerard.i18n.model.repository.snapshot.RepositorySnapshotDto;
import be.sgerard.i18n.model.workspace.persistence.GitHubReviewEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

/**
 * Snapshot of a {@link GitHubReviewEntity GitHub review}.
 *
 * @author Sebastien Gerard
 */
@JsonDeserialize(builder = GitHubReviewSnapshotDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class GitHubReviewSnapshotDto extends AbstractReviewSnapshotDto {

    /**
     * @see GitHubReviewEntity#getPullRequestBranch()
     */
    private final String pullRequestBranch;

    /**
     * @see GitHubReviewEntity#getPullRequestNumber()
     */
    private final Integer pullRequestNumber;

    @Override
    public RepositorySnapshotDto.Type getType() {
        return RepositorySnapshotDto.Type.GIT_HUB;
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
