package be.sgerard.poc.githuboauth.service;

/**
 * @author Sebastien Gerard
 */
public class LockTimeoutException extends Exception {

    public LockTimeoutException(String message) {
        super(message);
    }
}
