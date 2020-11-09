package be.sgerard.i18n.service.repository.listener;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.service.workspace.WorkspaceManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link RepositoryListener Repository listener} initializing workspaces when the repository has been initialized.
 *
 * @author Sebastien Gerard
 */
@Component
public class WorkspaceRepositoryListener implements RepositoryListener<RepositoryEntity> {

    private final WorkspaceManager workspaceManager;

    public WorkspaceRepositoryListener(WorkspaceManager workspaceManager) {
        this.workspaceManager = workspaceManager;
    }

    @Override
    public boolean support(RepositoryEntity repositoryEntity) {
        return true;
    }

    @Override
    public Mono<Void> afterInitialize(RepositoryEntity repository) {
        return workspaceManager
                .synchronizeAll(repository.getId())
                .then();
    }

    @Override
    public Mono<Void> beforeDelete(RepositoryEntity repository) {
        return workspaceManager
                .findAll(repository.getId())
                .flatMap(
                        workspace -> workspaceManager.delete(workspace.getId()),
                        1,
                        1
                )
                .then();
    }
}
