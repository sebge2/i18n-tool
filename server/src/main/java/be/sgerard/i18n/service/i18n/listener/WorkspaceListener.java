package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.persistence.WorkspaceEntity;
import be.sgerard.i18n.model.validation.ValidationResult;

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
    default void onCreate(WorkspaceEntity workspace) {
    }

    /**
     * Validates that the review on the specified workspace can finish.
     */
    default ValidationResult beforeFinishReview(WorkspaceEntity workspace) {
        return ValidationResult.EMPTY;
    }

    /**
     * Performs an action after the deletion of the specified workspace.
     */
    default void onDelete(WorkspaceEntity workspace) {
    }

}
