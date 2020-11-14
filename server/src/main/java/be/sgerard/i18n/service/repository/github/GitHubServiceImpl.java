package be.sgerard.i18n.service.repository.github;

import be.sgerard.i18n.client.repository.github.GitHubClient;
import be.sgerard.i18n.model.repository.github.GitHubPullRequestCreationInfo;
import be.sgerard.i18n.model.repository.github.dto.GitHubPullRequestDto;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.security.auth.GitHubRepositoryTokenCredentials;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
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
    private final GitHubClient gitHubClient;
    private final AuthenticationUserManager authenticationUserManager;

    public GitHubServiceImpl(RepositoryManager repositoryManager,
                             GitHubClient gitHubClient,
                             AuthenticationUserManager authenticationUserManager) {
        this.repositoryManager = repositoryManager;
        this.gitHubClient = gitHubClient;
        this.authenticationUserManager = authenticationUserManager;
    }

    @Override
    public Mono<GitHubPullRequestDto> createRequest(String repositoryId, String message, String currentBranch, String targetBranch) {
        return repositoryManager
                .findByIdOrDie(repositoryId, GitHubRepositoryEntity.class)
                .flatMap(repository ->
                        getToken(repository)
                                .flatMap(token ->
                                        gitHubClient.createRequest(
                                                repository.getCompositeId(),
                                                new GitHubPullRequestCreationInfo(message, currentBranch, targetBranch),
                                                token
                                        )
                                )
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
                                        gitHubClient.findByNumber(repository.getCompositeId(), requestNumber, token)
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
                        gitHubClient.findAll(repository.getCompositeId(), token)
                )
                .onErrorResume(error -> Mono.error(RepositoryException.onAccessGitHub(error)));
    }

    /**
     * Returns the token to use to access GitHub (can be empty).
     */
    private Mono<String> getToken(GitHubRepositoryEntity repository) {
        return authenticationUserManager
                .getCurrentUserOrDie()
                .map(authenticatedUser ->
                        authenticatedUser
                                .getCredentials(repository.getId(), GitHubRepositoryTokenCredentials.class)
                                .map(GitHubRepositoryTokenCredentials::getToken)
                                .orElse(null)
                );
    }
}
