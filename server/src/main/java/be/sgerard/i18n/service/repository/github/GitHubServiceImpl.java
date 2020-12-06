package be.sgerard.i18n.service.repository.github;

import be.sgerard.i18n.client.repository.github.GitHubClient;
import be.sgerard.i18n.model.repository.github.GitHubPullRequestCreationInfo;
import be.sgerard.i18n.model.repository.github.dto.GitHubPullRequestDto;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.security.repository.GitHubRepositoryTokenCredentials;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.service.security.repository.RepositoryCredentialsManager;
import be.sgerard.i18n.service.workspace.WorkspaceException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of the {@link GitHubService GitHub service}.
 *
 * @author Sebastien Gerard
 */
@Service
public class GitHubServiceImpl implements GitHubService {

    private final RepositoryManager repositoryManager;
    private final RepositoryCredentialsManager credentialsManager;
    private final GitHubClient gitHubClient;

    public GitHubServiceImpl(RepositoryManager repositoryManager,
                             RepositoryCredentialsManager credentialsManager,
                             GitHubClient gitHubClient) {
        this.repositoryManager = repositoryManager;
        this.credentialsManager = credentialsManager;
        this.gitHubClient = gitHubClient;
    }

    @Override
    public Mono<GitHubPullRequestDto> createRequest(String repositoryId, String message, String currentBranch, String targetBranch) {
        return repositoryManager
                .findByIdOrDie(repositoryId, GitHubRepositoryEntity.class)
                .flatMap(repository ->
                        getToken(repository)
                                .flatMap(token ->
                                        gitHubClient.createPullRequest(
                                                repository.getGlobalId(),
                                                new GitHubPullRequestCreationInfo(message, currentBranch, targetBranch),
                                                token
                                        )
                                )
                                .switchIfEmpty(Mono.error(RepositoryException.onAccessGitHub(null)))
                                .onErrorResume(error -> Mono.error(WorkspaceException.onStartingReview(error)))
                );
    }

    @Override
    public Flux<GitHubPullRequestDto> findAll(String repositoryId) {
        return repositoryManager
                .findByIdOrDie(repositoryId, GitHubRepositoryEntity.class)
                .flatMapMany(this::findAll);
    }

    @Override
    public Flux<GitHubPullRequestDto> findAll() {
        return repositoryManager
                .findAll()
                .filter(GitHubRepositoryEntity.class::isInstance)
                .map(GitHubRepositoryEntity.class::cast)
                .flatMap(this::findAll);
    }

    @Override
    public Mono<GitHubPullRequestDto> findByNumber(String repositoryId, int requestNumber) {
        return repositoryManager
                .findByIdOrDie(repositoryId, GitHubRepositoryEntity.class)
                .flatMap(repository ->
                        getToken(repository)
                                .flatMap(token ->
                                        gitHubClient.findPullRequestByNumber(repository.getGlobalId(), requestNumber, token)
                                )
                )
                .onErrorResume(error -> Mono.error(WorkspaceException.onFetchingReviewInformation(error)));
    }

    /**
     * Finds all {@link GitHubPullRequestDto pull-requests} of the specified repository.
     */
    private Flux<GitHubPullRequestDto> findAll(GitHubRepositoryEntity repository) {
        return getToken(repository)
                .flatMapMany(token ->
                        gitHubClient.findAllPullRequests(repository.getGlobalId(), token)
                )
                .onErrorResume(error -> Mono.error(RepositoryException.onAccessGitHub(error)));
    }

    /**
     * Returns the token to use to access GitHub (can be empty).
     */
    private Mono<String> getToken(GitHubRepositoryEntity repository) {
        return credentialsManager
                .loadUserCredentials(repository)
                .map(GitHubRepositoryTokenCredentials.class::cast)
                .flatMap(credentials -> Mono.justOrEmpty(credentials.getToken()));
    }
}
