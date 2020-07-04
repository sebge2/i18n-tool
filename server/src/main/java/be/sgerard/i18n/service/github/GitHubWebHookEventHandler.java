package be.sgerard.i18n.service.github;

import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.github.external.BaseGitHubWebHookEventDto;
import reactor.core.publisher.Mono;

/**
 * Handler of GitHub events.
 *
 * @author Sebastien Gerard
 */
public interface GitHubWebHookEventHandler<E extends BaseGitHubWebHookEventDto> {

    /**
     * Checks whether the specified {@link BaseGitHubWebHookEventDto event} is supported.
     */
    boolean support(BaseGitHubWebHookEventDto event);

    /**
     * Handles the specified {@link BaseGitHubWebHookEventDto event}.
     */
    Mono<WorkspaceEntity> handle(GitHubRepositoryEntity repository, E event);
}
