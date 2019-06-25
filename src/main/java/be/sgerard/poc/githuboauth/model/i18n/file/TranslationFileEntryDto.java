package be.sgerard.poc.githuboauth.model.i18n.file;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Sebastien Gerard
 */
public class TranslationFileEntryDto {

    public static TranslationFileEntryDto merge(TranslationFileEntryDto first, TranslationFileEntryDto second) {
        if (first == null) {
            return second;
        } else {
            if (second == null) {
                return first;
            } else {
                final Map<Locale, String> translations = new LinkedHashMap<>(first.getTranslations());
                translations.putAll(second.getTranslations());

                return new TranslationFileEntryDto(first.getKey(), translations);
            }
        }
    }

    private final String key;
    private final Map<Locale, String> translations;

    public TranslationFileEntryDto(String key, Map<Locale, String> translations) {
        this.key = key;
        this.translations = translations;
    }

    public String getKey() {
        return key;
    }

    public Map<Locale, String> getTranslations() {
        return translations;
    }

    @Override
    public String toString() {
        return "TranslationFileEntryDto(" + key + ")";
    }
}
