package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

/**
 * @author Sebastien Gerard
 */
public final class TranslationLocaleEntityTestUtils {

    public static final String ENGLISH_ID = "3b256c34-f3c7-491d-8092-c8ad4105ad25";
    public static final String ENGLISH_GB_ID = "eacb7a46-5a3c-45b0-a383-e4fb69aa09d4";
    public static final String FRANCAIS_ID = "5fc7d302-f8cf-4785-9f8a-74477fabdb10";
    public static final String FRANCAIS_WALLON_ID = "1edf3a9b-7686-4a12-9e93-093cb06dac1d";

    private TranslationLocaleEntityTestUtils() {
    }

    public static TranslationLocaleEntity enLocale() {
        return new TranslationLocaleEntity("en", null, emptySet(), "English", "flag-icon-gb")
                .setId(ENGLISH_ID);
    }

    public static TranslationLocaleEntity enGbLocale() {
        return new TranslationLocaleEntity("en", "GB", emptySet(), "English (GB)", "flag-icon-gb")
                .setId(ENGLISH_GB_ID);
    }

    public static TranslationLocaleEntity frLocale() {
        return new TranslationLocaleEntity("fr", null, emptySet(), "Français", "flag-icon-fr")
                .setId(FRANCAIS_ID);
    }

    public static TranslationLocaleEntity frBeWallonLocale() {
        return new TranslationLocaleEntity("fr", "BE", singleton("wallon"), "Français (Wallon)", "flag-icon-fr")
                .setId(FRANCAIS_WALLON_ID);
    }
}
