package be.sgerard.i18n.model;

import java.util.Locale;
import java.util.stream.Stream;

/**
 * All locales supported by this tool.
 *
 * @author Sebastien Gerard
 */
public enum ToolLocale {

    ENGLISH(Locale.ENGLISH, true),

    FRENCH(Locale.FRENCH, false);

    /**
     * Returns the default locale to use.
     */
    public static ToolLocale defaultToolLocale(){
        return Stream.of(values())
                .filter(ToolLocale::isDefaultLocale)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("There is no default locale set."));
    }

    private final Locale locale;
    private final boolean defaultLocale;

    ToolLocale(Locale locale, boolean defaultLocale) {
        this.locale = locale;
        this.defaultLocale = defaultLocale;
    }

    /**
     * Returns the associated {@link Locale locale}.
     */
    public Locale toLocale() {
        return locale;
    }

    /**
     * Returns whether this locale is the default one to use.
     */
    public boolean isDefaultLocale() {
        return defaultLocale;
    }
}
