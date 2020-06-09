package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.github.GitHubPullRequestStatus;
import be.sgerard.i18n.service.github.external.BaseGitHubWebHookEventDto;
import be.sgerard.i18n.service.github.external.GitHubPullRequestEventDto;

/**
 * @author Sebastien Gerard
 */
public final class GitHubBranchPullRequestEventDtoTestUtils {

    private GitHubBranchPullRequestEventDtoTestUtils() {
    }

    public static GitHubPullRequestEventDto i18nToolRelease20204PullRequestEvent() {
        return new GitHubPullRequestEventDto(
                new BaseGitHubWebHookEventDto.Repository("048650d2-7a90-4086-9315-2960acf71099", "sebge2/i18n-tool"),
                new GitHubPullRequestEventDto.PullRequest(
                        "68bf481a-8419-48d2-b2f1-252cbea45342",
                        1,
                        GitHubPullRequestStatus.CLOSED
                )
        );
    }
}
