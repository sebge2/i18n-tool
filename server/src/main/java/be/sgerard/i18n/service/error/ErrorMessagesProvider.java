package be.sgerard.i18n.service.error;

import be.sgerard.i18n.model.support.ErrorMessages;
import be.sgerard.i18n.model.support.LocalizedMessageHolder;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * Provider of {@link ErrorMessages error messages} that are localized and ready to be provided to the end-user.
 *
 * @author Sebastien Gerard
 */
@Service
public class ErrorMessagesProvider {

    private final MessageSource messageSource;

    public ErrorMessagesProvider(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Maps the error message to a {@link ErrorMessages localized error message}.
     */
    public ErrorMessages map(LocalizedMessageHolder message) {
        return map(singletonList(message));
    }

    /**
     * Maps the error messages to {@link ErrorMessages localized error messages}.
     */
    public ErrorMessages map(List<? extends LocalizedMessageHolder> messages) {
        return new ErrorMessages(
                messages.stream()
                        .map(validationMessage -> getLocalizedMessage(validationMessage, LocaleContextHolder.getLocale()))
                        .collect(toList())
        );
    }

    /**
     * Returns the message localized in the specified locale. The message is contained in the {@link LocalizedMessageHolder message holder}.
     */
    private String getLocalizedMessage(LocalizedMessageHolder messageHolder, Locale locale) {
        final Object[] messageParameters = Stream.of(messageHolder.getMessageParameters())
                .map(parameter -> {
                    if (parameter instanceof LocalizedMessageHolder) {
                        return getLocalizedMessage((LocalizedMessageHolder) parameter, locale);
                    } else {
                        return parameter;
                    }
                })
                .toArray();


        return messageSource.getMessage(messageHolder.getMessageKey(), messageParameters, locale);
    }

}
