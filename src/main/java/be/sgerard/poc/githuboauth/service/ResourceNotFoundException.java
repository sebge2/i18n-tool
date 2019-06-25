package be.sgerard.poc.githuboauth.service;

/**
 * @author Sebastien Gerard
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
