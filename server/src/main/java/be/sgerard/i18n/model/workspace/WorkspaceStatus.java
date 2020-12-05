package be.sgerard.i18n.model.workspace;

/**
 * All possible status of a workspace.
 *
 * @author Sebastien Gerard
 */
public enum WorkspaceStatus {

    /**
     * The workspace is not initialized, no translation can be associated to it.
     */
    NOT_INITIALIZED,

    /**
     * The workspace is initialized, translations can be associated to it.
     */
    INITIALIZED,

    /**
     * The workspace is in review waiting for modifications to be approved.
     */
    IN_REVIEW

}
