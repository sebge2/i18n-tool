package be.sgerard.i18n.service;

/**
 * @author Sebastien Gerard
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
