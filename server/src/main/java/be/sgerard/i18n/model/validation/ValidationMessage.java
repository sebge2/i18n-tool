package be.sgerard.i18n.model.validation;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.error.LocalizedMessageHolder;

/**
 * Validation message. The message is a key and it's used for translating the validation message in the user's locale.
 *
 * @author Sebastien Gerard
 */
public class ValidationMessage implements LocalizedMessageHolder {

    private final LocalizedString localizedMessage;

    public ValidationMessage(String messageKey, Object... messageParameters) {
        this.localizedMessage = LocalizedString.fromBundle("i18n/validation", messageKey, messageParameters);
    }

    @Override
    public LocalizedString toLocalizedMessage() {
        return localizedMessage;
    }
}
