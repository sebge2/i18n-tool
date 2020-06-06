package be.sgerard.i18n.service.github;

import be.sgerard.i18n.model.github.GitHubPullRequestStatus;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link GitHubWebHookCallback GitHub Webhook callback}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeGitHubWebHookCallback implements GitHubWebHookCallback {

    private final List<GitHubWebHookCallback> callbacks;

    public CompositeGitHubWebHookCallback(List<GitHubWebHookCallback> callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public Mono<Void> onPullRequestUpdate(GitHubRepositoryEntity repository, int pullRequestNumber, GitHubPullRequestStatus status) {
        return Flux
                .fromIterable(callbacks)
                .flatMap(callback -> callback.onPullRequestUpdate(repository, pullRequestNumber, status))
                .then();
    }

    @Override
    public Mono<Void> onCreatedBranch(GitHubRepositoryEntity repository, String branch) {
        return Flux
                .fromIterable(callbacks)
                .flatMap(callback -> callback.onCreatedBranch(repository, branch))
                .then();
    }

    @Override
    public Mono<Void> onDeletedBranch(GitHubRepositoryEntity repository, String branch) {
        return Flux
                .fromIterable(callbacks)
                .flatMap(callback -> callback.onDeletedBranch(repository, branch))
                .then();
    }
}
