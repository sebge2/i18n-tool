package be.sgerard.i18n.model;

import java.util.Locale;

/**
 * All locales supported by this tool.
 *
 * @author Sebastien Gerard
 */
public enum ToolLocale {

    ENGLISH(Locale.ENGLISH),

    FRENCH(Locale.FRENCH);

    private final Locale locale;

    ToolLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Returns the associated {@link Locale locale}.
     */
    public Locale toLocale() {
        return locale;
    }
}
