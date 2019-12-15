package be.sgerard.i18n.service;

/**
 * Exception thrown when the lock cannot be obtained.
 *
 * @see LockService
 * @author Sebastien Gerard
 */
public class LockTimeoutException extends RuntimeException {

    public LockTimeoutException(String message) {
        super(message);
    }
}
