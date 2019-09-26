package be.sgerard.i18n.service;

/**
 * @author Sebastien Gerard
 */
public class LockTimeoutException extends RuntimeException {

    public LockTimeoutException(String message) {
        super(message);
    }
}
