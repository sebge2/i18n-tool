package be.sgerard.i18n.service.workspace.listener;

import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import reactor.core.publisher.Mono;

/**
 * Listener of the lifecycle of {@link WorkspaceEntity workspaces}.
 *
 * @author Sebastien Gerard
 */
public interface WorkspaceListener {

    /**
     * Checks that the specified workspace is supported.
     */
    boolean support(WorkspaceEntity workspace);

    /**
     * Performs an action after the specified workspace has been persisted.
     */
    default Mono<Void> afterPersist(WorkspaceEntity workspace) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified workspace has been updated.
     */
    default Mono<Void> afterUpdate(WorkspaceEntity workspace) {
        return Mono.empty();
    }

    /**
     * Performs an action after the deletion of the specified workspace.
     */
    default Mono<Void> afterDelete(WorkspaceEntity workspace) {
        return Mono.empty();
    }
}
