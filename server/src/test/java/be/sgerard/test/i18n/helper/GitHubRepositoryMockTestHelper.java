package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.github.GitHubPullRequestStatus;
import be.sgerard.i18n.model.repository.dto.GitHubRepositoryDto;
import be.sgerard.test.i18n.mock.GitHubClientMock;
import org.springframework.stereotype.Component;

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

        public StepRepository assertHasPullRequests() {
            assertThat(gitHubClient.findAll(repositoryDto.getId()).hasElements().block()).isTrue();

            return this;
        }

        public StepRepository updatePullRequestStatus(String targetBranch, GitHubPullRequestStatus status) {
            gitHubClient.updatePullRequestStatus(repositoryDto.getId(), targetBranch, status);

            return this;
        }
    }
}
