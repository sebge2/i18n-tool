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

    public static ResourceNotFoundException bundleFileNotFoundException(String reference) {
        return new ResourceNotFoundException("ResourceNotFoundException.bundle-file.message", "bundle-file", reference);
    }

    public static ResourceNotFoundException userLiveSessionNotFoundException(String reference) {
        return new ResourceNotFoundException("ResourceNotFoundException.user-live-session.message", "user live session", reference);
    }

    public static ResourceNotFoundException authenticatedUserNotFoundException(String reference) {
        return new ResourceNotFoundException("ResourceNotFoundException.authenticated-user.message", "authenticated user", reference);
    }

    public static ResourceNotFoundException snapshotNotFoundException(String reference) {
        return new ResourceNotFoundException("ResourceNotFoundException.snapshot-user.message", "snapshot", reference);
    }

    public static ResourceNotFoundException scheduledTaskDefinitionNotFoundException(String reference) {
        return new ResourceNotFoundException("ResourceNotFoundException.scheduled-task-definition.message", "scheduled task definition", reference);
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
