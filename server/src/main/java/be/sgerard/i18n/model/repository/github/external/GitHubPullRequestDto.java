package be.sgerard.i18n.model.repository.github.external;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Pull-request on GitHub.
 *
 * @author Sebastien Gerard
 */
@Getter
public class GitHubPullRequestDto {

    /**
     * Maps the specified {@link be.sgerard.i18n.model.repository.github.external.GitHubPullRequestDto DTO} to the internal representation
     * of a {@link be.sgerard.i18n.model.repository.github.dto.GitHubPullRequestDto pull-request}.
     */
    public static be.sgerard.i18n.model.repository.github.dto.GitHubPullRequestDto fromDto(GitHubPullRequestDto dto) {
        return be.sgerard.i18n.model.repository.github.dto.GitHubPullRequestDto.builder()
                .repositoryName(dto.getBase().getRepo().getName())
                .number(dto.getNumber())
                .currentBranch(dto.getHead().getRef())
                .targetBranch(dto.getBase().getRef())
                .status(dto.getState())
                .build();
    }

    /**
     * Unique id of the pull-request.
     */
    private final String id;

    /**
     * Pull-request number.
     */
    private final int number;

    /**
     * Pull-request state.
     */
    private final GitHubPullRequestStatus state;

    /**
     * {@link GitHubBranchRefDto Reference} of the branch which is the the branch to merge into the {@link #base}.
     */
    private final GitHubBranchRefDto head;

    /**
     * {@link GitHubBranchRefDto Reference} of the branch which is the target of the pull-request.
     */
    private final GitHubBranchRefDto base;

    @JsonCreator
    public GitHubPullRequestDto(@JsonProperty("id") String id,
                                @JsonProperty("number") int number,
                                @JsonProperty("state") GitHubPullRequestStatus state,
                                @JsonProperty("head") GitHubBranchRefDto head,
                                @JsonProperty("base") GitHubBranchRefDto base) {
        this.id = id;
        this.number = number;
        this.state = state;
        this.head = head;
        this.base = base;
    }
}
