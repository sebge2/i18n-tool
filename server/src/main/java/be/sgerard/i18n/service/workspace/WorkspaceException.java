package be.sgerard.i18n.service.workspace;

import be.sgerard.i18n.model.support.LocalizedMessageHolder;

/**
 * Exception thrown when accessing and doing actions on a workspace.
 *
 * @author Sebastien Gerard
 */
public class WorkspaceException extends RuntimeException implements LocalizedMessageHolder {

    private final String messageKey;
    private final Object[] parameters;

    public WorkspaceException(String message, String messageKey, Throwable cause, Object... parameters) {
        super(message, cause);

        this.messageKey = messageKey;
        this.parameters = parameters;
    }

    @Override
    public String getMessageKey() {
        return messageKey;
    }

    @Override
    public Object[] getMessageParameters() {
        return parameters;
    }
}
