package be.sgerard.test.i18n.helper.repository.github;

import be.sgerard.i18n.model.repository.github.dto.GitHubPullRequestDto;
import be.sgerard.test.i18n.mock.repository.github.RemoteGitHubRepositoryMock;
import junit.framework.AssertionFailedError;

import java.util.Optional;

/**
 * Manages all pull-requests of a remote GitHub repository.
 *
 * @author Sebastien Gerard
 */
@SuppressWarnings("UnusedReturnValue")
public class StepRemoteGitHubRepositoryPullRequest {

    private final RemoteGitHubRepositoryMock gitHubRepositoryMock;

    public StepRemoteGitHubRepositoryPullRequest(RemoteGitHubRepositoryMock gitHubRepositoryMock) {
        this.gitHubRepositoryMock = gitHubRepositoryMock;
    }

    /**
     * Returns the GitHub mock for the current repository.
     */
    public RemoteGitHubRepositoryMock and() {
        return gitHubRepositoryMock;
    }

    /**
     * Returns the pull request for the target branch.
     */
    public Optional<StepPullRequest> forTargetBranch(String targetBranch) {
        return gitHubRepositoryMock
                .findPullRequestByTargetBranch(targetBranch)
                .map(StepPullRequest::new);
    }

    /**
     * Returns the pull request for the target branch.
     */
    public StepPullRequest forTargetBranchOrDie(String branch) {
        return forTargetBranch(branch)
                .orElseThrow(() -> new AssertionFailedError("There is no pull request for branch [" + branch + "]."));
    }

    /**
     * Closes all the pull requests of the current repository.
     */
    public StepRemoteGitHubRepositoryPullRequest closeAll() {
        gitHubRepositoryMock.closeAllPullRequests();
        return this;
    }

    /**
     * Possible actions on a specific pull-request.
     */
    public final class StepPullRequest {

        private final RemoteGitHubRepositoryMock.PullRequest pullRequest;

        public StepPullRequest(RemoteGitHubRepositoryMock.PullRequest pullRequest) {
            this.pullRequest = pullRequest;
        }

        /**
         * Returns actions to apply on pull-requests of the current repository.
         */
        public StepRemoteGitHubRepositoryPullRequest and() {
            return StepRemoteGitHubRepositoryPullRequest.this;
        }

        /**
         * Returns this pull-request.
         */
        public GitHubPullRequestDto get() {
            return pullRequest.toDto();
        }

        /**
         * Closes this pull-request.
         */
        @SuppressWarnings("UnusedReturnValue")
        public StepPullRequest close() {
            gitHubRepositoryMock.closePullRequest(pullRequest);

            return this;
        }
    }
}
