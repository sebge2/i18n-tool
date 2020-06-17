package be.sgerard.i18n.service.client;

import be.sgerard.i18n.model.github.GitHubPullRequestDto;
import be.sgerard.i18n.model.github.GitHubPullRequestStatus;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.service.security.auth.AuthenticationManager;
import be.sgerard.i18n.service.workspace.WorkspaceException;
import com.jcabi.github.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Collections.emptyMap;

/**
 * Implementation of the {@link GitHubClient pull-request manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class GitHubClientImpl implements GitHubClient {

    private final RepositoryManager repositoryManager;
    private final AuthenticationManager authenticationManager;

    public GitHubClientImpl(RepositoryManager repositoryManager, AuthenticationManager authenticationManager) {
        this.repositoryManager = repositoryManager;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Mono<GitHubPullRequestDto> createRequest(String repositoryId, String message, String currentBranch, String targetBranch) throws WorkspaceException {
        return repositoryManager
                .findByIdOrDie(repositoryId, GitHubRepositoryEntity.class)
                .flatMap(repository -> {
                    try {
                        return Mono.just(
                                map(
                                        getGitHubRepo(repository).pulls().create(message, currentBranch, targetBranch)
                                )
                        );
                    } catch (Exception e) {
                        return Mono.error(WorkspaceException.onStartingReview(e));
                    }
                });
    }

    @Override
    public Flux<GitHubPullRequestDto> findAll(String repositoryId) throws WorkspaceException {
        return repositoryManager
                .findByIdOrDie(repositoryId, GitHubRepositoryEntity.class)
                .flatMapMany(this::findAll);
    }

    @Override
    public Flux<GitHubPullRequestDto> findAll() throws WorkspaceException {
        return repositoryManager
                .findAll()
                .filter(GitHubRepositoryEntity.class::isInstance)
                .map(GitHubRepositoryEntity.class::cast)
                .flatMap(this::findAll);
    }

    @Override
    public Mono<GitHubPullRequestDto> findByNumber(String repositoryId, int requestNumber) throws WorkspaceException {
        return repositoryManager
                .findByIdOrDie(repositoryId, GitHubRepositoryEntity.class)
                .flatMap(repository -> {
                    try {
                        return Mono.just(map(getGitHubRepo(repository).pulls().get(requestNumber)));
                    } catch (Exception e) {
                        return Mono.error(WorkspaceException.onFetchingReviewInformation(e));
                    }
                });
    }

    /**
     * Finds all {@link GitHubPullRequestDto pull-requests} of the specified repository.
     */
    private Flux<GitHubPullRequestDto> findAll(GitHubRepositoryEntity repository) {
        return Flux
                .fromIterable(getGitHubRepo(repository).pulls().iterate(emptyMap()))
                .flatMap(pullRequest -> {
                    try {
                        return Mono.just(map(pullRequest));
                    } catch (Exception e) {
                        return Mono.error(WorkspaceException.onFetchingReviewInformation(e));
                    }
                });
    }

    /**
     * Returns the {@link Repo GitHub repository} to use for the API.
     */
    private Repo getGitHubRepo(GitHubRepositoryEntity gitHubRepository) {
        return getGitHubApi().repos().get(new Coordinates.Simple(gitHubRepository.getName()));
    }

    /**
     * Initializes the {@link Github GitHub API}.
     */
    private Github getGitHubApi() {
        // TODO
        return new RtGithub(/*authenticationManager.getCurrentUserOrFail().getGitHubTokenOrFail()*/);
    }

    /**
     * Maps the specified API representation of a pull-request to our representation.
     */
    private GitHubPullRequestDto map(Pull pullRequest) throws Exception {
        return GitHubPullRequestDto.builder()
                .repositoryName(pullRequest.repo().toString())
                .number(pullRequest.number())
                .currentBranch(pullRequest.head().ref())
                .targetBranch(pullRequest.base().ref())
                .status(GitHubPullRequestStatus.fromString(pullRequest.json().getString("state")))
                .build();
    }

}
