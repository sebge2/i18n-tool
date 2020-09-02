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
    default Mono<Void> onCreate(WorkspaceEntity workspace) {
        return Mono.empty();
    }

    /**
     * Performs an action after the initialization of the specified workspace.
     */
    default Mono<Void> onInitialize(WorkspaceEntity workspace) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified workspace starts to be in review.
     */
    default Mono<Void> onReview(WorkspaceEntity workspace) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified workspace has been updated. Called before
     * {@link #onInitialize(WorkspaceEntity) on-initialize} and {@link #onReview(WorkspaceEntity) on-review}.
     */
    default Mono<Void> onUpdate(WorkspaceEntity workspace) {
        return Mono.empty();
    }

    /**
     * Performs an action after the deletion of the specified workspace.
     */
    default Mono<Void> onDelete(WorkspaceEntity workspace) {
        return Mono.empty();
    }
}
