package be.sgerard.i18n.service.snapshot;

import be.sgerard.i18n.model.support.LocalizedMessageHolder;

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

    private final String messageKey;
    private final Object[] parameters;

    public SnapshotException(String message, String messageKey, Throwable cause, Object... parameters) {
        super(message, cause);

        this.messageKey = messageKey;
        this.parameters = parameters;
    }

    @Override
    public String getMessageKey() {
        return messageKey;
    }

    @Override
    public Object[] getMessageParameters() {
        return parameters;
    }
}
