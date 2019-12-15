package be.sgerard.i18n.service;

import be.sgerard.i18n.model.support.LocalizedMessageHolder;

/**
 * Exception when a request is unauthorized
 *
 * @author Sebastien Gerard
 */
public class UnauthorizedRequestException extends RuntimeException implements LocalizedMessageHolder {

    public static UnauthorizedRequestException invalidSignatureException(String signature) {
        return new UnauthorizedRequestException("The signature [" + signature + "] is invalid.", "UnauthorizedRequestException.invalid-signature.message", signature);
    }

    private final String messageKey;
    private final Object[] parameters;

    public UnauthorizedRequestException(String message, String messageKey, Object... parameters) {
        super(message);

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
