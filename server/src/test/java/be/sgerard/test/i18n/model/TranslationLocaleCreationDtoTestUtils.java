package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleCreationDto;
import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;

import static be.sgerard.test.i18n.model.TranslationLocaleEntityTestUtils.*;

/**
 * @author Sebastien Gerard
 */
public final class TranslationLocaleCreationDtoTestUtils {

    private TranslationLocaleCreationDtoTestUtils() {
    }

    public static TranslationLocaleCreationDto.Builder enLocaleCreationDto() {
        return initBuilder(enLocale());
    }

    public static TranslationLocaleCreationDto.Builder enGbLocaleCreationDto() {
        return initBuilder(enGbLocale());
    }

    public static TranslationLocaleCreationDto.Builder frLocaleCreationDto() {
        return initBuilder(frLocale());
    }

    public static TranslationLocaleCreationDto.Builder frBeWallonLocaleCreationDto() {
        return initBuilder(frBeWallonLocale());
    }

    private static TranslationLocaleCreationDto.Builder initBuilder(TranslationLocaleEntity locale) {
        return TranslationLocaleCreationDto.builder()
                .language(locale.getLanguage())
                .region(locale.getRegion().orElse(null))
                .variant(String.join(",", locale.getVariants()))
                .displayName(locale.getDisplayName().orElse(null))
                .icon(locale.getIcon());
    }
}
