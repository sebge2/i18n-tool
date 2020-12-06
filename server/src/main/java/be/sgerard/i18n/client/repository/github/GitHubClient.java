package be.sgerard.i18n.client.repository.github;

import be.sgerard.i18n.model.repository.github.GitHubPullRequestCreationInfo;
import be.sgerard.i18n.model.repository.github.GitHubRepositoryId;
import be.sgerard.i18n.model.repository.github.dto.GitHubPullRequestDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Client interacting with GitHub.
 *
 * @author Sebastien Gerard
 */
public interface GitHubClient {

    /**
     * Returns the {@link GitHubRepositoryId ids} of all accessible repositories.
     */
    Flux<GitHubRepositoryId> findAllRepositories(String token);

    /**
     * Returns all the organizations name to which the specified user belongs.
     */
    Flux<String> findAllOrganizations(String token);

    /**
     * Creates a new {@link GitHubPullRequestDto pull request}.
     */
    Mono<GitHubPullRequestDto> createPullRequest(GitHubRepositoryId repositoryId, GitHubPullRequestCreationInfo creationInfo, String token);

    /**
     * Finds all the {@link GitHubPullRequestDto pull-requests} of the specified repository.
     */
    Flux<GitHubPullRequestDto> findAllPullRequests(GitHubRepositoryId repositoryId, String token);

    /**
     * Finds the pull-request having the specified {@link GitHubPullRequestDto#getNumber() number}.
     */
    Mono<GitHubPullRequestDto> findPullRequestByNumber(GitHubRepositoryId repositoryId, int requestNumber, String token);

    /**
     * Checks if the current user is a member (that has read-write access) of the specified repository.
     */
    Mono<Boolean> isRepositoryMember(GitHubRepositoryId repositoryId, String token);
}
