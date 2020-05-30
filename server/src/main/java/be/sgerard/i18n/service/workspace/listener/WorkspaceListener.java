package be.sgerard.i18n.service.workspace.listener;

import be.sgerard.i18n.model.workspace.WorkspaceEntity;
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
     * Validates before the workspace is initialized.
     */
    default ValidationResult beforeInitialize(WorkspaceEntity workspace) {
        return ValidationResult.EMPTY;
    }

    /**
     * Performs an action after the initialization of the specified workspace.
     */
    default void onInitialize(WorkspaceEntity workspace) {
    }

    /**
     * Validates that the specified workspace can be published and eventually be in review.
     */
    default ValidationResult beforePublish(WorkspaceEntity workspace){
        return ValidationResult.EMPTY;
    }

    /**
     * Performs an action when the specified workspace starts to be in review.
     */
    default void onReview(WorkspaceEntity workspace){
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
