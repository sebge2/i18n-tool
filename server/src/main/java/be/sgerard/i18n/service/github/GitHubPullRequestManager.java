package be.sgerard.i18n.service.github;

import be.sgerard.i18n.model.github.GitHubPullRequestDto;
import be.sgerard.i18n.service.workspace.WorkspaceException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Manager of GitHub pull-requests.
 *
 * @author Sebastien Gerard
 */
public interface GitHubPullRequestManager {

    /**
     * Creates a new {@link GitHubPullRequestDto pull request}.
     */
    Mono<GitHubPullRequestDto> createRequest(String repositoryId, String message, String currentBranch, String targetBranch) throws WorkspaceException;

    /**
     * Finds all the {@link GitHubPullRequestDto pull-requests} of the specified repository.
     */
    Flux<GitHubPullRequestDto> findAll(String repositoryId) throws WorkspaceException;

    /**
     * Finds all the {@link GitHubPullRequestDto pull-requests} for all repositories.
     */
    Flux<GitHubPullRequestDto> findAll() throws WorkspaceException;

    /**
     * Finds the pull-request having the specified {@link GitHubPullRequestDto#getNumber() number}.
     */
    Mono<GitHubPullRequestDto> findByNumber(String repositoryId, int requestNumber) throws WorkspaceException;

}
