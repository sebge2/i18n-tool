package be.sgerard.i18n.model.error;

import be.sgerard.i18n.model.core.localized.LocalizedString;

import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Holder of a localized message.
 *
 * @author Sebastien Gerard
 */
@FunctionalInterface
public interface LocalizedMessageHolder extends LocalizedMessagesHolder {

    /**
     * Returns the message.
     */
    LocalizedString toLocalizedMessage();

    @Override
    default List<LocalizedString> toLocalizedMessages() {
        return singletonList(toLocalizedMessage());
    }
}
