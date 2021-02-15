package be.sgerard.i18n.service;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.error.LocalizedMessageHolder;

/**
 * Exception thrown when a request is unauthorized.
 *
 * @author Sebastien Gerard
 */
public class UnauthorizedRequestException extends RuntimeException implements LocalizedMessageHolder {

    public static UnauthorizedRequestException invalidSignatureException(String signature) {
        return new UnauthorizedRequestException("The signature [" + signature + "] is invalid.", "UnauthorizedRequestException.invalid-signature.message", signature);
    }

    private final LocalizedString localizedMessage;

    public UnauthorizedRequestException(String message, String messageKey, Object... parameters) {
        super(message);

        this.localizedMessage = LocalizedString.fromBundle("i18n/exception", messageKey, parameters);
    }

    @Override
    public LocalizedString toLocalizedMessage() {
        return localizedMessage;
    }
}
