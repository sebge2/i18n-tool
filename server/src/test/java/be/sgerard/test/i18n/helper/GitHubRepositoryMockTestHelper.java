package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryDto;
import be.sgerard.i18n.model.repository.github.dto.GitHubPullRequestDto;
import be.sgerard.i18n.model.repository.github.external.GitHubPullRequestStatus;
import be.sgerard.test.i18n.mock.GitHubClientMock;
import junit.framework.AssertionFailedError;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
@Component
public class GitHubRepositoryMockTestHelper {

    private final GitHubClientMock gitHubClient;
    private final GitRepositoryMockTestHelper gitRepositoryHelper;

    public GitHubRepositoryMockTestHelper(GitHubClientMock gitHubClient, GitRepositoryMockTestHelper gitRepositoryHelper) {
        this.gitHubClient = gitHubClient;
        this.gitRepositoryHelper = gitRepositoryHelper;
    }

    StepRepository forRepository(GitHubRepositoryDto repositoryDto) {
        return new StepRepository(repositoryDto);
    }

    public final class StepRepository {

        private final GitHubRepositoryDto repositoryDto;

        public StepRepository(GitHubRepositoryDto repositoryDto) {
            this.repositoryDto = repositoryDto;
        }

        public Optional<StepPullRequest> findPullRequestForBranch(String targetBranch) {
            return gitHubClient
                    .findAllNonBlocking().stream()
                    .filter(request -> Objects.equals(repositoryDto.getName(), request.getRepositoryName()))
                    .filter(request -> Objects.equals(targetBranch, request.getTargetBranch()))
                    .findFirst()
                    .map(request -> new StepPullRequest(repositoryDto, request, gitHubClient));
        }

        public StepPullRequest findPullRequestForBranchOrDie(String branch) {
            return findPullRequestForBranch(branch)
                    .orElseThrow(() -> new AssertionFailedError("There is no pull request for branch [" + branch + "]."));
        }

        @SuppressWarnings("UnusedReturnValue")
        public StepRepository resetPullRequests() {
            gitHubClient.resetPullRequests();
            return this;
        }
    }

    public final class StepPullRequest {

        private final GitHubPullRequestDto pullRequest;
        private final GitHubRepositoryDto repositoryDto;
        private final GitHubClientMock gitHubClient;
        private final GitRepositoryMock repositoryMock;

        public StepPullRequest(GitHubRepositoryDto repositoryDto, GitHubPullRequestDto pullRequest, GitHubClientMock gitHubClient) {
            this.pullRequest = pullRequest;
            this.repositoryDto = repositoryDto;
            this.gitHubClient = gitHubClient;
            this.repositoryMock = gitRepositoryHelper.getRepo(repositoryDto);
        }

        public StepRepository and() {
            return new StepRepository(repositoryDto);
        }

        public GitHubPullRequestDto get() {
            return pullRequest;
        }

        @SuppressWarnings("UnusedReturnValue")
        public StepPullRequest close() {
            gitHubClient.updatePullRequestStatus(repositoryDto.getId(), pullRequest.getTargetBranch(), GitHubPullRequestStatus.CLOSED);

            repositoryMock.mergeTo(pullRequest.getCurrentBranch(), pullRequest.getTargetBranch());

            return this;
        }
    }
}
