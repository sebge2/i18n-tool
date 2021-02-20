package be.sgerard.i18n.model.core.localized.dto;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Locale;
import java.util.Map;

/**
 * DTO for {@link be.sgerard.i18n.model.core.localized.LocalizedString localized strings}.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "LocalizedString", description = "Localized String", type = "object")
@Getter
public class LocalizedStringDto {

    public static LocalizedStringDto fromLocalizedString(LocalizedString localizedString) {
        if (localizedString == null) {
            return null;
        }

        return new LocalizedStringDto(localizedString.getTranslations());
    }

    /**
     * All the available translations per locale.
     */
    private final Map<Locale, String> translations;

    @JsonCreator
    public LocalizedStringDto(Map<Locale, String> translations) {
        this.translations = translations;
    }

    /**
     * @see #translations
     */
    @JsonAnyGetter
    public Map<Locale, String> getTranslations() {
        return translations;
    }
}
