package be.sgerard.i18n.service.github;

import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.github.external.BaseGitHubWebHookEventDto;
import be.sgerard.i18n.service.github.external.GitHubBranchDeletedEventDto;
import be.sgerard.i18n.service.workspace.WorkspaceManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * {@link GitHubWebHookEventHandler Event handler} for the {@link GitHubBranchDeletedEventDto deleted branch event}.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitHubBranchDeletedEventHandler implements GitHubWebHookEventHandler<GitHubBranchDeletedEventDto> {

    private final WorkspaceManager workspaceManager;

    public GitHubBranchDeletedEventHandler(WorkspaceManager workspaceManager) {
        this.workspaceManager = workspaceManager;
    }

    @Override
    public boolean support(BaseGitHubWebHookEventDto event) {
        return event instanceof GitHubBranchDeletedEventDto;
    }

    @Override
    public Mono<WorkspaceEntity> handle(GitHubRepositoryEntity repository, GitHubBranchDeletedEventDto event) {
        if (!event.isBranchRelated()) {
            return Mono.empty();
        }

        return workspaceManager
                .findAll(repository.getId())
                .filter(workspace -> Objects.equals(workspace.getBranch(), event.getRef()))
                .last()
                .onErrorResume(NoSuchElementException.class, e -> Mono.empty())
                .flatMap(workspace -> workspaceManager.delete(workspace.getId()));
    }
}
