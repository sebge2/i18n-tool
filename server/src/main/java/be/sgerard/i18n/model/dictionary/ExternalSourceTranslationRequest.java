package be.sgerard.i18n.model.dictionary;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Request asking the translation of a text.
 */
@AllArgsConstructor
@Getter
public class ExternalSourceTranslationRequest {

    /**
     * Template variable name referring to the original locale.
     */
    public static final String FROM_LOCALE_VARIABLE = "fromLocale";

    /**
     * Template variable name referring to the target locale.
     */
    public static final String TARGET_LOCALE_VARIABLE = "targetLocale";

    /**
     * Template variable name referring to the text to translate.
     */
    public static final String TEXT_VARIABLE = "text";

    /**
     * The locale in which the text is written.
     */
    private final TranslationLocaleEntity fromLocale;

    /**
     * The locale of the translation.
     */
    private final TranslationLocaleEntity targetLocale;

    /**
     * The text to translate.
     */
    private final String text;

    /**
     * Returns those field as parameters map.
     */
    public Map<String, String> toTranslatorParameters() {
        final Map<String, String> input = new HashMap<>();

        input.put(FROM_LOCALE_VARIABLE, getFromLocale().toLocale().toString());
        input.put(TARGET_LOCALE_VARIABLE, getTargetLocale().toLocale().toString());
        input.put(TEXT_VARIABLE, getText());

        return input;
    }
}
