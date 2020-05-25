package be.sgerard.i18n.service.i18n.review;

import be.sgerard.i18n.model.i18n.persistence.WorkspaceEntity;

/**
 * Handles the review of a workspace.
 *
 * @author Sebastien Gerard
 */
public interface WorkspaceReviewHandler {

    /**
     * Returns whether the specified workspace is supported.
     */
    boolean support(WorkspaceEntity workspace);

    /**
     * Returns whether the review of the specified workspace is finished.
     */
    boolean isReviewFinished(WorkspaceEntity workspace);

    /**
     * Returns reviews are supported and need to start once modifications are published.
     */
    boolean isReviewSupported(WorkspaceEntity workspace);
}
