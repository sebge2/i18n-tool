package be.sgerard.i18n.service.workspace.listener;

import be.sgerard.i18n.model.validation.ValidationResult;
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
     * Validates before the workspace is initialized.
     */
    default Mono<ValidationResult> beforeInitialize(WorkspaceEntity workspace) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Performs an action after the initialization of the specified workspace.
     */
    default Mono<Void> onInitialize(WorkspaceEntity workspace) {
        return Mono.empty();
    }

    /**
     * Validates that the specified workspace can be published and eventually be in review.
     */
    default Mono<ValidationResult> beforePublish(WorkspaceEntity workspace) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Performs an action when the specified workspace starts to be in review.
     */
    default Mono<Void> onReview(WorkspaceEntity workspace) {
        return Mono.empty();
    }

    /**
     * Validates that the review on the specified workspace can finish.
     */
    default Mono<ValidationResult> beforeFinishReview(WorkspaceEntity workspace) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Performs an action after the deletion of the specified workspace.
     */
    default Mono<Void> onDelete(WorkspaceEntity workspace) {
        return Mono.empty();
    }
}
