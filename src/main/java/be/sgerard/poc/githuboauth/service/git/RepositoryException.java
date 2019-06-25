package be.sgerard.poc.githuboauth.service.git;

/**
 * @author Sebastien Gerard
 */
public class RepositoryException extends Exception {

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
