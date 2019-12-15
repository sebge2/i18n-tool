package be.sgerard.i18n.model.validation;

import be.sgerard.i18n.model.support.LocalizedMessageHolder;

/**
 * Validation message. The message is a key and it's used for translating the validation message in the user's locale.
 *
 * @author Sebastien Gerard
 */
public class ValidationMessage implements LocalizedMessageHolder {

    private final String messageKey;
    private final Object[] messageParameters;

    public ValidationMessage(String messageKey, Object... messageParameters) {
        this.messageKey = messageKey;
        this.messageParameters = messageParameters;
    }

    @Override
    public String getMessageKey() {
        return messageKey;
    }

    @Override
    public Object[] getMessageParameters() {
        return messageParameters;
    }
}
