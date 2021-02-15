package be.sgerard.i18n.service.workspace;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.error.LocalizedMessageHolder;

import java.io.File;

/**
 * Exception thrown when accessing and doing actions on a workspace.
 *
 * @author Sebastien Gerard
 */
public class WorkspaceException extends RuntimeException implements LocalizedMessageHolder {

    public static WorkspaceException onStartingReview(Throwable cause) {
        return new WorkspaceException("Error while starting the review.", "WorkspaceException.starting-review.message", cause);
    }

    public static WorkspaceException onFetchingReviewInformation(Throwable cause) {
        return new WorkspaceException("Error while fetching information about the current state of the review.", "WorkspaceException.fetching-review-state.message", cause);
    }

    public static WorkspaceException onFileReading(File file, Throwable cause) {
        return new WorkspaceException("Error while reading the content of the file [" + file + "].", "WorkspaceException.error-reading-file.message", cause, file.toPath());
    }

    public static WorkspaceException onFileWriting(File file, Throwable cause) {
        return new WorkspaceException("Error while writing the content of the file [" + file + "].", "WorkspaceException.error-writing-file.message", cause, file.toPath());
    }

    private final LocalizedString localizedMessage;

    public WorkspaceException(String message, String messageKey, Throwable cause, Object... parameters) {
        super(message, cause);

        this.localizedMessage = LocalizedString.fromBundle("i18n/exception", messageKey, parameters);
    }

    @Override
    public LocalizedString toLocalizedMessage() {
        return localizedMessage;
    }
}
