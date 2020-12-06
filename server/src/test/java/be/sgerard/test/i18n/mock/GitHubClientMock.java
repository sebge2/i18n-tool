package be.sgerard.test.i18n.mock;

import be.sgerard.i18n.client.repository.github.GitHubClient;
import be.sgerard.i18n.model.repository.github.GitHubPullRequestCreationInfo;
import be.sgerard.i18n.model.repository.github.GitHubRepositoryId;
import be.sgerard.i18n.model.repository.github.dto.GitHubPullRequestDto;
import be.sgerard.test.i18n.mock.repository.github.GitHubMock;
import be.sgerard.test.i18n.mock.repository.github.RemoteGitHubRepositoryMock;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Mock implementation of the {@link GitHubClient GitHub client}.
 *
 * @author Sebastien Gerard
 */
@Service
@Primary
@SuppressWarnings("unused")
public class GitHubClientMock implements GitHubClient {

    private final GitHubMock gitHubMock;

    public GitHubClientMock(GitHubMock gitHubMock) {
        this.gitHubMock = gitHubMock;
    }

    @Override
    public Flux<GitHubRepositoryId> findAllRepositories(String token) {
        return gitHubMock
                .findAllRepositories(token)
                .map(RemoteGitHubRepositoryMock::getRepositoryId);
    }

    @Override
    public Flux<String> findAllOrganizations(String token) {
        return gitHubMock
                .findAllRepositories(token)
                .map(RemoteGitHubRepositoryMock::getRepositoryId)
                .map(GitHubRepositoryId::getUsername)
                .distinct();
    }

    @Override
    public Mono<GitHubPullRequestDto> createPullRequest(GitHubRepositoryId repositoryId, GitHubPullRequestCreationInfo creationInfo, String token) {
        return gitHubMock
                .findRepository(repositoryId, token)
                .map(repository -> repository.createPullRequest(creationInfo))
                .map(RemoteGitHubRepositoryMock.PullRequest::toDto);
    }

    @Override
    public Flux<GitHubPullRequestDto> findAllPullRequests(GitHubRepositoryId repositoryId, String token) {
        return gitHubMock
                .findRepository(repositoryId, token)
                .flatMapMany(rep -> Flux.fromIterable(rep.getPullRequests()))
                .map(RemoteGitHubRepositoryMock.PullRequest::toDto);
    }

    @Override
    public Mono<GitHubPullRequestDto> findPullRequestByNumber(GitHubRepositoryId repositoryId, int requestNumber, String token) {
        return gitHubMock
                .findRepository(repositoryId, token)
                .flatMap(repository -> Mono.justOrEmpty(repository.findPullRequestByNumber(requestNumber)))
                .map(RemoteGitHubRepositoryMock.PullRequest::toDto);
    }

    @Override
    public Mono<Boolean> isRepositoryMember(GitHubRepositoryId repositoryId, String token) {
        return gitHubMock
                .findRepository(repositoryId, token)
                .thenReturn(true)
                .onErrorResume(error -> Mono.just(false));
    }
}
