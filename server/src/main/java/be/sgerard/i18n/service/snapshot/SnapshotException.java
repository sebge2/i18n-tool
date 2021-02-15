package be.sgerard.i18n.service.snapshot;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.error.LocalizedMessageHolder;

/**
 * Exception thrown when manipulating a snapshot.
 *
 * @author Sebastien Gerard
 */
public class SnapshotException extends RuntimeException implements LocalizedMessageHolder {

    public static SnapshotException onUnzippingExistingSnapshot(Throwable cause) {
        return new SnapshotException("Error while unzipping the snapshot.", "SnapshotException.unzipping-existing-snapshot.message", cause);
    }

    public static SnapshotException onReadingMetadata(Throwable cause) {
        return new SnapshotException("Error while reading the snapshot ZIP file. Are-you sure that the ZIP file is correct? " +
                "and that the password is correct (if needed)?", "SnapshotException.reading-metadata.message", cause);
    }

    private final LocalizedString localizedMessage;

    public SnapshotException(String message, String messageKey, Throwable cause, Object... parameters) {
        super(message, cause);

        this.localizedMessage = LocalizedString.fromBundle("i18n/exception", messageKey, parameters);
    }

    @Override
    public LocalizedString toLocalizedMessage() {
        return localizedMessage;
    }
}
