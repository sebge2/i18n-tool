package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.persistence.WorkspaceEntity;

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
     * Performs an action after the deletion of the specified workspace.
     */
    default void onDelete(WorkspaceEntity workspace) {
    }

}
