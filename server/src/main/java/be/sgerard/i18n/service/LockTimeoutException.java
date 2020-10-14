package be.sgerard.i18n.service;

import be.sgerard.i18n.model.support.LocalizedMessageHolder;

/**
 * Exception thrown when the lock cannot be obtained.
 *
 * @see LockService
 * @author Sebastien Gerard
 */
public class LockTimeoutException extends RuntimeException implements LocalizedMessageHolder {

    private final int timeoutInMS;

    public LockTimeoutException(int timeoutInMS) {
        super();
        this.timeoutInMS = timeoutInMS;
    }

    @Override
    public String getMessageKey() {
        return "LockTimeoutException.message";
    }

    @Override
    public Object[] getMessageParameters() {
        return new Object[]{timeoutInMS};
    }
}
