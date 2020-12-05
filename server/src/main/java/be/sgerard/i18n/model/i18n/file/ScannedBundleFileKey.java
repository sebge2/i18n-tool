package be.sgerard.i18n.model.i18n.file;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;

import java.util.Map;

/**
 * Translation key in a {@link ScannedBundleFile translation bundle file}.
 *
 * @author Sebastien Gerard
 */
public class ScannedBundleFileKey {

    private final String key;
    private final Map<TranslationLocaleEntity, String> translations;

    public ScannedBundleFileKey(String key, Map<TranslationLocaleEntity, String> translations) {
        this.key = key;
        this.translations = translations;
    }

    /**
     * Returns the key associated to those translations.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns translations by their locales.
     */
    public Map<TranslationLocaleEntity, String> getTranslations() {
        return translations;
    }

    @Override
    public String toString() {
        return "ScannedBundleFileKey(" + key + ")";
    }
}
