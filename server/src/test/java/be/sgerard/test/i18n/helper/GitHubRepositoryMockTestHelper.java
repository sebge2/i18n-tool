package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryDto;
import be.sgerard.test.i18n.mock.GitHubClientMock;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
@Component
public class GitHubRepositoryMockTestHelper {

    private final GitHubClientMock pullRequestClient;

    public GitHubRepositoryMockTestHelper(GitHubClientMock pullRequestClient) {
        this.pullRequestClient = pullRequestClient;
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
            assertThat(pullRequestClient.findAll(repositoryDto.getId()).hasElements().block()).isTrue();

            return this;
        }
    }
}
