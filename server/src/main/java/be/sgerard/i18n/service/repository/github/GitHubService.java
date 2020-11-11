package be.sgerard.i18n.service.repository.github;

import be.sgerard.i18n.model.repository.github.dto.GitHubPullRequestDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service interacting with GitHub.
 *
 * @author Sebastien Gerard
 */
public interface GitHubService {

    /**
     * Creates a new {@link GitHubPullRequestDto pull request}.
     */
    Mono<GitHubPullRequestDto> createRequest(String repositoryId, String message, String currentBranch, String targetBranch);

    /**
     * Finds all the {@link GitHubPullRequestDto pull-requests} of the specified repository.
     */
    Flux<GitHubPullRequestDto> findAll(String repositoryId);

    /**
     * Finds all the {@link GitHubPullRequestDto pull-requests} for all repositories.
     */
    Flux<GitHubPullRequestDto> findAll();

    /**
     * Finds the pull-request having the specified {@link GitHubPullRequestDto#getNumber() number}.
     */
    Mono<GitHubPullRequestDto> findByNumber(String repositoryId, int requestNumber);

}
