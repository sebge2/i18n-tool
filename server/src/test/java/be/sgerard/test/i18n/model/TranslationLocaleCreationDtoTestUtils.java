package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleCreationDto;

/**
 * @author Sebastien Gerard
 */
public final class TranslationLocaleCreationDtoTestUtils {

    private TranslationLocaleCreationDtoTestUtils() {
    }

    public static TranslationLocaleCreationDto.Builder enLocaleCreationDto() {
        return TranslationLocaleCreationDto.builder()
                .language("en")
                .displayName("English")
                .icon("flag-icon-gb");
    }

    public static TranslationLocaleCreationDto.Builder enGbLocaleCreationDto() {
        return TranslationLocaleCreationDto.builder()
                .language("en")
                .region("GB")
                .displayName("English (GB)")
                .icon("flag-icon-gb");
    }

    public static TranslationLocaleCreationDto.Builder frLocaleCreationDto() {
        return TranslationLocaleCreationDto.builder()
                .language("fr")
                .displayName("Français")
                .icon("flag-icon-fr");
    }

    public static TranslationLocaleCreationDto.Builder frBeWallonLocaleCreationDto() {
        return TranslationLocaleCreationDto.builder()
                .language("fr")
                .region("BE")
                .variant("wallon")
                .displayName("Français (Wallon)")
                .icon("flag-icon-fr");
    }
}
