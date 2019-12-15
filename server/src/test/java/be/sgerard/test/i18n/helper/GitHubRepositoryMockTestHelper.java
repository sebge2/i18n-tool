package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.github.GitHubPullRequestDto;
import be.sgerard.i18n.model.github.GitHubPullRequestStatus;
import be.sgerard.i18n.model.repository.dto.GitHubRepositoryDto;
import be.sgerard.test.i18n.mock.GitHubClientMock;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
@Component
public class GitHubRepositoryMockTestHelper {

    private final GitHubClientMock gitHubClient;

    public GitHubRepositoryMockTestHelper(GitHubClientMock gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    StepRepository forRepository(GitHubRepositoryDto repositoryDto) {
        return new StepRepository(repositoryDto);
    }

    public final class StepRepository {

        private final GitHubRepositoryDto repositoryDto;

        public StepRepository(GitHubRepositoryDto repositoryDto) {
            this.repositoryDto = repositoryDto;
        }

        public StepPullRequest assertHasPullRequest(String repository, String targetBranch) {
            final Optional<GitHubPullRequestDto> actual = gitHubClient
                    .findAllNonBlocking().stream()
                    .filter(request -> Objects.equals(repository, request.getRepositoryName()))
                    .filter(request -> Objects.equals(targetBranch, request.getTargetBranch()))
                    .findFirst();

            assertThat(actual).isNotEmpty();

            return new StepPullRequest(actual.get(), repositoryDto);
        }

        public StepRepository updatePullRequestStatus(String targetBranch, GitHubPullRequestStatus status) {
            gitHubClient.updatePullRequestStatus(repositoryDto.getId(), targetBranch, status);

            return this;
        }

        public StepRepository resetPullRequests() {
            gitHubClient.resetPullRequests();
            return this;
        }
    }

    public final class StepPullRequest {

        private final GitHubPullRequestDto pullRequest;
        private final GitHubRepositoryDto repositoryDto;

        public StepPullRequest(GitHubPullRequestDto pullRequest, GitHubRepositoryDto repositoryDto) {
            this.pullRequest = pullRequest;
            this.repositoryDto = repositoryDto;
        }

        public StepRepository and() {
            return new StepRepository(repositoryDto);
        }

        public GitHubPullRequestDto get() {
            return pullRequest;
        }
    }
}
