package be.sgerard.i18n.service;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.error.LocalizedMessageHolder;

/**
 * Exception thrown when the lock cannot be obtained.
 *
 * @author Sebastien Gerard
 * @see LockService
 */
public class LockTimeoutException extends RuntimeException implements LocalizedMessageHolder {

    private final LocalizedString localizedMessage;

    public LockTimeoutException(int timeoutInMS) {
        super();

        this.localizedMessage = LocalizedString.fromBundle("i18n/exception", "LockTimeoutException.message", timeoutInMS);
    }

    @Override
    public LocalizedString toLocalizedMessage() {
        return localizedMessage;
    }
}
