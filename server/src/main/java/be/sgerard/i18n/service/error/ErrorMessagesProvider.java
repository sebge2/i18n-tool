package be.sgerard.i18n.service.error;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.error.LocalizedMessagesHolder;
import be.sgerard.i18n.model.support.ErrorMessages;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * Provider of {@link ErrorMessages error messages} that are localized and ready to be provided to the end-user.
 *
 * @author Sebastien Gerard
 */
@Service
public class ErrorMessagesProvider {

    public ErrorMessagesProvider() {
    }

    /**
     * Maps the error message to a {@link ErrorMessages localized error message}.
     */
    public ErrorMessages map(LocalizedString message) {
        return map(() -> singletonList(message));
    }

    /**
     * Maps the error messages to {@link ErrorMessages localized error messages}.
     */
    public ErrorMessages map(LocalizedMessagesHolder message) {
        final Locale locale = LocaleContextHolder.getLocale();

        return new ErrorMessages(
                message.toLocalizedMessages().stream()
                        .map(validationMessage -> validationMessage.getTranslationOrFallback(locale, "?"))
                        .collect(toList())
        );
    }
}
