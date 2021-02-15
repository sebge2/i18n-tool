package be.sgerard.i18n.model.core.localized;

import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.LookupTranslator;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.singletonMap;
import static java.util.Collections.unmodifiableMap;

/**
 * {@link LocalizedString.Formatter Formatter} of {@link LocalizedString localized strings}.
 *
 * @author Sebastien Gerard
 */
public class LocalizedStringFormatter implements LocalizedString.Formatter {

    public static final LocalizedString.Formatter INSTANCE = new LocalizedStringFormatter();

    /**
     * Escape simple quote to double quote.
     */
    public static final CharSequenceTranslator ESCAPE_TRANSLATION = new AggregateTranslator(
            new LookupTranslator(unmodifiableMap(singletonMap("'${", "\'${"))),
            new LookupTranslator(unmodifiableMap(singletonMap("}'", "}\'"))),
            new LookupTranslator(unmodifiableMap(singletonMap("'", "''")))
    );

    @Override
    public String format(String translation, Locale locale, Object[] arguments) {
        try {
            return MessageFormat.format(
                    ESCAPE_TRANSLATION.translate(translation),
                    postProcessArgs(arguments, localizedString -> localizedString.getTranslationOrFallback(locale, ""))
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Error while formatting [%s] with arguments [%s].", translation, Arrays.toString(arguments)), e);
        }
    }

    /**
     * Processes the specified arguments that can be {@link LocalizedString localized} themselves.
     */
    private Object[] postProcessArgs(Object[] arguments, Function<LocalizedString, String> localizedToTranslation) {
        return Stream.of(arguments)
                .map(argument -> {
                    if (argument instanceof LocalizedString) {
                        return localizedToTranslation.apply((LocalizedString) argument);
                    } else {
                        return argument;
                    }
                })
                .toArray();
    }
}
