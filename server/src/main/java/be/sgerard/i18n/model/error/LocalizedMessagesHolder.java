package be.sgerard.i18n.model.error;

import be.sgerard.i18n.model.core.localized.LocalizedString;

import java.util.List;

/**
 * Holder of a localized messages.
 *
 * @author Sebastien Gerard
 */
@FunctionalInterface
public interface LocalizedMessagesHolder {

    /**
     * Returns the messages.
     */
    List<LocalizedString> toLocalizedMessages();
}
