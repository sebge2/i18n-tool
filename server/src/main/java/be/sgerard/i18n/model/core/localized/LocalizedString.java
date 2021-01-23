package be.sgerard.i18n.model.core.localized;

import be.sgerard.i18n.model.ToolLocale;
import be.sgerard.i18n.model.core.localized.dto.LocalizedStringDto;

import java.util.*;

import static be.sgerard.i18n.support.StringUtils.isNotEmptyString;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toMap;

/**
 * String translated in different locales.
 *
 * @author Sebastien Gerard
 */
public class LocalizedString {

    public static final Locale DEFAULT_LOCALE = ToolLocale.defaultToolLocale().toLocale();

    /**
     * Returns the localized string based on the specified bundle path and associated to the specified property. Translations
     * may have place holders filled with the specified arguments.
     *
     * @see LocalizedStringFormatter
     */
    public static LocalizedString fromBundle(String bundlePath, String property, Object... arguments) {
        return new LocalizedStringResourceBundle(bundlePath)
                .toLocalizedString(property)
                .formatWithDefault(arguments);
    }

    /**
     * Returns the localized string based on its DTO representation.
     */
    public static LocalizedString fromDto(LocalizedStringDto dto) {
        if (dto == null) {
            return null;
        }

        return new LocalizedString(dto.getTranslations());
    }

    private final Map<Locale, String> translations;

    public LocalizedString(Map<Locale, String> translations) {
        this.translations = translations.entrySet().stream()
                .filter(entry -> isNotEmptyString(entry.getValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public LocalizedString() {
        this(new HashMap<>());
    }

    public LocalizedString(Locale locale, String translation) {
        this(singletonMap(locale, translation));
    }

    /**
     * Returns the translation for the specified locale.
     */
    public Optional<String> getTranslation(Locale locale) {
        return Optional.ofNullable(translations.get(locale));
    }

    /**
     * Returns the translation for the specified locale.
     */
    public Optional<String> getTranslation(ToolLocale locale) {
        return getTranslation(locale.toLocale());
    }

    /**
     * Returns all the available translations per locale.
     */
    public Map<Locale, String> getTranslations() {
        return translations;
    }

    /**
     * Returns the translation in the specified {@link Locale locale}. If not present, takes the first available translation.
     */
    public Optional<String> getTranslationOrFallback(Locale locale) {
        return getTranslation(locale)
                .or(() -> getTranslations().values().stream().findFirst());
    }

    /**
     * Returns the translation in the specified {@link Locale locale}. If not present, takes the first available translation, or the specified value if any.
     */
    public String getTranslationOrFallback(Locale locale, String defaultValue) {
        return getTranslationOrFallback(locale)
                .orElse(defaultValue);
    }

    /**
     * Returns all the {@link Locale locales}.
     */
    public Collection<Locale> getLocales() {
        return translations.keySet();
    }

    /**
     * Formats the current localized string using the specified {@link Formatter formatter}.
     */
    public LocalizedString format(Formatter formatter, Object... arguments) {
        return new LocalizedString(
                translations.entrySet().stream()
                        .collect(toMap(
                                Map.Entry::getKey,
                                entry -> formatter.format(entry.getValue(), entry.getKey(), arguments),
                                (var0, var1) -> {
                                    throw new IllegalStateException(String.format("Duplicate key %s.", var0));
                                },
                                LinkedHashMap::new
                        ))
        );
    }

    /**
     * Formats the current localized string using the default {@link Formatter formatter}.
     *
     * @see LocalizedStringFormatter
     */
    public LocalizedString formatWithDefault(Object... arguments) {
        return format(LocalizedStringFormatter.INSTANCE, arguments);
    }

    /**
     * Formatter of translations.
     */
    interface Formatter {

        /**
         * Formats the specified translation in the specified locale.
         */
        String format(String translation, Locale locale, Object[] arguments);

    }
}
