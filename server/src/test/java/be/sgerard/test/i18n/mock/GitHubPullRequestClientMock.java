package be.sgerard.test.i18n.mock;

import be.sgerard.i18n.service.client.GitHubPullRequestClient;
import be.sgerard.i18n.model.github.GitHubPullRequestDto;
import be.sgerard.i18n.model.github.GitHubPullRequestStatus;
import be.sgerard.i18n.service.workspace.WorkspaceException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Sebastien Gerard
 */
@Service
@Primary
public class GitHubPullRequestClientMock implements GitHubPullRequestClient {

    public GitHubPullRequestClientMock() {
    }

    @Override
    public Mono<GitHubPullRequestDto> createRequest(String repositoryId, String message, String currentBranch, String targetBranch) throws WorkspaceException {
        return Mono.just(
                GitHubPullRequestDto.builder()
                        .number(1)
                        .status(GitHubPullRequestStatus.OPEN)
                        .build()
        );
    }

    @Override
    public Flux<GitHubPullRequestDto> findAll(String repositoryId) throws WorkspaceException {
        return null;
    }

    @Override
    public Flux<GitHubPullRequestDto> findAll() throws WorkspaceException {
        return null;
    }

    @Override
    public Mono<GitHubPullRequestDto> findByNumber(String repositoryId, int requestNumber) throws WorkspaceException {
        return null;
    }
}
