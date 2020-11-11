package be.sgerard.i18n.service.repository.github.webhook;

import be.sgerard.i18n.model.repository.github.external.BaseGitHubWebHookEventDto;
import be.sgerard.i18n.model.repository.github.external.GitHubBranchCreatedEventDto;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.workspace.WorkspaceManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * {@link GitHubWebHookEventHandler Event handler} for the {@link GitHubBranchCreatedEventDto created branch event}.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitHubBranchCreatedEventHandler implements GitHubWebHookEventHandler<GitHubBranchCreatedEventDto> {

    private final WorkspaceManager workspaceManager;

    public GitHubBranchCreatedEventHandler(WorkspaceManager workspaceManager) {
        this.workspaceManager = workspaceManager;
    }

    @Override
    public boolean support(BaseGitHubWebHookEventDto event) {
        return event instanceof GitHubBranchCreatedEventDto;
    }

    @Override
    public Mono<WorkspaceEntity> handle(GitHubRepositoryEntity repository, GitHubBranchCreatedEventDto event) {
        if (!event.isBranchRelated()) {
            return Mono.empty();
        }

        return workspaceManager
                .synchronizeAll(repository.getId())
                .filter(workspace -> Objects.equals(workspace.getBranch(), event.getRef()))
                .last()
                .onErrorResume(NoSuchElementException.class, e -> Mono.empty());
    }
}
