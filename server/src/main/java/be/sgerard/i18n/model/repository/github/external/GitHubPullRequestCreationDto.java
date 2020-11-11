package be.sgerard.i18n.model.repository.github.external;

import be.sgerard.i18n.model.repository.github.GitHubPullRequestCreationInfo;
import lombok.Getter;

/**
 * DTO asking the creation of a {@link GitHubPullRequestDto pull-request}.
 *
 * @author Sebastien Gerard
 */
@Getter
public class GitHubPullRequestCreationDto {

    /**
     * Maps the {@link GitHubPullRequestCreationInfo creation info} to the {@link GitHubPullRequestCreationDto DTO}.
     */
    public static GitHubPullRequestCreationDto toDto(GitHubPullRequestCreationInfo creationInfo) {
        return new GitHubPullRequestCreationDto(creationInfo.getTitle(), creationInfo.getCurrentBranch(), creationInfo.getTargetBranch(), "");
    }

    /**
     * Pull request title.
     */
    private final String title;

    /**
     * Branch of the pull-request to merge (example, branch = develop).
     * <p>
     * Format if cross-repository: [USERNAME]:[BRANCH].
     *
     * @see GitHubPullRequestDto#getHead()
     */
    private final String head;

    /**
     * Branch target of the pull-request (example, branch = master).
     * <p>
     * Format if cross-repository: [USERNAME]:[BRANCH].
     *
     * @see GitHubPullRequestDto#getBase()
     */
    private final String base;

    /**
     * Free body displayed to the end-user.
     */
    private final String body;

    public GitHubPullRequestCreationDto(String title,
                                        String head,
                                        String base,
                                        String body) {
        this.title = title;
        this.head = head;
        this.base = base;
        this.body = body;
    }
}
