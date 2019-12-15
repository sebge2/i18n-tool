package be.sgerard.i18n.service;

import be.sgerard.i18n.model.support.LocalizedMessageHolder;

/**
 * Exception thrown when a resource has not been found with a particular reference.
 *
 * @author Sebastien Gerard
 */
public class ResourceNotFoundException extends RuntimeException implements LocalizedMessageHolder {

    public static ResourceNotFoundException userNotFoundException(String reference) {
        return new ResourceNotFoundException("ResourceNotFoundException.user.message", "user", reference);
    }

    public static ResourceNotFoundException repositoryNotFoundException(String reference) {
        return new ResourceNotFoundException("ResourceNotFoundException.repository.message", "repository", reference);
    }

    public static ResourceNotFoundException workspaceNotFoundException(String reference) {
        return new ResourceNotFoundException("ResourceNotFoundException.workspace.message", "workspace", reference);
    }

    public static ResourceNotFoundException translationLocaleNotFoundException(String reference) {
        return new ResourceNotFoundException("ResourceNotFoundException.translation-locale.message", "translation locale", reference);
    }

    public static ResourceNotFoundException translationNotFoundException(String reference) {
        return new ResourceNotFoundException("ResourceNotFoundException.translation.message", "translation", reference);
    }

    private final String messageKey;
    private final String reference;

    private ResourceNotFoundException(String messageKey, String concept, String reference) {
        super("There is no " + concept + " with reference [" + reference + "].");

        this.messageKey = messageKey;
        this.reference = reference;
    }

    @Override
    public String getMessageKey() {
        return messageKey;
    }

    @Override
    public Object[] getMessageParameters() {
        return new Object[]{getReference()};
    }

    /**
     * Returns the missing reference.
     */
    public String getReference() {
        return reference;
    }
}
