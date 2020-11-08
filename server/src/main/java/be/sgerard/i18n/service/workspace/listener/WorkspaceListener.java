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
     * Performs an action after the creation of the specified workspace.
     */
    default Mono<Void> afterCreate(WorkspaceEntity workspace) {
        return Mono.empty();
    }

    /**
     * Performs an action after the initialization of the specified workspace.
     */
    default Mono<Void> afterInitialize(WorkspaceEntity workspace) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified workspace starts to be reviewed. Called before
     * {@link #afterUpdate(WorkspaceEntity) after-update}.
     */
    default Mono<Void> beforeReview(WorkspaceEntity workspace) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified workspace is no more in review. Called before
     * {@link #afterUpdate(WorkspaceEntity) after-update}.
     */
    default Mono<Void> afterReview(WorkspaceEntity workspace) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified workspace has been updated. Called before
     * {@link #afterInitialize(WorkspaceEntity) on-initialize} and {@link #beforeReview(WorkspaceEntity) on-review}.
     */
    default Mono<Void> afterUpdate(WorkspaceEntity workspace) {
        return Mono.empty();
    }

    /**
     * Performs an action after the synchronization of the specified workspace.
     */
    default Mono<Void> afterSynchronization(WorkspaceEntity workspace)  {
        return Mono.empty();
    }

    /**
     * Performs an action before the deletion of the specified workspace.
     */
    default Mono<Void> beforeDelete(WorkspaceEntity workspace) {
        return Mono.empty();
    }  // TODO delete branch
}
