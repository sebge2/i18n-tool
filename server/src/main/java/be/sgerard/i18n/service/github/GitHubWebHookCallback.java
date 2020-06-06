package be.sgerard.i18n.service.github;

import be.sgerard.i18n.model.github.GitHubPullRequestStatus;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import reactor.core.publisher.Mono;

/**
 * Callback when GitHub invoked the Web-hook.
 *
 * @author Sebastien Gerard
 */
public interface GitHubWebHookCallback {

    /**
     * Performs an action when the specified pull-request has been updated.
     */
    default Mono<Void> onPullRequestUpdate(GitHubRepositoryEntity repository, int pullRequestNumber, GitHubPullRequestStatus status) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified branch has been created.
     */
    default Mono<Void> onCreatedBranch(GitHubRepositoryEntity repository, String branch) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified branch has been deleted.
     */
    default Mono<Void> onDeletedBranch(GitHubRepositoryEntity repository, String branch) {
        return Mono.empty();
    }

}
